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

package org.kantega.openaksess.wicketintegration.pages;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.apache.wicket.util.time.Time;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 *
 */
public abstract class OpenAksessPage extends WebPage implements IMarkupResourceStreamProvider {

    public OpenAksessPage() {
        add(new Label("title", getPageTitle()).setRenderBodyOnly(true));

        add(getContentComponent("content"));
    }

    protected Component getContentComponent(String id) {
        return new Label(id, "");
    }

    protected String getPageTitle() {
        return getClass().getSimpleName();
    }


    public IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass) {
        WebRequest req = (WebRequest) RequestCycle.get().getRequest();
        WebResponse res = (WebResponse) RequestCycle.get().getResponse();

        final RequestDispatcher rd = req.getHttpServletRequest().getRequestDispatcher("/WEB-INF/jsp/wicket/design.jsp");
        try {
            final StringHttpServletResponseWrapper wrapper = new StringHttpServletResponseWrapper(res.getHttpServletResponse());
            long before = System.currentTimeMillis();
            rd.include(req.getHttpServletRequest(), wrapper);
            System.out.println("Took: " + (System.currentTimeMillis() - before));
            String content = wrapper.getContent();
            return new StringResourceStream(content) {
                @Override
                public Time lastModifiedTime() {
                    return Time.milliseconds(System.currentTimeMillis());
                }
            };
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private class StringHttpServletResponseWrapper extends HttpServletResponseWrapper {

        private ByteArrayOutputStream out = new ByteArrayOutputStream();

        private ServletOutputStream sout = new ServletOutputStream() {
            @Override
            public void write(int i) throws IOException {
                out.write(i);
            }
        };

        private PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));

        public StringHttpServletResponseWrapper(HttpServletResponse res) {
            super(res);
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return writer;
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            return sout;
        }

        public String getContent() {
            writer.flush();
            return new String(out.toByteArray());
        }
    }
}
