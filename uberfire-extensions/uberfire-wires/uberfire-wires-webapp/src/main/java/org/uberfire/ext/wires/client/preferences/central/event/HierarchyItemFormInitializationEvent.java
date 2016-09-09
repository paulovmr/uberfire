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

package org.uberfire.ext.wires.client.preferences.central.event;

public class HierarchyItemFormInitializationEvent extends AbstractHierarchyItemEvent {

    private Class<?> clazz;

    private Object preference;

    public HierarchyItemFormInitializationEvent( final String itemId,
                                                 final Class<?> clazz,
                                                 final Object preference ) {
        super( itemId );
        this.clazz = clazz;
        this.preference = preference;
    }

    public <T> T getPreference() {
        Class<T> preferenceClass = (Class<T>) clazz;
        return (T) preference;
    }
}
