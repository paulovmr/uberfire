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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.ext.preferences.shared.PreferenceStore;
import org.uberfire.ext.preferences.shared.annotations.Property;
import org.uberfire.ext.preferences.shared.annotations.RootPreference;
import org.uberfire.ext.preferences.shared.annotations.WorkbenchPreference;
import org.uberfire.ext.preferences.shared.bean.BasePreference;
import org.uberfire.ext.preferences.shared.bean.BasePreferencePortable;
import org.uberfire.ext.preferences.shared.bean.PortablePreference;
import org.uberfire.ext.preferences.shared.bean.Preference;
import org.uberfire.ext.preferences.shared.bean.PreferenceBeanServerStore;
import org.uberfire.ext.preferences.shared.bean.PreferenceHierarchyElement;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

@Service
public class PreferenceBeanStoreImpl implements PreferenceBeanServerStore {

    private static final AnnotationLiteral<org.uberfire.ext.preferences.shared.annotations.PortablePreference> portablePreferenceAnnotation = new AnnotationLiteral<org.uberfire.ext.preferences.shared.annotations.PortablePreference>() { };

    private static final AnnotationLiteral<RootPreference> rootPreferenceAnnotation = new AnnotationLiteral<RootPreference>() { };

    private PreferenceStore preferenceStore;

    private Instance<Preference> preferences;

    //private Instance<PortablePreference> portablePreferences;

    public PreferenceBeanStoreImpl() {
    }

    @Inject
    public PreferenceBeanStoreImpl( final PreferenceStore preferenceStore,
                                    @org.uberfire.ext.preferences.shared.annotations.PortablePreference final Instance<Preference> preferences
                                  ){//@Any final Instance<PortablePreference> portablePreferences ) {
        this.preferenceStore = preferenceStore;
        this.preferences = preferences;
        //this.portablePreferences = portablePreferences;
    }

    @Override
    public <U extends BasePreference<U>, T extends BasePreferencePortable<U>> T load( final T emptyPortablePreference ) {
        Class<U> clazz = emptyPortablePreference.getPojoClass();
        T portablePreference = preferenceStore.get( emptyPortablePreference.key() );

        try {
            return load( clazz, portablePreference );
        } catch ( IllegalAccessException e ) {
            throw new RuntimeException( e );
        }
    }

    @Override
    public <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void load( final T emptyPortablePreference,
                                                                                         final ParameterizedCommand<T> successCallback,
                                                                                         final ParameterizedCommand<Throwable> errorCallback ) {
        T loadedPreference = null;
        try {
            loadedPreference = load( emptyPortablePreference );
        } catch ( Exception e ) {
            if ( errorCallback != null ) {
                errorCallback.execute( e );
            }
        }

        if ( successCallback != null ) {
            successCallback.execute( loadedPreference );
        }
    }

    @Override
    public <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void save( final T portablePreference ) {
        try {
            Class<U> clazz = portablePreference.getPojoClass();
            save( clazz, portablePreference );
            preferenceStore.put( portablePreference.key(), portablePreference );
        } catch ( IllegalAccessException e ) {
            throw new RuntimeException( e );
        }
    }

    @Override
    public void saveAll( final Collection<BasePreferencePortable<? extends BasePreference<?>>> portablePreferences ) {
        for ( BasePreferencePortable<? extends BasePreference<?>> portablePreference : portablePreferences ) {
            a( portablePreference );
        }

        /*portablePreferences.forEach( portablePreference -> {
            try {
                Class<?> clazz = portablePreference.getPojoClass();
                save( clazz, portablePreference );
                preferenceStore.put( portablePreference.key(), portablePreference );
            } catch ( IllegalAccessException e ) {
                throw new RuntimeException( e );
            }
        });*/
    }

    private <T extends BasePreference<T>> void a( final BasePreferencePortable<?> portablePreference ) {
        Class<T> clazz = (Class<T>) portablePreference.getPojoClass();
        try {
            save( clazz, (BasePreferencePortable<T>) portablePreference );
            preferenceStore.put( portablePreference.key(), portablePreference );
        } catch ( IllegalAccessException e ) {
            throw new RuntimeException( e );
        }
    }

    @Override
    public List<PreferenceHierarchyElement<?>> buildHierarchyStructure() {
        List<PreferenceHierarchyElement<?>> hierarchyRoots = new ArrayList<>();

        preferences.select( rootPreferenceAnnotation ).forEach( rootPreference -> {
            rootPreference = load( (BasePreferencePortable) rootPreference );

            final PreferenceHierarchyElement<?> childElement = buildHierarchyElement( (BasePreferencePortable) rootPreference,
                                                                                      null,
                                                                                      false,
                                                                                      true );
            hierarchyRoots.add( childElement );
        } );

        return hierarchyRoots;
    }

    private <T> PreferenceHierarchyElement<T> buildHierarchyElement( final BasePreferencePortable<T> portablePreference,
                                                                                               final PreferenceHierarchyElement<?> parent,
                                                                                               final boolean inherited,
                                                                                               final boolean root ) {
        PreferenceHierarchyElement<T> hierarchyElement = new PreferenceHierarchyElement<>( UUID.randomUUID().toString(),
                                                                                           portablePreference,
                                                                                           null,
                                                                                           inherited,
                                                                                           root );

        try {
            hierarchyElement.setPortablePreference( portablePreference );

            for ( Field field : portablePreference.getPojoClass().getDeclaredFields() ) {
                Property propertyAnnotation = field.getAnnotation( Property.class );
                if ( propertyAnnotation != null ) {
                    if ( field.getType().isAnnotationPresent( WorkbenchPreference.class ) ) {
                        field.setAccessible( true );
                        final BasePreferencePortable fieldValue = (BasePreferencePortable) field.get( portablePreference );

                        final PreferenceHierarchyElement<?> childElement = buildHierarchyElement( fieldValue,
                                                                                                  hierarchyElement,
                                                                                                  propertyAnnotation.inherited(),
                                                                                                  false );

                        hierarchyElement.getChildren().add( childElement );
                    }
                }
            }
        } catch ( IllegalAccessException e ) {
            e.printStackTrace();
        }

        return hierarchyElement;
    }

    @Override
    public <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void save( final T portablePreference,
                                                                                         final Command successCallback,
                                                                                         final ParameterizedCommand<Throwable> errorCallback ) {
        try {
            save( portablePreference );
        } catch ( Exception e ) {
            if ( errorCallback != null ) {
                errorCallback.execute( e );
            }
        }

        if ( successCallback != null ) {
            successCallback.execute();
        }
    }

    @Override
    public void saveAll( final Collection<BasePreferencePortable<? extends BasePreference<?>>> portablePreferences,
                         final Command successCallback,
                         final ParameterizedCommand<Throwable> errorCallback ) {
        try {
            saveAll( portablePreferences );
        } catch ( Exception e ) {
            if ( errorCallback != null ) {
                errorCallback.execute( e );
            }
        }

        if ( successCallback != null ) {
            successCallback.execute();
        }
    }

    private <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void save( final Class<U> clazz,
                                                                                          final T portablePreference ) throws IllegalAccessException {
        for ( Field field : portablePreference.getPojoClass().getDeclaredFields() ) {
            Property propertyAnnotation = field.getAnnotation( Property.class );
            if ( propertyAnnotation != null ) {
                if ( field.getType().isAnnotationPresent( WorkbenchPreference.class ) ) {
                    boolean inherited = propertyAnnotation.inherited();

                    field.setAccessible( true );

                    if ( inherited ) {
                        saveInheritedPreference( portablePreference, field );
                        field.set( portablePreference, null );
                    } else {
                        saveSubPreference( portablePreference, field );
                    }
                }
            }
        }
    }

    private <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void saveInheritedPreference( final Object portablePreference,
                                                                                                             final Field field ) throws IllegalAccessException {
        final Class<U> propertyType = (Class<U>) field.getType();
        final T inheritedPropertyValue = (T) field.get( portablePreference );
        save( inheritedPropertyValue, null, null );
    }

    private <U extends BasePreference<U>, T extends BasePreferencePortable<U>> void saveSubPreference( final Object portablePreference,
                                                                                                       final Field field ) throws IllegalAccessException {
        final Class<U> propertyType = (Class<U>) field.getType();
        final T subPreferenceValue = (T) field.get( portablePreference );
        save( propertyType, subPreferenceValue );
    }

    private <U extends BasePreference<U>, T extends BasePreferencePortable<U>> T load( final Class<U> clazz,
                                                                                       T portablePreference ) throws IllegalAccessException {
        if ( portablePreference == null ) {
            portablePreference = lookupPortablePreference( clazz );
        }

        for ( Field field : portablePreference.getPojoClass().getDeclaredFields() ) {
            Property propertyAnnotation = field.getAnnotation( Property.class );
            if ( propertyAnnotation != null ) {
                if ( field.getType().isAnnotationPresent( WorkbenchPreference.class ) ) {
                    final Class<? extends BasePreference<?>> propertyType = (Class<? extends BasePreference<?>>) field.getType();
                    boolean inherited = propertyAnnotation.inherited();

                    field.setAccessible( true );

                    if ( inherited ) {
                        BasePreferencePortable<?> loadedInheritedProperty = loadInheritedPreference( field );
                        field.set( portablePreference, loadedInheritedProperty );
                    } else {
                        final BasePreferencePortable<?> subPreferenceValue = loadSubPreferenceValue( portablePreference, field );
                        field.set( portablePreference, subPreferenceValue );
                    }
                }
            }
        }

        return portablePreference;
    }

    private <U extends BasePreference<U>, T extends BasePreferencePortable<U>> T loadInheritedPreference( final Field field ) {
        final Class<U> propertyType = (Class<U>) field.getType();
        T loadedPreference;

        try {
            T emptyPortablePreference = lookupPortablePreference( propertyType );
            T portablePreference = preferenceStore.get( emptyPortablePreference.key() );
            loadedPreference = load( propertyType, portablePreference );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }

        return loadedPreference;
    }

    private <U extends BasePreference<U>, T extends BasePreferencePortable<U>> T loadSubPreferenceValue( final Object portablePreference,
                                                                                                         final Field field ) throws IllegalAccessException {
        final Class<U> propertyType = (Class<U>) field.getType();
        final T subPreferenceValue = (T) field.get( portablePreference );
        return load( propertyType, subPreferenceValue );
    }

    private <U extends BasePreference<U>, T extends BasePreferencePortable<U>> T lookupPortablePreference( final Class<U> clazz ) {
        return (T) preferences.select( clazz, portablePreferenceAnnotation ).get();
    }
}