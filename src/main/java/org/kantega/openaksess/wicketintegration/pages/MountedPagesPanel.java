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

import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.IRequestCodingStrategy;
import org.apache.wicket.request.IRequestTargetMountsInfo;
import org.apache.wicket.request.target.coding.IRequestTargetUrlCodingStrategy;
import org.kantega.openaksess.wicketintegration.OpenAksessWicketApplication;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MountedPagesPanel extends Panel {
    public MountedPagesPanel(String id) {
        super(id);

        final IRequestCodingStrategy requestCodingStrategy = OpenAksessWicketApplication.get()
                .getRequestCycleProcessor()
                .getRequestCodingStrategy();

        List<String> mounts = new ArrayList<String>();

        if(requestCodingStrategy instanceof IRequestTargetMountsInfo) {
            final IRequestTargetMountsInfo mountsInfo = (IRequestTargetMountsInfo) requestCodingStrategy;
            for(IRequestTargetUrlCodingStrategy str : mountsInfo.listMounts()) {
                mounts.add(str.getMountPath());
            }
        }

        add(new ListView<String>("links", mounts) {
            @Override
            protected void populateItem(ListItem<String> item) {
                final Model<String> mountModel = new Model<String>(item.getModelObject());
                item.add(new ExternalLink("link", mountModel, mountModel));
            }
        });
    }
}
