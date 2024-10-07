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

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * A received query.
 */
public class QueryRequest {

    private final long timestamp;
    private final String queryId;
    private final String clientId;
    private final String userQuery;
    private final String query;
    private final Map<String, String> queryAttributes;
    private final QueryResponse queryResponse;

    /**
     * Creates a query request.
     * @param queryId The ID of the query.
     * @param userQuery The user-entered query.
     * @param clientId The ID of the client that initiated the query.
     * @param query The raw query.
     * @param queryAttributes An optional map of additional attributes for the query.
     * @param queryResponse The {@link QueryResponse} for this query request.
     */
    public QueryRequest(final String queryId, final String userQuery, final String clientId, final String query,
                        final Map<String, String> queryAttributes, final QueryResponse queryResponse) {

        this.timestamp = System.currentTimeMillis();
        this.queryId = queryId;
        this.clientId = clientId;
        this.userQuery = userQuery;
        this.query = query;
        this.queryAttributes = queryAttributes;
        this.queryResponse = queryResponse;

    }

    @Override
    public String toString() {

        try {

            final ObjectMapper objectMapper = new ObjectMapper();

            final String json = objectMapper.writeValueAsString(this);

            return "[" + json + "]";

        } catch (Exception ex) {
            // TODO: Handle
            return "";
        }

    }

    /**
     * Gets the query attributes.
     * @return The query attributes.
     */
    public Map<String, String> getQueryAttributes() {
        return queryAttributes;
    }

    /**
     * Gets the timestamp.
     * @return The timestamp.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the query ID.
     * @return The query ID.
     */
    public String getQueryId() {
        return queryId;
    }

    /**
     * Gets the user query.
     * @return The user query.
     */
    public String getUserQuery() {
        if(userQuery == null) {
            return "";
        }
        return userQuery;
    }

    /**
     * Gets the client ID.
     * @return The client ID.
     */
    public String getClientId() {
        if(clientId == null) {
            return "";
        }
        return clientId;
    }

    /**
     * Gets the raw query.
     * @return The raw query.
     */
    public String getQuery() {
        if(query == null) {
            return "";
        }
        return query;
    }

    /**
     * Gets the query response for this query request.
     * @return The {@link QueryResponse} for this query request.
     */
    public QueryResponse getQueryResponse() {
        return queryResponse;
    }

}
