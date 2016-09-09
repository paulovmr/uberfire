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

package org.uberfire.ext.preferences.processors;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import org.uberfire.ext.preferences.shared.PropertyFormType;
import org.uberfire.ext.preferences.shared.annotations.Property;
import org.uberfire.ext.preferences.shared.annotations.WorkbenchPreference;

public class PropertyData {

    private String fieldName;

    private String capitalizedFieldName;

    private String typeFullName;

    private boolean inherited;

    private boolean subPreference;

    private PropertyFormType formType;

    public PropertyData( final Element element,
                         final Property propertyAnnotation,
                         final Elements elementUtils ) {
        fieldName = element.getSimpleName().toString();

        typeFullName = element.asType().toString();

        final char elementNameFirstLetter = fieldName.charAt( 0 );
        final char elementNameCapitalizedFirstLetter = Character.toUpperCase( elementNameFirstLetter );
        final String nameWithoutFirstLetter = fieldName.substring( 1 );
        capitalizedFieldName = elementNameCapitalizedFirstLetter + nameWithoutFirstLetter;

        inherited = propertyAnnotation.inherited();

        final TypeElement typeElement = elementUtils.getTypeElement( element.asType().toString() );
        subPreference = typeElement != null && typeElement.getAnnotation( WorkbenchPreference.class ) != null;

        formType = propertyAnnotation.formType();
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getCapitalizedFieldName() {
        return capitalizedFieldName;
    }

    public String getTypeFullName() {
        return typeFullName;
    }

    public boolean isInherited() {
        return inherited;
    }

    public boolean isSubPreference() {
        return subPreference;
    }

    public PropertyFormType getFormType() {
        return formType;
    }
}