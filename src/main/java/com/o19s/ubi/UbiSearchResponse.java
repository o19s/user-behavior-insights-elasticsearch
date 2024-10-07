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

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.ShardSearchFailure;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.InternalAggregations;
import org.elasticsearch.search.profile.SearchProfileResults;
import org.elasticsearch.search.suggest.Suggest;
import com.o19s.ubi.ext.UbiParametersExtBuilder;
import org.elasticsearch.xcontent.ToXContent;
import org.elasticsearch.xcontent.XContentBuilder;

import java.io.IOException;

/**
 * A UBI search response.
 */
public class UbiSearchResponse extends SearchResponse {

    private static final String EXT_SECTION_NAME = "ext";
    private static final String UBI_QUERY_ID_FIELD_NAME = "query_id";

    private final String queryId;

    public UbiSearchResponse(
            SearchHits hits,
            InternalAggregations aggregations,
            Suggest suggest,
            boolean timedOut,
            Boolean terminatedEarly,
            SearchProfileResults profileResults,
            int numReducePhases,
            String scrollId,
            int totalShards,
            int successfulShards,
            int skippedShards,
            long tookInMillis,
            ShardSearchFailure[] shardFailures,
            Clusters clusters,
            String queryId
    ) {
        super(hits, aggregations, suggest, timedOut, terminatedEarly, profileResults, numReducePhases, scrollId, totalShards, successfulShards, skippedShards, tookInMillis, shardFailures, clusters);
        this.queryId = queryId;
    }

    @Override
    public XContentBuilder headerToXContent(XContentBuilder builder, ToXContent.Params params) throws IOException {

        //builder.startObject();
        innerToXContentChunked(params);

        builder.startObject(EXT_SECTION_NAME);
        builder.startObject(UbiParametersExtBuilder.UBI_PARAMETER_NAME);
        builder.field(UBI_QUERY_ID_FIELD_NAME, this.queryId);
        builder.endObject();
        builder.endObject();
        //builder.endObject();

        return builder;

    }

}