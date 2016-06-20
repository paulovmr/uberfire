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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.mocks.SessionInfoMock;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.ext.services.shared.preferences.scoped.impl.PreferenceScopePoolImpl;
import org.uberfire.ext.services.shared.preferences.scoped.impl.DefaultPreferenceScopeResolutionStrategy;
import org.uberfire.ext.services.shared.preferences.scoped.impl.DefaultPreferenceScopeTypes;
import org.uberfire.ext.services.shared.preferences.scoped.PreferenceScope;
import org.uberfire.ext.services.shared.preferences.scoped.PreferenceScopeResolutionStrategy;
import org.uberfire.ext.services.shared.preferences.scoped.PreferenceStorage;
import org.uberfire.ext.services.shared.preferences.scoped.PreferenceStore;
import org.uberfire.ext.services.shared.preferences.scoped.impl.PreferenceScopeBuilderImpl;
import org.uberfire.rpc.SessionInfo;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PreferenceStoreImplTest {

    private ParameterizedCommand<String> callback;

    private PreferenceScopePoolImpl scopePool;

    private PreferenceStorage storage;

    private PreferenceScopeResolutionStrategy preferenceScopeResolutionStrategy;

    private PreferenceStore preferenceStore;

    @Before
    public void setup() {
        callback = (ParameterizedCommand<String>) mock( ParameterizedCommand.class );
        final SessionInfo sessionInfo = new SessionInfoMock( "myuser" );
        final DefaultPreferenceScopeTypes scopeTypes = new DefaultPreferenceScopeTypes( sessionInfo );
        final PreferenceScopeBuilderImpl scopeBuilder = new PreferenceScopeBuilderImpl( scopeTypes );

        scopePool = new PreferenceScopePoolImpl( scopeBuilder );
        preferenceScopeResolutionStrategy = new DefaultPreferenceScopeResolutionStrategy( scopePool );
        storage = mockStorage();

        preferenceStore = new PreferenceStoreImpl( storage, preferenceScopeResolutionStrategy );
    }

    @Test
    public void putTest() {
        final PreferenceScope globalScope = scopePool.get( DefaultPreferenceScopeTypes.GLOBAL );
        final String key = "my.preference.key";
        final String value = "value";

        preferenceStore.put( globalScope, key, value );

        verify( storage ).write( globalScope, key, value );
    }

    @Test
    public void getStringValueWithoutDefaultValueTest() {
        final String key = "my.preference.key";
        final String expectedValue = "value";

        mockStorageRead( expectedValue );

        final String value = preferenceStore.get( key );

        assertEquals( expectedValue, value );
    }

    @Test
    public void getNullValueWithoutDefaultValueTest() {
        final String key = "my.preference.key";
        final String expectedValue = null;

        mockStorageRead( expectedValue );

        final String value = preferenceStore.get( key );

        assertEquals( expectedValue, value );
    }

    @Test
    public void getStringValueWithDefaultValueTest() {
        final String key = "my.preference.key";
        final String expectedValue = "value";
        final String defaultValue = "defaultValue";

        mockStorageRead( expectedValue );

        final String value = preferenceStore.get( key, defaultValue );

        assertEquals( expectedValue, value );
    }

    @Test
    public void getNullValueWithDefaultValueTest() {
        final String key = "my.preference.key";
        final String defaultValue = "defaultValue";

        final String value = preferenceStore.get( key, defaultValue );

        assertEquals( defaultValue, value );
    }

    @Test
    public void getStringValueFromScopeWithoutDefaultValueTest() {
        final PreferenceScope globalScope = scopePool.get( DefaultPreferenceScopeTypes.GLOBAL );
        final String key = "my.preference.key";
        final String expectedValue = "value";

        mockStorageRead( expectedValue );

        final String value = preferenceStore.get( globalScope, key );

        assertEquals( expectedValue, value );
    }

    @Test
    public void getNullValueFromScopeWithoutDefaultValueTest() {
        final PreferenceScope globalScope = scopePool.get( DefaultPreferenceScopeTypes.GLOBAL );
        final String key = "my.preference.key";
        final String expectedValue = null;

        mockStorageRead( expectedValue );

        final String value = preferenceStore.get( globalScope, key );

        assertEquals( expectedValue, value );
    }

    @Test
    public void getStringValueFromScopeWithDefaultValueTest() {
        final PreferenceScope globalScope = scopePool.get( DefaultPreferenceScopeTypes.GLOBAL );
        final String key = "my.preference.key";
        final String expectedValue = "value";
        final String defaultValue = "defaultValue";

        mockStorageRead( expectedValue );

        final String value = preferenceStore.get( globalScope, key, defaultValue );

        assertEquals( expectedValue, value );
    }

    @Test
    public void getNullValueFromScopeWithDefaultValueTest() {
        final PreferenceScope globalScope = scopePool.get( DefaultPreferenceScopeTypes.GLOBAL );
        final String key = "my.preference.key";
        final String defaultValue = "defaultValue";

        final String value = preferenceStore.get( globalScope, key, defaultValue );

        assertEquals( defaultValue, value );
    }

    @Test
    public void allPreferencesTest() {
        doReturn( "value" ).when( storage ).read( preferenceScopeResolutionStrategy, "my.first.preference.key" );
        doReturn( "value" ).when( storage ).read( preferenceScopeResolutionStrategy, "my.second.preference.key" );
        doReturn( "value" ).when( storage ).read( preferenceScopeResolutionStrategy, "my.third.preference.key" );

        List<String> preferenceKeys = new ArrayList<>( 3 );
        preferenceKeys.add( "my.first.preference.key" );
        preferenceKeys.add( "my.second.preference.key" );
        preferenceKeys.add(  "my.third.preference.key");
        doReturn( preferenceKeys ).when( storage ).allKeys( preferenceScopeResolutionStrategy.order() );

        Map<String, Object> valueByKey = preferenceStore.all();

        assertNotNull( valueByKey );
        assertEquals( 3, valueByKey.size() );

        assertTrue( valueByKey.containsKey( "my.first.preference.key" ) );
        assertTrue( valueByKey.containsKey( "my.second.preference.key" ) );
        assertTrue( valueByKey.containsKey( "my.third.preference.key" ) );

        assertEquals( "value", valueByKey.get( "my.first.preference.key" ) );
        assertEquals( "value", valueByKey.get( "my.second.preference.key" ) );
        assertEquals( "value", valueByKey.get( "my.third.preference.key" ) );
    }

    @Test
    public void allScopedPreferencesTest() {
        // preferences defined for user scope
        final PreferenceScope userScope = scopePool.get( DefaultPreferenceScopeTypes.USER );
        doReturn( "value" ).when( storage ).read( userScope, "my.first.preference.key" );
        doReturn( "value" ).when( storage ).read( userScope, "my.second.preference.key" );

        final Map<String, Object> valueByKey = preferenceStore.all( userScope );

        assertNotNull( valueByKey );
        assertEquals( 2, valueByKey.size() );

        assertTrue( valueByKey.containsKey( "my.first.preference.key" ) );
        assertTrue( valueByKey.containsKey( "my.second.preference.key" ) );

        assertEquals( "value", valueByKey.get( "my.first.preference.key" ) );
        assertEquals( "value", valueByKey.get( "my.second.preference.key" ) );
    }

    @Test
    public void removeTest() {
        final PreferenceScope globalScope = scopePool.get( DefaultPreferenceScopeTypes.GLOBAL );
        final String key = "my.preference.key";

        preferenceStore.remove( globalScope, key );

        verify( storage ).delete( globalScope, key );
    }

    private void mockStorageRead( final String value ) {
        doReturn( value ).when( storage ).read( any( PreferenceScope.class ), anyString() );
        doReturn( value ).when( storage ).read( any( PreferenceScopeResolutionStrategy.class ), anyString() );
    }

    private PreferenceStorage mockStorage() {
        storage = mock( PreferenceStorage.class );

        List<String> allGlobalKeys = new ArrayList<>( 3 );
        allGlobalKeys.add( "my.first.preference.key" );
        allGlobalKeys.add( "my.second.preference.key" );
        allGlobalKeys.add( "my.third.preference.key" );

        List<String> allUserKeys = new ArrayList<>( 2 );
        allUserKeys.add( "my.first.preference.key" );
        allUserKeys.add( "my.second.preference.key" );

        when( storage.allKeys( eq( scopePool.get( DefaultPreferenceScopeTypes.GLOBAL ) ) ) ).thenReturn( allGlobalKeys );
        when( storage.allKeys( eq( scopePool.get( DefaultPreferenceScopeTypes.USER ) ) ) ).thenReturn( allUserKeys );

        return storage;
    }
}
