/*
 * Copyright 2010 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kantega.openaksess.wicketintegration;

import no.kantega.publishing.api.plugin.OpenAksessPlugin;
import org.apache.wicket.Page;
import org.apache.wicket.markup.MarkupCache;
import org.apache.wicket.protocol.http.WebApplication;
import org.kantega.jexmec.PluginManager;
import org.kantega.openaksess.wicketintegration.pages.HomePage;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class OpenAksessWicketApplication extends WebApplication {
    private PluginManager<OpenAksessPlugin> pluginManger;

    @Override
    protected void init() {
        getMarkupSettings().setMarkupCache(new MarkupCache(this));

        pluginManger = (PluginManager<OpenAksessPlugin>) getServletContext().getAttribute("OAPluginManager");

        for (OpenAksessWicketPlugin plugin : getOAWicketPlugins()) {
            plugin.initApplication(this);
        }

    }

    private List<OpenAksessWicketPlugin> getOAWicketPlugins() {
        List<OpenAksessWicketPlugin> plugins = new ArrayList<OpenAksessWicketPlugin>();
        for (OpenAksessPlugin plugin : pluginManger.getPlugins()) {
            if (plugin instanceof OpenAksessWicketPlugin) {
                plugins.add((OpenAksessWicketPlugin) plugin);
            }
        }
        return plugins;
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return HomePage.class;
    }

    public static WebApplication get() {
        return WebApplication.get();
    }
}
