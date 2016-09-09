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


package org.uberfire.ext.preferences.shared.bean;

import java.util.Map;

import org.uberfire.ext.preferences.shared.PropertyFormType;

public interface BasePreferencePortable<T> extends Preference,
                                                   BasePreference<T>,
                                                   PortablePreference {

    Class<T> getPojoClass();

    String key();

    void set( String property, Object value );

    Object get( String property );

    Map<String, PropertyFormType> getPropertiesTypes();

    default PropertyFormType getPropertyType( String propertyName ) {
        return getPropertiesTypes().get( propertyName );
    }
}
