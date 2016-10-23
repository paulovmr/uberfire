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

package org.uberfire.ext.preferences.backend;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.uberfire.annotations.Customizable;
import org.uberfire.ext.preferences.backend.annotations.ComponentKey;
import org.uberfire.ext.preferences.shared.PreferenceScopeFactory;
import org.uberfire.ext.preferences.shared.PreferenceScopeResolutionStrategy;
import org.uberfire.ext.preferences.shared.PreferenceScopeTypes;
import org.uberfire.ext.preferences.shared.impl.DefaultPreferenceScopeResolutionStrategy;

@Dependent
public class PreferenceScopeResolutionStrategyProducer {

    @Inject
    private Instance<PreferenceScopeResolutionStrategy> preferenceScopeResolutionStrategy;

    @Inject
    private PreferenceScopeFactory scopeFactory;

    @Inject
    @Customizable
    private PreferenceScopeTypes scopeTypes;

    private DefaultPreferenceScopeResolutionStrategy defaultPreferenceScopeResolutionStrategy = null;

    @Produces
    @Customizable
    public PreferenceScopeResolutionStrategy preferenceScopeResolutionStrategyProducer( final InjectionPoint ip ) {
        if ( this.preferenceScopeResolutionStrategy.isUnsatisfied() ) {
            String componentKey = null;
            Annotation annotation = ip.getAnnotated().getAnnotation( ComponentKey.class );
            if ( annotation != null ) {
                componentKey = ( (ComponentKey) annotation ).value();
            }

            if ( defaultPreferenceScopeResolutionStrategy == null ) {
                defaultPreferenceScopeResolutionStrategy = new DefaultPreferenceScopeResolutionStrategy( scopeFactory,
                                                                                                         componentKey );
            }

            return defaultPreferenceScopeResolutionStrategy;
        }

        return this.preferenceScopeResolutionStrategy.get();
    }
}