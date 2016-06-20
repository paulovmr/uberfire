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

package org.uberfire.ext.widgets.core.client.preferences.defaultscreen;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.widgets.common.client.common.Div;

@Dependent
@Templated
public class DefaultPreferencesEditorView extends Composite
        implements DefaultPreferencesEditorPresenter.View {

    private final TranslationService translationService;

    private DefaultPreferencesEditorPresenter presenter;

    @Inject
    @DataField("preferences-container")
    Div container;

    @Inject
    public DefaultPreferencesEditorView( final TranslationService translationService ) {
        super();
        this.translationService = translationService;
    }

    @Override
    public void init( final DefaultPreferencesEditorPresenter presenter ) {
        this.presenter = presenter;

        container.add( presenter.getPreferencesForm().getView().asWidget() );
    }

    @Override
    public Widget asWidget() {
        return container;
    }

}
