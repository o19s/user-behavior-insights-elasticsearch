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

import java.util.List;

/**
 * A query response.
 */
public class QueryResponse {

    private final String queryId;
    private final String queryResponseId;
    private final List<String> queryResponseObjectIds;

    /**
     * Creates a query response.
     * @param queryId The ID of the query.
     * @param queryResponseId The ID of the query response.
     * @param queryResponseObjectIds A list of IDs for the hits in the query.
     */
    public QueryResponse(final String queryId, final String queryResponseId, final List<String> queryResponseObjectIds) {
        this.queryId = queryId;
        this.queryResponseId = queryResponseId;
        this.queryResponseObjectIds = queryResponseObjectIds;
    }

    /**
     * Gets the query ID.
     * @return The query ID.
     */
    public String getQueryId() {
        return queryId;
    }

    /**
     * Gets the query response ID.
     * @return The query response ID.
     */
    public String getQueryResponseId() {
        return queryResponseId;
    }

    /**
     * Gets the list of query response hit IDs.
     * @return A list of query response hit IDs.
     */
    public List<String> getQueryResponseObjectIds() {
        return queryResponseObjectIds;
    }

}
