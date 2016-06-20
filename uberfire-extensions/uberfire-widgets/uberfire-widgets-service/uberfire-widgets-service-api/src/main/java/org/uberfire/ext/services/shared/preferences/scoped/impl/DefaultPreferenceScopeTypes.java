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

import org.uberfire.ext.services.shared.preferences.scoped.PreferenceScopeTypes;
import org.uberfire.ext.services.shared.preferences.scoped.impl.exception.InvalidPreferenceScopeException;
import org.uberfire.rpc.SessionInfo;

@ApplicationScoped
public class DefaultPreferenceScopeTypes implements PreferenceScopeTypes {

    public static final String GLOBAL = "global";
    public static final String USER = "user";

    private Map<String, String> defaultKeyByType;

    private SessionInfo sessionInfo;

    protected DefaultPreferenceScopeTypes() {
    }

    @Inject
    public DefaultPreferenceScopeTypes( final SessionInfo sessionInfo ) {
        this.sessionInfo = sessionInfo;

        defaultKeyByType = new HashMap<>();

        defaultKeyByType.put( GLOBAL, GLOBAL );
        defaultKeyByType.put( USER, sessionInfo.getIdentity().getIdentifier() );
    }

    @Override
    public void validateType( final String type ) throws InvalidPreferenceScopeException {
        if ( !defaultKeyByType.containsKey( type ) ) {
            throw new InvalidPreferenceScopeException( "Invalid preference scope." );
        }
    }

    @Override
    public boolean typeRequiresKey( final String type ) throws InvalidPreferenceScopeException {
        validateType( type );

        return defaultKeyByType.get( type ) == null;
    }

    @Override
    public String getDefaultKeyFor( final String type ) throws InvalidPreferenceScopeException {
        validateType( type );

        return defaultKeyByType.get( type );
    }
}
