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

package org.uberfire.ext.preferences.shared.bean;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class PreferenceHierarchyElement<T> {

    private String id;

    private BasePreferencePortable<T> portablePreference;

    private PreferenceHierarchyElement<?> parent;

    private List<PreferenceHierarchyElement<?>> children;

    private boolean inherited;

    private boolean root;

    private boolean modified;

    public PreferenceHierarchyElement() {
    }

    public PreferenceHierarchyElement( final String id,
                                       final BasePreferencePortable<T> portablePreference,
                                       final PreferenceHierarchyElement<?> parent,
                                       final boolean inherited,
                                       final boolean root ) {

        this( id, portablePreference, parent, new ArrayList<>(), inherited, root, false );
    }

    public PreferenceHierarchyElement( @MapsTo( "id" ) final String id,
                                       @MapsTo( "portablePreference" ) final BasePreferencePortable<T> portablePreference,
                                       @MapsTo( "parent" ) final PreferenceHierarchyElement<?> parent,
                                       @MapsTo( "children" ) final List<PreferenceHierarchyElement<?>> children,
                                       @MapsTo( "inherited" ) final boolean inherited,
                                       @MapsTo( "root" ) final boolean root,
                                       @MapsTo( "modified" ) final boolean modified ) {
        this.id = id;
        this.portablePreference = portablePreference;
        this.parent = parent;
        this.children = children;
        this.inherited = inherited;
        this.root = root;
        this.modified = modified;
    }

    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }

    public String getId() {
        return id;
    }

    public void setId( final String id ) {
        this.id = id;
    }

    public BasePreferencePortable<T> getPortablePreference() {
        return portablePreference;
    }

    public void setPortablePreference( final BasePreferencePortable<T> portablePreference ) {
        this.portablePreference = portablePreference;
    }

    public PreferenceHierarchyElement<?> getParent() {
        return parent;
    }

    public void setParent( final PreferenceHierarchyElement<?> parent ) {
        this.parent = parent;
    }

    public List<PreferenceHierarchyElement<?>> getChildren() {
        return children;
    }

    public void setChildren( final List<PreferenceHierarchyElement<?>> children ) {
        this.children = children;
    }

    public boolean isInherited() {
        return inherited;
    }

    public void setInherited( final boolean inherited ) {
        this.inherited = inherited;
    }

    public boolean isRoot() {
        return root;
    }

    public void setRoot( final boolean root ) {
        this.root = root;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified( final boolean modified ) {
        this.modified = modified;
    }
}
