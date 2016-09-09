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

import java.lang.RuntimeException;
import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.ext.preferences.shared.annotations.PortablePreference;
<#if rootPreference>
import org.uberfire.ext.preferences.shared.annotations.RootPreference;
</#if>
import org.uberfire.ext.preferences.shared.bean.BasePreferencePortable;
import org.uberfire.ext.preferences.shared.PropertyFormType;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

@Portable
@PortablePreference
<#if rootPreference>
@RootPreference
</#if>
public class ${targetClassName} extends ${sourceClassName} implements BasePreferencePortable<${sourceClassName}> {

    public ${targetClassName}() {
    }

    public ${targetClassName}( ${constructorParamsText} ) {
    <#list properties as property>
        set${property.getCapitalizedFieldName()}( ${property.getFieldName()} );
    </#list>
    }

    @Override
    public void load() {
    }

    @Override
    public void load( final ParameterizedCommand<Throwable> errorCallback ) {
    }

    @Override
    public void load( final ParameterizedCommand<${sourceClassName}> successCallback,
                      final ParameterizedCommand<Throwable> errorCallback ) {
    }

    @Override
    public void save() {
    }

    @Override
    public void save( final ParameterizedCommand<Throwable> errorCallback ) {
    }

    @Override
    public void save( final Command successCallback,
                      final ParameterizedCommand<Throwable> errorCallback ) {
    }

    @Override
    public Class<${sourceClassName}> getPojoClass() {
        return ${sourceClassName}.class;
    }

    @Override
    public String key() {
        return "${preferenceKey}";
    }

    @Override
    public void set( String property, Object value ) {
    <#list simpleProperties as property>
        if ( property.equals( "${property.getFieldName()}" ) ) {
            set${property.getCapitalizedFieldName()}( (${property.getTypeFullName()}) value );
        } else
    </#list>
        {
            throw new RuntimeException( "Unknown property: " + property );
        }
    }

    @Override
    public Object get( String property ) {
    <#list simpleProperties as property>
        if ( property.equals( "${property.getFieldName()}" ) ) {
            return get${property.getCapitalizedFieldName()}();
        } else
    </#list>
        {
            throw new RuntimeException( "Unknown property: " + property );
        }
    }

    @Override
    public Map<String, PropertyFormType> getPropertiesTypes() {
        Map<String, PropertyFormType> propertiesTypes = new HashMap<>();

    <#list simpleProperties as property>
        propertiesTypes.put( "${property.getFieldName()}", PropertyFormType.${property.getFormType()});
    </#list>
        
        return propertiesTypes;
    }
}