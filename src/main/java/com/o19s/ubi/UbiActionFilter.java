/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.o19s.ubi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.ActionFilter;
import org.elasticsearch.action.support.ActionFilterChain;
import org.elasticsearch.client.internal.Client;
import org.elasticsearch.cluster.metadata.IndexMetadata;
import org.elasticsearch.common.io.Streams;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.tasks.Task;
import com.o19s.ubi.ext.UbiParameters;
import org.elasticsearch.xcontent.XContentType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * An implementation of {@link ActionFilter} that listens for
 * queries and persists the queries to the UBI store.
 */
public class UbiActionFilter implements ActionFilter {

    private static final Logger LOGGER = LogManager.getLogger(UbiActionFilter.class);

    private static final String UBI_QUERIES_INDEX = "ubi_queries";
    private static final String UBI_EVENTS_INDEX = "ubi_events";

    private static final String EVENTS_MAPPING_FILE = "/events-mapping.json";
    private static final String QUERIES_MAPPING_FILE = "/queries-mapping.json";

    private final Client client;
    /**
     * Creates a new filter.
     * @param client A {@link Client}.
     */
    public UbiActionFilter(Client client) {
        this.client = client;
    }

    @Override
    public int order() {
        return Integer.MAX_VALUE;
    }

    @Override
    public <Request extends ActionRequest, Response extends ActionResponse> void apply(
        Task task,
        String action,
        Request request,
        ActionListener<Response> listener,
        ActionFilterChain<Request, Response> chain
    ) {

        if (!(request instanceof SearchRequest || request instanceof MultiSearchRequest)) {
            chain.proceed(task, action, request, listener);
            return;
        }

        chain.proceed(task, action, request, new ActionListener<>() {

            @Override
            public void onResponse(Response response) {

                if (request instanceof MultiSearchRequest) {

                    final MultiSearchRequest multiSearchRequest = (MultiSearchRequest) request;

                    for(final SearchRequest searchRequest : multiSearchRequest.requests()) {
                        handleSearchRequest(searchRequest, response);
                    }

                }

                if(request instanceof SearchRequest) {
                    response = (Response) handleSearchRequest((SearchRequest) request, response);
                }

                listener.onResponse(response);

            }

            @Override
            public void onFailure(Exception ex) {
                listener.onFailure(ex);
            }

        });

    }

    private ActionResponse handleSearchRequest(final SearchRequest searchRequest, ActionResponse response) {

        if (response instanceof SearchResponse) {

            final UbiParameters ubiParameters = UbiParameters.getUbiParameters(searchRequest);

            if (ubiParameters != null) {

                final String queryId = ubiParameters.getQueryId();
                final String userQuery = ubiParameters.getUserQuery();
                final String userId = ubiParameters.getClientId();
                final String objectIdField = ubiParameters.getObjectIdField();
                final Map<String, String> queryAttributes = ubiParameters.getQueryAttributes();
                
                final String query = searchRequest.source().toString();

                final List<String> queryResponseHitIds = new LinkedList<>();

                for (final SearchHit hit : ((SearchResponse) response).getHits()) {

                    if (objectIdField == null || objectIdField.isEmpty()) {
                        // Use the result's docId since no object_id was given for the search.
                        queryResponseHitIds.add(String.valueOf(hit.docId()));
                    } else {
                        final Map<String, Object> source = hit.getSourceAsMap();
                        queryResponseHitIds.add((String) source.get(objectIdField));
                    }

                }

                final String queryResponseId = UUID.randomUUID().toString();
                final QueryResponse queryResponse = new QueryResponse(queryId, queryResponseId, queryResponseHitIds);
                final QueryRequest queryRequest = new QueryRequest(queryId, userQuery, userId, query, queryAttributes, queryResponse);

                indexQuery(queryRequest);

                final SearchResponse searchResponse = (SearchResponse) response;

                response = new UbiSearchResponse(
                        searchResponse.getHits(),
                        searchResponse.getAggregations(),
                        searchResponse.getSuggest(),
                        searchResponse.isTimedOut(),
                        searchResponse.isTerminatedEarly(),
                        null,            // TODO: How to get the value for `profileResults`?
                        searchResponse.getNumReducePhases(),
                        searchResponse.getScrollId(),
                        searchResponse.getTotalShards(),
                        searchResponse.getSuccessfulShards(),
                        searchResponse.getSkippedShards(),
                        searchResponse.getTookInMillis(),
                        searchResponse.getShardFailures(),
                        searchResponse.getClusters(),
                        queryId
                );

            }

        }

        return response;

    }

    private void createIndexes(final Client client) {

        final Settings indexSettings = Settings.builder()
                .put(IndexMetadata.INDEX_NUMBER_OF_SHARDS_SETTING.getKey(), 1)
                .put(IndexMetadata.INDEX_AUTO_EXPAND_REPLICAS_SETTING.getKey(), "0-2")
                .put(IndexMetadata.SETTING_PRIORITY, Integer.MAX_VALUE)
                .build();

        final CreateIndexRequest createQueriesIndex = new CreateIndexRequest(UBI_QUERIES_INDEX, indexSettings);
        createQueriesIndex.mapping(getResourceFile(QUERIES_MAPPING_FILE));
        client.admin().indices().create(createQueriesIndex);

        final CreateIndexRequest createEventsIndex = new CreateIndexRequest(UBI_EVENTS_INDEX, indexSettings);
        createEventsIndex.mapping(getResourceFile(EVENTS_MAPPING_FILE));
        client.admin().indices().create(createEventsIndex);

    }

    private String getResourceFile(final String fileName) {
        try (InputStream is = UbiActionFilter.class.getResourceAsStream(fileName)) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Streams.copy(is.readAllBytes(), out);
            return out.toString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to get mapping from resource [" + fileName + "]", e);
        }
    }

    private void indexQuery(final QueryRequest queryRequest) {

        LOGGER.debug(
            "Indexing query ID {} with response ID {}",
            queryRequest.getQueryId(),
            queryRequest.getQueryResponse().getQueryResponseId()
        );

        // What will be indexed - adheres to the queries-mapping.json
        final Map<String, Object> source = new HashMap<>();
        source.put("timestamp", queryRequest.getTimestamp());
        source.put("query_id", queryRequest.getQueryId());
        source.put("query_response_id", queryRequest.getQueryResponse().getQueryResponseId());
        source.put("query_response_object_ids", queryRequest.getQueryResponse().getQueryResponseObjectIds());
        source.put("client_id", queryRequest.getClientId());
        source.put("user_query", queryRequest.getUserQuery());
        source.put("query_attributes", queryRequest.getQueryAttributes());

        // The query can be null for some types of queries.
        if(queryRequest.getQuery() != null) {
            source.put("query", queryRequest.getQuery());
        }

        // Build the index request.
        final IndexRequest indexRequest = new IndexRequest(UBI_QUERIES_INDEX).source(source, XContentType.JSON);

        client.index(indexRequest, new ActionListener<>() {

            @Override
            public void onResponse(DocWriteResponse docWriteResponse) {
                // Nothing needed.
            }

            @Override
            public void onFailure(Exception e) {
                LOGGER.error("Unable to index query into UBI index.", e);
            }

        });

    }

}
