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

import org.elasticsearch.action.support.ActionFilter;
import org.elasticsearch.plugins.ActionPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.plugins.SearchPlugin;
import com.o19s.ubi.ext.UbiParametersExtBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;

/**
 * elasticsearch User Behavior Insights
 */
public class UbiPlugin extends Plugin implements ActionPlugin, SearchPlugin {

    private ActionFilter ubiActionFilter;

    /**
     * Creates a new instance of {@link UbiPlugin}.
     */
    public UbiPlugin() {}

    @Override
    public List<ActionFilter> getActionFilters() {
        return singletonList(ubiActionFilter);
    }

    @Override
    public Collection<?> createComponents(PluginServices services) {

        this.ubiActionFilter = new UbiActionFilter(services.client());
        return Collections.emptyList();

    }

    @Override
    public List<SearchExtSpec<?>> getSearchExts() {

        final List<SearchExtSpec<?>> searchExts = new ArrayList<>();

        searchExts.add(
            new SearchExtSpec<>(UbiParametersExtBuilder.UBI_PARAMETER_NAME, UbiParametersExtBuilder::new, UbiParametersExtBuilder::parse)
        );

        return searchExts;

    }

}
