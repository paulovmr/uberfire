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

package org.uberfire.ext.services.shared.preferences.scoped.impl;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.ext.services.shared.preferences.scoped.PreferenceScope;
import org.uberfire.ext.services.shared.preferences.scoped.PreferenceScopeResolutionStrategy;

@ApplicationScoped
public class DefaultPreferenceScopeResolutionStrategy implements PreferenceScopeResolutionStrategy {

    private PreferenceScopePoolImpl scopesPool;

    private List<PreferenceScope> order = null;

    protected DefaultPreferenceScopeResolutionStrategy() {
    }

    @Inject
    public DefaultPreferenceScopeResolutionStrategy( final PreferenceScopePoolImpl scopesPool ) {
        this.scopesPool = scopesPool;
    }

    @Override
    public List<PreferenceScope> order() {
        if ( order == null ) {
            order = new ArrayList<>( 2 );

            order.add( scopesPool.get( DefaultPreferenceScopeTypes.USER ) );
            order.add( scopesPool.get( DefaultPreferenceScopeTypes.GLOBAL ) );
        }

        return order;
    }

    @Override
    public PreferenceScope defaultScope() {
        final List<PreferenceScope> order = order();

        return order.get( order.size() - 1 );
    }
}
