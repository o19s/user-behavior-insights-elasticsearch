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
package com.o19s.ubi.ext;

import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.search.SearchExtBuilder;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentParser;

import java.io.IOException;
import java.util.Objects;

/**
 * Subclass of {@link SearchExtBuilder} to access UBI parameters.
 */
public class UbiParametersExtBuilder extends SearchExtBuilder {

    /**
     * The name of the "ext" section containing UBI parameters.
     */
    public static final String UBI_PARAMETER_NAME = "ubi";

    private UbiParameters params;

    /**
     * Creates a new instance.
     */
    public UbiParametersExtBuilder() {}

    /**
     * Creates a new instance from a {@link StreamInput}.
     * @param input A {@link StreamInput} containing the parameters.
     * @throws IOException Thrown if the stream cannot be read.
     */
    public UbiParametersExtBuilder(StreamInput input) throws IOException {
        this.params = new UbiParameters(input);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getClass(), this.params);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof UbiParametersExtBuilder)) {
            return false;
        }

        return this.params.equals(((UbiParametersExtBuilder) obj).getParams());
    }

    @Override
    public String getWriteableName() {
        return UBI_PARAMETER_NAME;
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        this.params.writeTo(out);
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        return builder.value(this.params);
    }

    /**
     * Parses the ubi section of the ext block.
     * @param parser A {@link XContentParser parser}.
     * @return The {@link UbiParameters paramers}.
     * @throws IOException Thrown if the UBI parameters cannot be read.
     */
    public static UbiParametersExtBuilder parse(XContentParser parser) throws IOException {
        final UbiParametersExtBuilder builder = new UbiParametersExtBuilder();
        builder.setParams(UbiParameters.parse(parser));
        return builder;
    }

    /**
     * Gets the {@link UbiParameters params}.
     * @return The {@link UbiParameters params}.
     */
    public UbiParameters getParams() {
        return params;
    }

    /**
     * Set the {@link UbiParameters params}.
     * @param params The {@link UbiParameters params}.
     */
    public void setParams(UbiParameters params) {
        this.params = params;
    }

}
