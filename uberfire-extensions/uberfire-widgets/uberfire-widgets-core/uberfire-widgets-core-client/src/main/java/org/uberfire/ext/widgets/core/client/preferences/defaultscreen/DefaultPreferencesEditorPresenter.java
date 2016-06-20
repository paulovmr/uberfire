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

import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.widgets.core.client.preferences.form.PreferencesEditorFormPresenter;

@Dependent
@WorkbenchScreen( identifier = "DefaultPreferencesEditorPresenter" )
public class DefaultPreferencesEditorPresenter {

    public interface View extends UberView<DefaultPreferencesEditorPresenter> {

    }

    private final SyncBeanManager beanManager;

    private final View view;

    private PreferencesEditorFormPresenter preferencesForm;

    @Inject
    public DefaultPreferencesEditorPresenter( final SyncBeanManager beanManager,
                                              final View view ) {
        this.beanManager = beanManager;
        this.view = view;
    }

    @PostConstruct
    public void init() {
        preferencesForm = beanManager.lookupBean( PreferencesEditorFormPresenter.class ).newInstance();
        preferencesForm.setManagedKeys( managedKeys() );
        view.init( this );
    }

    @WorkbenchPartView
    public View getView() {
        return view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Preferences";
    }

    protected List<String> managedKeys() {
        return null;
    }

    public PreferencesEditorFormPresenter getPreferencesForm() {
        return preferencesForm;
    }

    @PreDestroy
    public void destroy() {
        if ( preferencesForm != null ) {
            beanManager.destroyBean( preferencesForm );
        }
    }
}
