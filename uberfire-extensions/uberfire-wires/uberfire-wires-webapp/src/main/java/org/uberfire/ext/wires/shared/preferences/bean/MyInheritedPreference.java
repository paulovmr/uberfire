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

package org.uberfire.ext.wires.shared.preferences.bean;

import org.uberfire.ext.preferences.shared.annotations.Property;
import org.uberfire.ext.preferences.shared.annotations.WorkbenchPreference;
import org.uberfire.ext.preferences.shared.bean.BasePreference;

@WorkbenchPreference
public abstract class MyInheritedPreference implements BasePreference<MyInheritedPreference> {

    @Property
    private String text;

    @Property
    private MyInnerPreference2 myInnerPreference2;

    public String getText() {
        return text;
    }

    public void setText( final String text ) {
        this.text = text;
    }

    public MyInnerPreference2 getMyInnerPreference2() {
        return myInnerPreference2;
    }

    public void setMyInnerPreference2( final MyInnerPreference2 myInnerPreference2 ) {
        this.myInnerPreference2 = myInnerPreference2;
    }
}
