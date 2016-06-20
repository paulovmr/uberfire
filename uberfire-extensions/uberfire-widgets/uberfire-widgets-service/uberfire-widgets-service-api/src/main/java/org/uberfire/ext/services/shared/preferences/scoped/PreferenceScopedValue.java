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

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class PreferenceScopedValue<T> {

    private final T value;

    private final String scopeKey;

    public PreferenceScopedValue( @MapsTo( "value" ) final T value,
                                  @MapsTo( "scopeKey" ) final String scopeKey ) {
        this.value = value;
        this.scopeKey = scopeKey;
    }

    public T getValue() {
        return value;
    }

    public String getScopeKey() {
        return scopeKey;
    }
}
