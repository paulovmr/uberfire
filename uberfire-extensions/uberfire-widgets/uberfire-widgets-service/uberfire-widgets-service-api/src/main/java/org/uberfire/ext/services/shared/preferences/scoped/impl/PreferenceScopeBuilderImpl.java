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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.ext.services.shared.preferences.scoped.PreferenceScope;
import org.uberfire.ext.services.shared.preferences.scoped.PreferenceScopeBuilder;
import org.uberfire.ext.services.shared.preferences.scoped.PreferenceScopeTypes;

@ApplicationScoped
public class PreferenceScopeBuilderImpl implements PreferenceScopeBuilder {

    private PreferenceScopeTypes scopeTypes;

    protected PreferenceScopeBuilderImpl() {
    }

    @Inject
    public PreferenceScopeBuilderImpl( final PreferenceScopeTypes scopeTypes ) {
        this.scopeTypes = scopeTypes;
    }

    public PreferenceScope build( final String type,
                                  final String key ) {
        if ( !scopeTypes.typeRequiresKey( type ) ) {
            throw new RuntimeException( "This preference scope type does not require a key to be built." );
        }

        return create( type, key );
    }

    public PreferenceScope build( final String type ) {
        if ( scopeTypes.typeRequiresKey( type ) ) {
            throw new RuntimeException( "This preference scope type requires a key to be built." );
        }

        return create( type, scopeTypes.getDefaultKeyFor( type ) );
    }

    public PreferenceScope create( final String type,
                                   final String key ) {
        return new DefaultPreferenceScope( type, key );
    }
}
