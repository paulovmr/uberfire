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

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.ext.services.shared.preferences.scoped.PreferenceScope;
import org.uberfire.ext.services.shared.preferences.scoped.PreferenceScopeBuilder;
import org.uberfire.ext.services.shared.preferences.scoped.PreferenceScopePool;

@ApplicationScoped
public class PreferenceScopePoolImpl implements PreferenceScopePool {

    private PreferenceScopeBuilder builder;

    private Map<String, PreferenceScope> scopesByType = new HashMap<>();

    private Map<String, Map<String, PreferenceScope>> scopesByKeyByType = new HashMap<>();

    protected PreferenceScopePoolImpl() {
    }

    @Inject
    public PreferenceScopePoolImpl( final PreferenceScopeBuilder builder ) {
        this.builder = builder;
    }

    public PreferenceScope get( String type, String key ) {
        Map<String, PreferenceScope> scopesByKey = scopesByKeyByType.get( type );

        if ( scopesByKey == null ) {
            scopesByKey = new HashMap<>();
            scopesByKeyByType.put( type, scopesByKey );
        }

        PreferenceScope scope = scopesByKey.get( key );

        if ( scope == null ) {
            scope = builder.build( type, key );
            scopesByKey.put( key, scope );
        }

        return scope;
    }

    public PreferenceScope get( String type ) {
        PreferenceScope scope = scopesByType.get( type );

        if ( scope == null ) {
            scope = builder.build( type );
            scopesByType.put( type, scope );
        }

        return scope;
    }
}
