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

package org.uberfire.ext.services.shared.preferences.scoped;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface PreferenceStore {

    <T> void put( final PreferenceScope scope,
                  final String key,
                  final T value );

    <T> void put( final PreferenceScopeResolutionStrategy scopeResolutionStrategy,
                  final String key,
                  final T value );

    <T> void put( final String key,
                  final T value );

    <T> void put( final PreferenceScope scope,
                  final Map<String, T> valueByKey );

    <T> void put( final PreferenceScopeResolutionStrategy scopeResolutionStrategy,
                  final Map<String, T> valueByKey );

    <T> void put( final Map<String, T> valueByKey );

    <T> void putIfAbsent( final PreferenceScope scope,
                          final String key,
                          final T value );

    <T> void putIfAbsent( final PreferenceScopeResolutionStrategy scopeResolutionStrategy,
                          final String key,
                          final T value );

    <T> void putIfAbsent( final String key,
                          final T value );

    <T> void putIfAbsent( final PreferenceScope scope,
                          final Map<String, T> valueByKey );

    <T> void putIfAbsent( final PreferenceScopeResolutionStrategy scopeResolutionStrategy,
                          final Map<String, T> valueByKey );

    <T> void putIfAbsent( final Map<String, T> valueByKey );

    <T> T get( final PreferenceScope scope,
               final String key );

    <T> T get( final PreferenceScope scope,
               final String key,
               final T defaultValue );

    <T> T get( final PreferenceScopeResolutionStrategy scopeResolutionStrategy,
               final String key );

    <T> T get( final PreferenceScopeResolutionStrategy scopeResolutionStrategy,
               final String key,
               final T defaultValue );

    <T> T get( final String key );

    <T> T get( final String key,
               final T defaultValue );

    <T> PreferenceScopedValue<T> getScoped( final PreferenceScopeResolutionStrategy scopeResolutionStrategy,
                                            final String key );

    <T> PreferenceScopedValue<T> getScoped( final PreferenceScopeResolutionStrategy scopeResolutionStrategy,
                                            final String key,
                                            final T defaultValue );

    <T> PreferenceScopedValue<T> getScoped( final String key );

    <T> PreferenceScopedValue<T> getScoped( final String key,
                                            final T defaultValue );

    Map<String, Object> search( final PreferenceScope scope,
                                final Collection<String> keys );

    Map<String, Object> search( final PreferenceScopeResolutionStrategy scopeResolutionStrategy,
                                final Collection<String> keys );

    Map<String, Object> search( final Collection<String> keys );

    Map<String, PreferenceScopedValue<Object>> searchScoped( final PreferenceScopeResolutionStrategy scopeResolutionStrategy,
                                                             final Collection<String> keys );

    Map<String, PreferenceScopedValue<Object>> searchScoped( final Collection<String> keys );

    Map<String, Object> all( final PreferenceScopeResolutionStrategy scopeResolutionStrategy );

    Map<String, Object> all( final PreferenceScope scope );

    Map<String, Object> all();

    Map<String, PreferenceScopedValue<Object>> allScoped( final PreferenceScopeResolutionStrategy scopeResolutionStrategy );

    Map<String, PreferenceScopedValue<Object>> allScoped();

    void remove( final PreferenceScope scope,
                 final String key );

    void clear( final List<PreferenceScope> scopes,
                final String key );
}
