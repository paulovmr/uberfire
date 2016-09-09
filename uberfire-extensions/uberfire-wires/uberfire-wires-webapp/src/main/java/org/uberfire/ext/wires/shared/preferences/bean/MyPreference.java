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

import org.uberfire.ext.preferences.shared.PropertyFormType;
import org.uberfire.ext.preferences.shared.annotations.Property;
import org.uberfire.ext.preferences.shared.annotations.WorkbenchPreference;
import org.uberfire.ext.preferences.shared.bean.BasePreference;

@WorkbenchPreference( root = true )
public abstract class MyPreference implements BasePreference<MyPreference> {

    @Property
    private String text;

    @Property( formType = PropertyFormType.BOOLEAN )
    private boolean sendReports;

    @Property( formType = PropertyFormType.COLOR )
    private String backgroundColor;

    @Property( formType = PropertyFormType.NATURAL_NUMBER )
    private int age;

    @Property( formType = PropertyFormType.SECRET_TEXT )
    private String password;

    @Property
    private MyInnerPreference myInnerPreference;

    @Property( inherited = true )
    private MyInheritedPreference myInheritedPreference;

    public String getText() {
        return text;
    }

    public void setText( final String text ) {
        this.text = text;
    }

    public boolean getSendReports() {
        return sendReports;
    }

    public void setSendReports( final boolean sendReports ) {
        this.sendReports = sendReports;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor( final String backgroundColor ) {
        this.backgroundColor = backgroundColor;
    }

    public int getAge() {
        return age;
    }

    public void setAge( final int age ) {
        this.age = age;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword( final String password ) {
        this.password = password;
    }

    public MyInnerPreference getMyInnerPreference() {
        return myInnerPreference;
    }

    public void setMyInnerPreference( final MyInnerPreference myInnerPreference ) {
        this.myInnerPreference = myInnerPreference;
    }

    public MyInheritedPreference getMyInheritedPreference() {
        return myInheritedPreference;
    }

    public void setMyInheritedPreference( final MyInheritedPreference myInheritedPreference ) {
        this.myInheritedPreference = myInheritedPreference;
    }
}