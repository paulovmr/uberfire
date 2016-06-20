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

package org.uberfire.ext.services.backend.preferences.scoped;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.mocks.SessionInfoMock;
import org.uberfire.ext.services.shared.preferences.scoped.impl.PreferenceScopePoolImpl;
import org.uberfire.ext.services.shared.preferences.scoped.impl.DefaultPreferenceScopeResolutionStrategy;
import org.uberfire.ext.services.shared.preferences.scoped.impl.DefaultPreferenceScopeTypes;
import org.uberfire.ext.services.shared.preferences.scoped.PreferenceScope;
import org.uberfire.ext.services.shared.preferences.scoped.impl.PreferenceScopeBuilderImpl;
import org.uberfire.rpc.SessionInfo;

import static org.junit.Assert.*;

public class DefaultPreferenceScopeResolutionStrategyTest {

    private DefaultPreferenceScopeResolutionStrategy defaultPreferenceScopeResolutionStrategy;

    @Before
    public void setup() {
        final SessionInfo sessionInfo = new SessionInfoMock( "myuser" );
        final DefaultPreferenceScopeTypes scopeTypes = new DefaultPreferenceScopeTypes( sessionInfo );
        final PreferenceScopeBuilderImpl scopeBuilder = new PreferenceScopeBuilderImpl( scopeTypes );
        final PreferenceScopePoolImpl scopePool = new PreferenceScopePoolImpl( scopeBuilder );

        defaultPreferenceScopeResolutionStrategy = new DefaultPreferenceScopeResolutionStrategy( scopePool );
    }

    @Test
    public void orderTest() {
        final List<PreferenceScope> order = defaultPreferenceScopeResolutionStrategy.order();

        assertEquals( 2, order.size() );

        final PreferenceScope userScope = order.get( 0 );
        assertEquals( "user", userScope.type() );
        assertEquals( "myuser", userScope.key() );

        final PreferenceScope globalScope = order.get( 1 );
        assertEquals( "global", globalScope.type() );
        assertEquals( "global", globalScope.key() );
    }

    @Test
    public void defaultScopeTest() {
        final PreferenceScope defaultScope = defaultPreferenceScopeResolutionStrategy.defaultScope();

        assertEquals( "global", defaultScope.type() );
        assertEquals( "global", defaultScope.key() );
    }
}
