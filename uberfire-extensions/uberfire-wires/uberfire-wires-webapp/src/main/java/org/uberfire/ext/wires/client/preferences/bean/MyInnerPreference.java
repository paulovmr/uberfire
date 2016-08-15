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

package org.uberfire.ext.wires.client.preferences.bean;

import org.uberfire.ext.preferences.client.annotations.Property;
import org.uberfire.ext.preferences.client.annotations.WorkbenchPreference;
import org.uberfire.ext.preferences.shared.BasePreference;

@WorkbenchPreference
public abstract class MyInnerPreference implements BasePreference<MyInnerPreference> {

    @Property
    private String property;

    public MyInnerPreference() {
    }

    public MyInnerPreference( final String property ) {
        this.property = property;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty( final String property ) {
        this.property = property;
    }
}
