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

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.common.client.api.RemoteCallback;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.uberfire.ext.preferences.client.ioc.store.PreferenceStore;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.Command;

@Dependent
public class ${targetClassName} extends ${sourceClassName} {

    @Inject
    private PreferenceStore store;

    // Inject instances for inherited preference properties
<#list inheritedSubPreferences as property>
    @Inject
    private ManagedInstance<${property.getTypeFullName()}> ${property.getFieldName()}Provider;
</#list>

    private boolean initialized = false;

    public ${targetClassName}() {
    }

    @Override
    public void load( final ParameterizedCommand<Throwable> errorCallback ) {
        load( null, errorCallback );
    }

    @Override
    public void load( final ParameterizedCommand<${sourceClassName}> successCallback,
                      final ParameterizedCommand<Throwable> errorCallback ) {
        final ${targetClassName} preference = this;

        store.get( getKey(), new RemoteCallback<${sourceClassName}>() {
            @Override
            public void callback( final ${sourceClassName} persistedPreference ) {
                if ( persistedPreference != null ) {
                    copyProperties( persistedPreference, preference );
                }

                initSubPreferences( errorCallback );
                
                if ( successCallback != null ) {
                    successCallback.execute( preference );
                }
            }
        }, ( message, throwable ) -> {
            if ( errorCallback != null ) {
                errorCallback.execute( throwable );
            }
            return false;
        } );
    }

    private void copyProperties( final ${sourceClassName} from,
                                 final ${sourceClassName} to ) {
    <#list properties as property>
        to.set${property.getCapitalizedFieldName()}( from.get${property.getCapitalizedFieldName()}() );
    </#list>
    }

    @Override
    public void save( final ParameterizedCommand<Throwable> errorCallback ) {
        save( null, errorCallback );
    }

    @Override
    public void save( final Command successCallback,
                      final ParameterizedCommand<Throwable> errorCallback ) {
        ${sourceClassName} preferenceToSave = new ${sourceClassName}ForStorageGeneratedImpl();
        saveSubPreferences( errorCallback );
        copyProperties( this, preferenceToSave );

        store.put( getKey(), preferenceToSave, result -> {
            if ( successCallback != null ) {
                successCallback.execute();
            }
        }, ( message, throwable ) -> {
            if ( errorCallback != null ) {
                errorCallback.execute( throwable );
            }
            return false;
        } );
    }

    @Override
    public void initSubPreferences( final ParameterizedCommand<Throwable> errorCallback ) {
        if ( initialized ) {
            return;
        }

        initialized = true;

        // Search for inherited preferences
    <#list inheritedSubPreferences as property>
        set${property.getCapitalizedFieldName()}( ${property.getFieldName()}Provider.get() );
        get${property.getCapitalizedFieldName()}().load( errorCallback );
    </#list>

        // Set null non-inherited sub-preferences
    <#list nonInheritedSubPreferences as property>
        if ( get${property.getCapitalizedFieldName()}() == null ) {
            set${property.getCapitalizedFieldName()}( new ${property.getTypeFullName()}ForStorageGeneratedImpl() );
        }
    </#list>

        // Init all non-inherited sub-preferences
    <#list nonInheritedSubPreferences as property>
        get${property.getCapitalizedFieldName()}().initSubPreferences( errorCallback );
    </#list>
    }  

    public String getKey() {
        return "${preferenceKey}";
    }
}
