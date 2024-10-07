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

import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.test.ESTestCase;
import com.o19s.ubi.ext.UbiParameters;
import com.o19s.ubi.ext.UbiParametersExtBuilder;
import org.elasticsearch.xcontent.XContentParser;
import org.elasticsearch.xcontent.XContentType;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UbiParametersExtBuilderTests extends ESTestCase {

    public void testCtor() {

        final Map<String, String> queryAttributes = new HashMap<>();

        final UbiParametersExtBuilder builder = new UbiParametersExtBuilder();
        final UbiParameters parameters = new UbiParameters("query_id", "user_query", "client_id", "object_id_field", queryAttributes);
        builder.setParams(parameters);
        assertEquals(parameters, builder.getParams());

    }

    public void testParse() throws IOException {
        XContentParser xcParser = mock(XContentParser.class);
        when(xcParser.nextToken()).thenReturn(XContentParser.Token.START_OBJECT).thenReturn(XContentParser.Token.END_OBJECT);
        UbiParametersExtBuilder builder = UbiParametersExtBuilder.parse(xcParser);
        assertNotNull(builder);
        assertNotNull(builder.getParams());
    }

    public void testXContentRoundTrip() throws IOException {
        UbiParameters param1 = new UbiParameters("query_id", "user_query", "client_id", "object_id_field", Collections.emptyMap());
        UbiParametersExtBuilder extBuilder = new UbiParametersExtBuilder();
        extBuilder.setParams(param1);
        XContentType xContentType = randomFrom(XContentType.values());
        BytesReference serialized = XContentHelper.toXContent(extBuilder, xContentType, true);
        XContentParser parser = createParser(xContentType.xContent(), serialized);
        UbiParametersExtBuilder deserialized = UbiParametersExtBuilder.parse(parser);
        assertEquals(extBuilder, deserialized);
        UbiParameters parameters = deserialized.getParams();
        assertEquals("query_id", parameters.getQueryId());
        assertEquals("user_query", parameters.getUserQuery());
        assertEquals("client_id", parameters.getClientId());
        assertEquals("object_id_field", parameters.getObjectIdField());
    }

    public void testXContentRoundTripAllValues() throws IOException {
        UbiParameters param1 = new UbiParameters("query_id", "user_query", "client_id", "object_id_field", Collections.emptyMap());
        UbiParametersExtBuilder extBuilder = new UbiParametersExtBuilder();
        extBuilder.setParams(param1);
        XContentType xContentType = randomFrom(XContentType.values());
        BytesReference serialized = XContentHelper.toXContent(extBuilder, xContentType, true);
        XContentParser parser = createParser(xContentType.xContent(), serialized);
        UbiParametersExtBuilder deserialized = UbiParametersExtBuilder.parse(parser);
        assertEquals(extBuilder, deserialized);
    }

    public void testStreamRoundTrip() throws IOException {
        UbiParameters param1 = new UbiParameters("query_id", "user_query", "client_id", "object_id_field", Collections.emptyMap());
        UbiParametersExtBuilder extBuilder = new UbiParametersExtBuilder();
        extBuilder.setParams(param1);
        BytesStreamOutput bso = new BytesStreamOutput();
        extBuilder.writeTo(bso);
        UbiParametersExtBuilder deserialized = new UbiParametersExtBuilder(bso.bytes().streamInput());
        assertEquals(extBuilder, deserialized);
        UbiParameters parameters = deserialized.getParams();
        assertEquals("query_id", parameters.getQueryId());
        assertEquals("user_query", parameters.getUserQuery());
        assertEquals("client_id", parameters.getClientId());
        assertEquals("object_id_field", parameters.getObjectIdField());
    }

    public void testStreamRoundTripAllValues() throws IOException {
        UbiParameters param1 = new UbiParameters("query_id", "user_query", "client_id", "object_id_field", Collections.emptyMap());
        UbiParametersExtBuilder extBuilder = new UbiParametersExtBuilder();
        extBuilder.setParams(param1);
        BytesStreamOutput bso = new BytesStreamOutput();
        extBuilder.writeTo(bso);
        UbiParametersExtBuilder deserialized = new UbiParametersExtBuilder(bso.bytes().streamInput());
        assertEquals(extBuilder, deserialized);
    }

}
