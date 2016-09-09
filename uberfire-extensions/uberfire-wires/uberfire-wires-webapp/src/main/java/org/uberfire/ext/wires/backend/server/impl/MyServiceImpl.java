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

package org.uberfire.ext.wires.backend.server.impl;

import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.ext.wires.shared.preferences.bean.MyPreference;
import org.uberfire.ext.wires.shared.preferences.bean.MyService;

@Service
public class MyServiceImpl implements MyService {

    @Inject
    private MyPreference myPreference;

    public void load() {
        myPreference.load();
        myPreference.setText( "atest1" );
        myPreference.setSendReports( true );
        myPreference.setBackgroundColor( "ABCDEF" );
        myPreference.setAge( 27 );
        myPreference.setPassword( "testPassword" );
        myPreference.getMyInnerPreference().setText( "atest2" );
        myPreference.getMyInheritedPreference().setText( "atest3" );
        myPreference.getMyInheritedPreference().getMyInnerPreference2().setText( "atest4" );
        myPreference.getMyInheritedPreference().getMyInnerPreference2().getMyInheritedPreference2().setText( "atest5" );
        myPreference.save();
        System.out.println("test");
    }
}