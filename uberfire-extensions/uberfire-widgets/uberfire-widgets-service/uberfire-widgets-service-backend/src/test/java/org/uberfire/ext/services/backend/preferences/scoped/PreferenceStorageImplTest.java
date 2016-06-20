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

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.mocks.FileSystemTestingUtils;
import org.uberfire.mocks.SessionInfoMock;
import org.uberfire.ext.services.shared.preferences.scoped.impl.PreferenceScopePoolImpl;
import org.uberfire.ext.services.shared.preferences.scoped.impl.DefaultPreferenceScopeResolutionStrategy;
import org.uberfire.ext.services.shared.preferences.scoped.impl.DefaultPreferenceScopeTypes;
import org.uberfire.ext.services.shared.preferences.scoped.PreferenceScopeResolutionStrategy;
import org.uberfire.ext.services.shared.preferences.scoped.PreferenceScopedValue;
import org.uberfire.ext.services.shared.preferences.scoped.impl.PreferenceScopeBuilderImpl;
import org.uberfire.rpc.SessionInfo;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PreferenceStorageImplTest {

    private static FileSystemTestingUtils fileSystemTestingUtils = new FileSystemTestingUtils();

    private PreferenceStorageImpl preferenceStorageServiceBackendImpl;

    private PreferenceScopePoolImpl scopePool;

    private PreferenceScopeResolutionStrategy preferenceScopeResolutionStrategy;

    @Before
    public void setup() throws IOException {
        fileSystemTestingUtils.setup();

        final SessionInfo sessionInfo = mockSessionInfo();
        final FileSystem fileSystem = mockFileSystem();
        final IOService ioService = mockIoService( fileSystem );

        final DefaultPreferenceScopeTypes scopeTypes = new DefaultPreferenceScopeTypes( sessionInfo );
        final PreferenceScopeBuilderImpl scopeBuilder = new PreferenceScopeBuilderImpl( scopeTypes );

        scopePool = new PreferenceScopePoolImpl( scopeBuilder );
        preferenceScopeResolutionStrategy = new DefaultPreferenceScopeResolutionStrategy( scopePool );
        preferenceStorageServiceBackendImpl = new PreferenceStorageImpl( ioService,
                                                                         sessionInfo );
        preferenceStorageServiceBackendImpl.init();
    }

    @After
    public void cleanupFileSystem() {
        fileSystemTestingUtils.cleanup();
    }

    @Test
    public void writeReadLongTest() {
        preferenceStorageServiceBackendImpl.write( scopePool.get( DefaultPreferenceScopeTypes.USER ),
                                                   "my.preference.key",
                                                   23L );
        final long value = preferenceStorageServiceBackendImpl.read( scopePool.get( DefaultPreferenceScopeTypes.USER ),
                                                                     "my.preference.key" );
        assertEquals( 23, value );
    }

    @Test
    public void writeReadStringTest() {
        preferenceStorageServiceBackendImpl.write( scopePool.get( DefaultPreferenceScopeTypes.USER ),
                                                   "my.preference.key",
                                                   "text" );
        final String value = preferenceStorageServiceBackendImpl.read( scopePool.get( DefaultPreferenceScopeTypes.USER ),
                                                                       "my.preference.key" );
        assertEquals( "text", value );
    }

    @Test
    public void writeReadBooleanTest() {
        preferenceStorageServiceBackendImpl.write( scopePool.get( DefaultPreferenceScopeTypes.USER ),
                                                   "my.preference.key.true",
                                                   true );
        preferenceStorageServiceBackendImpl.write( scopePool.get( DefaultPreferenceScopeTypes.USER ),
                                                   "my.preference.key.false",
                                                   false );
        final boolean value1 = preferenceStorageServiceBackendImpl.read( scopePool.get( DefaultPreferenceScopeTypes.USER ),
                                                                         "my.preference.key.true" );
        final boolean value2 = preferenceStorageServiceBackendImpl.read( scopePool.get( DefaultPreferenceScopeTypes.USER ),
                                                                         "my.preference.key.false" );
        assertEquals( true, value1 );
        assertEquals( false, value2 );
    }

    @Test
    public void writeReadCustomObjectTest() {
        CustomObject customObject = new CustomObject( 61L, "some text" );

        preferenceStorageServiceBackendImpl.write( scopePool.get( DefaultPreferenceScopeTypes.USER ),
                                                   "my.preference.key",
                                                   customObject );
        final CustomObject value = preferenceStorageServiceBackendImpl.read( scopePool.get( DefaultPreferenceScopeTypes.USER ),
                                                                             "my.preference.key" );
        assertEquals( customObject.id, value.id );
        assertEquals( customObject.text, value.text );
    }

    @Test
    public void readNonexistentPreferenceFromSpecificScopeTest() {
        final String value = preferenceStorageServiceBackendImpl.read( scopePool.get( DefaultPreferenceScopeTypes.USER ),
                                                                       "my.nonexistent.preference.key" );
        assertNull( value );
    }

    @Test
    public void readNonexistentPreferenceWithResolutionStrategyTest() {
        final String value = preferenceStorageServiceBackendImpl.read( preferenceScopeResolutionStrategy,
                                                                       "my.nonexistent.preference.key" );
        assertNull( value );
    }

    @Test
    public void writeGlobalAndUserReadUserWithResolutionStrategyTest() {
        preferenceStorageServiceBackendImpl.write( scopePool.get( DefaultPreferenceScopeTypes.USER ),
                                                   "my.preference.key",
                                                   "user_value" );
        preferenceStorageServiceBackendImpl.write( scopePool.get( DefaultPreferenceScopeTypes.GLOBAL ),
                                                   "my.preference.key",
                                                   "global_value" );
        final String value = preferenceStorageServiceBackendImpl.read( preferenceScopeResolutionStrategy,
                                                                       "my.preference.key" );
        assertEquals( "user_value", value );
    }

    @Test
    public void writeGlobalReadGlobalWithResolutionStrategyTest() {
        preferenceStorageServiceBackendImpl.write( scopePool.get( DefaultPreferenceScopeTypes.GLOBAL ),
                                                   "my.preference.key",
                                                   "global_value" );
        final String value = preferenceStorageServiceBackendImpl.read( preferenceScopeResolutionStrategy,
                                                                       "my.preference.key" );
        assertEquals( "global_value", value );
    }

    @Test
    public void writeUserReadUserWithResolutionStrategyTest() {
        preferenceStorageServiceBackendImpl.write( scopePool.get( DefaultPreferenceScopeTypes.USER ),
                                                   "my.preference.key",
                                                   "user_value" );
        final String value = preferenceStorageServiceBackendImpl.read( preferenceScopeResolutionStrategy,
                                                                       "my.preference.key" );
        assertEquals( "user_value", value );
    }

    @Test
    public void readFromSpecificScopeTest() {
        preferenceStorageServiceBackendImpl.write( scopePool.get( DefaultPreferenceScopeTypes.USER ),
                                                   "my.preference.key",
                                                   "value" );
        final String value = preferenceStorageServiceBackendImpl.read( scopePool.get( DefaultPreferenceScopeTypes.USER ),
                                                                      "my.preference.key" );
        assertEquals( "value", value );
    }

    @Test
    public void readWithResolutionStrategyTest() {
        preferenceStorageServiceBackendImpl.write( scopePool.get( DefaultPreferenceScopeTypes.GLOBAL ),
                                                   "my.preference.key",
                                                   "value" );
        final String value = preferenceStorageServiceBackendImpl.read( preferenceScopeResolutionStrategy,
                                                                      "my.preference.key" );
        assertEquals( "value", value );
    }

    @Test
    public void writeGlobalAndUserReadWithScopeUserWithResolutionStrategyTest() {
        preferenceStorageServiceBackendImpl.write( scopePool.get( DefaultPreferenceScopeTypes.USER ),
                                                   "my.preference.key",
                                                   "user_value" );
        preferenceStorageServiceBackendImpl.write( scopePool.get( DefaultPreferenceScopeTypes.GLOBAL ),
                                                   "my.preference.key",
                                                   "global_value" );
        final PreferenceScopedValue<String> scopedValue = preferenceStorageServiceBackendImpl.readWithScope( preferenceScopeResolutionStrategy,
                                                                                                             "my.preference.key" );
        assertEquals( "user_value", scopedValue.getValue() );
        assertEquals( scopePool.get( DefaultPreferenceScopeTypes.USER ).key(), scopedValue.getScopeKey() );
    }

    @Test
    public void writeGlobalReadWithScopeGlobalWithResolutionStrategyTest() {
        preferenceStorageServiceBackendImpl.write( scopePool.get( DefaultPreferenceScopeTypes.GLOBAL ),
                                                   "my.preference.key",
                                                   "global_value" );
        final PreferenceScopedValue<String> scopedValue = preferenceStorageServiceBackendImpl.readWithScope( preferenceScopeResolutionStrategy,
                                                                                                             "my.preference.key" );
        assertEquals( "global_value", scopedValue.getValue() );
        assertEquals( scopePool.get( DefaultPreferenceScopeTypes.GLOBAL ).key(), scopedValue.getScopeKey() );
    }

    @Test
    public void writeUserReadWithScopeUserUsingResolutionStrategyTest() {
        preferenceStorageServiceBackendImpl.write( scopePool.get( DefaultPreferenceScopeTypes.USER ),
                                                   "my.preference.key",
                                                   "user_value" );
        final PreferenceScopedValue<String> scopedValue = preferenceStorageServiceBackendImpl.readWithScope( preferenceScopeResolutionStrategy,
                                                                                                             "my.preference.key" );
        assertEquals( "user_value", scopedValue.getValue() );
        assertEquals( scopePool.get( DefaultPreferenceScopeTypes.USER ).key(), scopedValue.getScopeKey() );
    }

    @Test
    public void deleteFromUserTest() {
        String value;

        // define preference defined for global and user scopes
        preferenceStorageServiceBackendImpl.write( scopePool.get( DefaultPreferenceScopeTypes.USER ),
                                                   "my.preference.key",
                                                   "user_value" );
        preferenceStorageServiceBackendImpl.write( scopePool.get( DefaultPreferenceScopeTypes.GLOBAL ),
                                                   "my.preference.key",
                                                   "global_value" );
        value = preferenceStorageServiceBackendImpl.read( preferenceScopeResolutionStrategy,
                                                          "my.preference.key" );
        assertEquals( "user_value", value );

        // delete preference from user scope
        preferenceStorageServiceBackendImpl.delete( scopePool.get( DefaultPreferenceScopeTypes.USER ),
                                                    "my.preference.key" );
        value = preferenceStorageServiceBackendImpl.read( preferenceScopeResolutionStrategy,
                                                          "my.preference.key" );
        assertEquals( "global_value", value );

        // delete preference from global scope
        preferenceStorageServiceBackendImpl.delete( scopePool.get( DefaultPreferenceScopeTypes.GLOBAL ),
                                                    "my.preference.key" );
        value = preferenceStorageServiceBackendImpl.read( preferenceScopeResolutionStrategy,
                                                          "my.preference.key" );
        assertNull( value );
    }

    @Test
    public void allKeysWithNoKeysTest() {
        // global preferences
        preferenceStorageServiceBackendImpl.write( scopePool.get( DefaultPreferenceScopeTypes.GLOBAL ),
                                                   "my.first.global.preference.key",
                                                   "global_value1" );
        preferenceStorageServiceBackendImpl.write( scopePool.get( DefaultPreferenceScopeTypes.GLOBAL ),
                                                   "my.second.global.preference.key",
                                                   "global_value2" );
        preferenceStorageServiceBackendImpl.write( scopePool.get( DefaultPreferenceScopeTypes.GLOBAL ),
                                                   "my.third.global.preference.key",
                                                   "global_value3" );

        // user preferences
        preferenceStorageServiceBackendImpl.write( scopePool.get( DefaultPreferenceScopeTypes.USER ),
                                                   "my.first.user.preference.key",
                                                   "user_value1" );
        preferenceStorageServiceBackendImpl.write( scopePool.get( DefaultPreferenceScopeTypes.USER ),
                                                   "my.second.user.preference.key",
                                                   "user_value2" );

        final Collection<String> globalKeys = preferenceStorageServiceBackendImpl.allKeys( scopePool.get( DefaultPreferenceScopeTypes.GLOBAL ) );
        final Collection<String> userKeys = preferenceStorageServiceBackendImpl.allKeys( scopePool.get( DefaultPreferenceScopeTypes.USER ) );

        assertNotNull( globalKeys );
        assertEquals( 3, globalKeys.size() );
        assertTrue( globalKeys.contains( "my.first.global.preference.key" ) );
        assertTrue( globalKeys.contains( "my.second.global.preference.key" ) );
        assertTrue( globalKeys.contains( "my.third.global.preference.key" ) );

        assertNotNull( userKeys );
        assertEquals( 2, userKeys.size() );
        assertTrue( userKeys.contains( "my.first.user.preference.key" ) );
        assertTrue( userKeys.contains( "my.second.user.preference.key" ) );
    }

    @Test
    public void allKeysWithKeysTest() {
        final Collection<String> keys = preferenceStorageServiceBackendImpl.allKeys( scopePool.get( DefaultPreferenceScopeTypes.GLOBAL ) );

        assertNotNull( keys );
        assertEquals( "There should not exist any keys.", 0, keys.size() );
    }

    @Test
    public void buildScopePathForGlobalScope() {
        final String path = preferenceStorageServiceBackendImpl.buildScopePath( scopePool.get( DefaultPreferenceScopeTypes.GLOBAL ) );

        assertEquals( "/config/global/global", path );
    }

    @Test
    public void buildScopePathForUserScope() {
        final String path = preferenceStorageServiceBackendImpl.buildScopePath( scopePool.get( DefaultPreferenceScopeTypes.USER ) );

        assertEquals( "/config/user/myuser", path );
    }

    @Test
    public void buildStoragePathForUserScope() {
        final String path = preferenceStorageServiceBackendImpl.buildScopedPreferencePath( scopePool.get( DefaultPreferenceScopeTypes.USER ),
                                                                                           "my.preference.key" );

        assertEquals( "/config/user/myuser/my.preference.key.preferences", path );
    }

    @Test
    public void buildStoragePathForGlobalScope() {
        final String path = preferenceStorageServiceBackendImpl.buildScopedPreferencePath( scopePool.get( DefaultPreferenceScopeTypes.GLOBAL ),
                                                                                           "my.preference.key" );

        assertEquals( "/config/global/global/my.preference.key.preferences", path );
    }

    private SessionInfo mockSessionInfo() {
        return new SessionInfoMock( "myuser" );
    }

    private FileSystem mockFileSystem() {
        return fileSystemTestingUtils.getFileSystem();
    }

    private IOService mockIoService( final FileSystem fileSystem ) {
        final IOService ioService = spy( fileSystemTestingUtils.getIoService() );

        doNothing().when( ioService ).startBatch( any( FileSystem.class ) );
        doNothing().when( ioService ).endBatch();
        doReturn( fileSystem ).when( ioService ).newFileSystem( any( URI.class ), anyMap() );

        return ioService;
    }

}
