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

package ${targetPackage};

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.common.client.api.annotations.MapsTo;

import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.Command;
import org.uberfire.ext.preferences.client.annotations.Vetoed;

import ${sourcePackage}.${sourceClassName};

@Vetoed
@Portable
public class ${targetClassName} extends ${sourceClassName} {

    public ${targetClassName}() {
    }

    public ${targetClassName}( ${constructorParamsText} ) {
        super( ${propertyFieldsText} );
    }

    @Override
    public void load( final ParameterizedCommand<Throwable> errorCallback ) {
        // do nothing, class only for persistence
    }

    @Override
    public void load( final ParameterizedCommand<${sourceClassName}> successCallback,
                      final ParameterizedCommand<Throwable> errorCallback ) {
        // do nothing, class only for persistence
    }

    @Override
    public void save( final ParameterizedCommand<Throwable> errorCallback ) {
        // do nothing, class only for persistence
    }

    @Override
    public void save( final Command successCallback,
                      final ParameterizedCommand<Throwable> errorCallback ) {
        // do nothing, class only for persistence
    }

    @Override
    public void initSubPreferences( final ParameterizedCommand<Throwable> errorCallback ) {
        // do nothing, class only for persistence
    }
}
