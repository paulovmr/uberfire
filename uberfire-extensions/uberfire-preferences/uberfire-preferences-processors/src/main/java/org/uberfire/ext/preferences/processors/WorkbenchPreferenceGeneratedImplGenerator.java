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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic.Kind;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.uberfire.annotations.processors.GeneratorUtils;
import org.uberfire.annotations.processors.exceptions.GenerationException;
import org.uberfire.ext.preferences.client.annotations.Property;
import org.uberfire.ext.preferences.client.annotations.WorkbenchPreference;

/**
 * A source code generator for {@link WorkbenchPreference}.
 */
public class WorkbenchPreferenceGeneratedImplGenerator extends AbstractGenerator {

    private GeneratorContext generatorContext;
    private String targetPackage = null;
    private String targetClassName = null;

    public WorkbenchPreferenceGeneratedImplGenerator( final GeneratorContext generatorContext ) {
        this.generatorContext = generatorContext;
    }

    @Override
    public StringBuffer generate( final String packageName,
                                  final PackageElement packageElement,
                                  final String className,
                                  final Element element,
                                  final ProcessingEnvironment processingEnvironment ) throws GenerationException {

        final Messager messager = processingEnvironment.getMessager();
        messager.printMessage( Kind.NOTE, "Starting code generation for [" + className + "]" );

        final Elements elementUtils = processingEnvironment.getElementUtils();

        final TypeElement classElement = (TypeElement) element;
        final String annotationName = WorkbenchPreferenceProcessor.WORKBENCH_PREFERENCE;

        String sourcePackage = packageName;
        String sourceClassName = className;
        String preferenceKey = packageName + "." + className;
        targetPackage = packageName;

        if ( GeneratorContext.CLIENT.equals( generatorContext ) ) {
            targetClassName = className + "ClientGeneratedImpl";
        } else if ( GeneratorContext.STORAGE.equals( generatorContext ) ) {
            targetClassName = className + "ForStorageGeneratedImpl";
        }

        List<PropertyData> properties = new ArrayList<>();

        TypeElement c = classElement;
        c.getEnclosedElements().forEach( el -> {
            final Property propertyAnnotation = el.getAnnotation( Property.class );
            if ( propertyAnnotation != null ) {
                messager.printMessage( Kind.NOTE, "aaaaaaaaaa [" + el.asType() + "]" );
                properties.add( new PropertyData( el, propertyAnnotation, elementUtils ) );
            }
        } );

        final List<PropertyData> subPreferences = properties.stream()
                .filter( p -> p.isSubPreference() )
                .collect( Collectors.toList() );

        final List<PropertyData> nonInheritedSubPreferences = subPreferences.stream()
                .filter( p -> !p.isInherited() )
                .collect( Collectors.toList() );

        final List<PropertyData> inheritedSubPreferences = subPreferences.stream()
                .filter( p -> p.isInherited() )
                .collect( Collectors.toList() );

        final List<String> constructorParams = properties.stream()
                .map( p -> "@MapsTo(\"" + p.getFieldName() + "\") " + p.getTypeFullName() + " " + p.getFieldName() )
                .collect( Collectors.toList() );
        final String constructorParamsText = String.join( ", ", constructorParams );

        final List<String> propertyFields = properties.stream()
                .map( PropertyData::getFieldName )
                .collect( Collectors.toList() );
        final String propertyFieldsText = String.join( ", ", propertyFields );

        if ( GeneratorUtils.debugLoggingEnabled() ) {
            final List<String> subPreferencesNames = subPreferences.stream()
                    .map( PropertyData::getFieldName )
                    .collect( Collectors.toList() );
            final String subPreferencesText = String.join( ", ", subPreferencesNames );

            final List<String> inheritedSubPreferencesNames = inheritedSubPreferences.stream()
                    .map( PropertyData::getFieldName )
                    .collect( Collectors.toList() );
            final String inheritedSubPreferencesText = String.join( ", ", inheritedSubPreferencesNames );

            final List<String> nonInheritedSubPreferencesNames = nonInheritedSubPreferences.stream()
                    .map( PropertyData::getFieldName )
                    .collect( Collectors.toList() );
            final String nonInheritedSubPreferencesText = String.join( ", ", nonInheritedSubPreferencesNames );

            messager.printMessage( Kind.NOTE, "Source package name: " + sourcePackage );
            messager.printMessage( Kind.NOTE, "Source class name: " + sourceClassName );
            messager.printMessage( Kind.NOTE, "Target package name: " + targetPackage );
            messager.printMessage( Kind.NOTE, "Target class name: " + targetClassName );
            messager.printMessage( Kind.NOTE, "Preference key: " + preferenceKey );
            messager.printMessage( Kind.NOTE, "Property fields: " + propertyFieldsText );
            messager.printMessage( Kind.NOTE, "Sub-preferences fields: " + subPreferencesText );
            messager.printMessage( Kind.NOTE, "Inherited subPreferences fields: " + inheritedSubPreferencesText );
            messager.printMessage( Kind.NOTE, "Non-inherited subPreferences fields: " + nonInheritedSubPreferencesText );
            messager.printMessage( Kind.NOTE, "Constructor parameters: " + constructorParamsText );
        }

        Map<String, Object> root = new HashMap<String, Object>();
        root.put( "sourcePackage",
                  sourcePackage );
        root.put( "sourceClassName",
                  sourceClassName );
        root.put( "targetPackage",
                  targetPackage );
        root.put( "targetClassName",
                  targetClassName );
        root.put( "preferenceKey",
                  preferenceKey );
        root.put( "properties",
                  properties );
        root.put( "subPreferences",
                  subPreferences );
        root.put( "inheritedSubPreferences",
                  inheritedSubPreferences );
        root.put( "nonInheritedSubPreferences",
                  nonInheritedSubPreferences );
        root.put( "constructorParamsText",
                  constructorParamsText );
        root.put( "propertyFieldsText",
                  propertyFieldsText );

        final StringWriter sw = new StringWriter();
        final BufferedWriter bw = new BufferedWriter( sw );
        try {
            Template template = null;
            if ( GeneratorContext.CLIENT.equals( generatorContext ) ) {
                template = config.getTemplate( "workbenchPreferenceClient.ftl" );
            } else if ( GeneratorContext.STORAGE.equals( generatorContext ) ) {
                template = config.getTemplate( "workbenchPreferenceForStorage.ftl" );
            }

            if ( template != null ) {
                template.process( root, bw );
            }
        } catch ( IOException ioe ) {
            throw new GenerationException( ioe );
        } catch ( TemplateException te ) {
            throw new GenerationException( te );
        } finally {
            try {
                bw.close();
                sw.close();
            } catch ( IOException ioe ) {
                throw new GenerationException( ioe );
            }
        }
        messager.printMessage( Kind.NOTE, "Successfully generated code for [" + className + "]" );

        return sw.getBuffer();
    }

    public String getTargetPackage() {
        return targetPackage;
    }

    public String getTargetClassName() {
        return targetClassName;
    }
}
