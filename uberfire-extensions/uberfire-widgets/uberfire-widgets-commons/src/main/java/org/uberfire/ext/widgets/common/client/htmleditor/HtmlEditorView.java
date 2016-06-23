/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.widgets.common.client.htmleditor;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.widgets.common.client.common.Div;
import org.uberfire.ext.widgets.common.client.resources.HtmlEditorResources;

import static com.google.gwt.core.client.ScriptInjector.*;

@Dependent
@Templated
public class HtmlEditorView extends Composite
        implements HtmlEditorPresenter.View {

    private HtmlEditorPresenter presenter;

    private boolean scriptsAreLoaded = false;

    @Inject
    @DataField("html-editor")
    Div htmlEditor;

    @Override
    public void init( final HtmlEditorPresenter presenter ) {
        this.presenter = presenter;
    }

    @PostConstruct
    public void initEditor() {
    }

    @Override
    public void show() {
        if ( !scriptsAreLoaded ) {
            ScriptInjector.fromString( HtmlEditorResources.INSTANCE.wysihtml().getText() ).setWindow( TOP_WINDOW ).inject();
            ScriptInjector.fromString( HtmlEditorResources.INSTANCE.wysihtmlAllCommands().getText() ).setWindow( TOP_WINDOW ).inject();
            ScriptInjector.fromString( HtmlEditorResources.INSTANCE.wysihtmlTableEditing().getText() ).setWindow( TOP_WINDOW ).inject();
            ScriptInjector.fromString( HtmlEditorResources.INSTANCE.wysihtmlToolbar().getText() ).setWindow( TOP_WINDOW ).inject();
            ScriptInjector.fromString( HtmlEditorResources.INSTANCE.parserRules().getText() ).setWindow( TOP_WINDOW ).inject();
            ScriptInjector.fromString( HtmlEditorResources.INSTANCE.custom().getText() ).setWindow( TOP_WINDOW ).inject();
            scriptsAreLoaded = true;
        }
    }

    @Override
    public void setContent( final String content ) {
        htmlEditor.getElement().setInnerHTML( content );
    }

    @Override
    public String getContent() {
        return htmlEditor.getElement().getInnerHTML();
    }
}
