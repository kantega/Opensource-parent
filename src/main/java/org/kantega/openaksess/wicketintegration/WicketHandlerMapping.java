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
import org.apache.wicket.protocol.http.WicketFilter;
import org.kantega.jexmec.PluginManager;
import org.kantega.jexmec.PluginManagerListenerAdapter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

/**
 *
 */
public class WicketHandlerMapping implements HandlerMapping, ServletContextAware, InitializingBean {
    private ServletContext servletContext;
    private WicketFilter filter;
    @Autowired
    private PluginManager<OpenAksessPlugin> pluginManager;

    public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        final String path = request.getRequestURI().substring(request.getContextPath().length());
        if(path.startsWith("/oap/wicket")) {
            return new HandlerExecutionChain(new HttpRequestHandler() {
                public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
                    filter.doFilter(request, response, new FilterChain() {
                        public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {

                        }
                    });
                }
            });
        } else {
            return null;
        }
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void afterPropertiesSet() throws Exception {
        pluginManager.addPluginManagerListener(new PluginManagerListenerAdapter<OpenAksessPlugin>() {
            @Override
            public void pluginManagerStarted() {
                filter = new WicketFilter();
                final Properties props = new Properties();
                props.setProperty("applicationClassName", OpenAksessWicketApplication.class.getName());
                props.setProperty("filterMappingUrlPattern", "/oap/wicket/*");

                servletContext.setAttribute("OAPluginManager", pluginManager);
                try {
                    filter.init(new FilterConfig() {

                        public String getFilterName() {
                            return "wicket";
                        }

                        public ServletContext getServletContext() {
                            return servletContext;
                        }

                        public String getInitParameter(String name) {
                            return props.getProperty(name);
                        }

                        public Enumeration getInitParameterNames() {
                            return props.propertyNames();
                        }
                    });
                } catch (ServletException e) {
                    throw new RuntimeException(e);
                }

                servletContext.removeAttribute("OAPluginManager");
            }
        });

    }
}
