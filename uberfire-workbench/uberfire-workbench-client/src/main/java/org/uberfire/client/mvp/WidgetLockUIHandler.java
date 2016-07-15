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

package org.uberfire.client.mvp;

import javax.inject.Inject;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.backend.vfs.impl.LockInfo;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;

public class WidgetLockUIHandler implements LockUIHandler {

    @Inject
    private javax.enterprise.event.Event<ChangeTitleWidgetEvent> changeTitleEvent;

    private LockTarget lockTarget;
    private LockInfo lockInfo;
    private LockDemandDetector lockDemandDetector;
    private User user;
    private Callback<Event> elementEventListener;

    @Override
    public void init( final LockTarget lockTarget,
                      final LockInfo lockInfo,
                      final LockDemandDetector lockDemandDetector,
                      final User user,
                      final Callback<Event> elementEventListener ) {
        this.lockTarget = lockTarget;
        this.lockInfo = lockInfo;
        this.lockDemandDetector = lockDemandDetector;
        this.user = user;
        this.elementEventListener = elementEventListener;
    }

    @Override
    public void fireChangeTitleEvent( final LockInfo lockInfo ) {
        this.lockInfo = lockInfo;
        changeTitleEvent.fire( LockTitleWidgetEvent.create( lockTarget,
                                                            lockInfo,
                                                            user ) );
    }

    @Override
    public void acquireLockOnDemand() {
        if ( lockTarget == null ) {
            return;
        }

        final Widget widget = getLockTargetWidget();
        final Element element = widget.getElement();
        acquireLockOnDemand( element );

        widget.addAttachHandler( new AttachEvent.Handler() {

            @Override
            public void onAttachOrDetach( AttachEvent event ) {
                // Handle widget reattachment/reparenting
                if ( event.isAttached() ) {
                    acquireLockOnDemand( element );
                }
            }
        } );
    }

    protected EventListener acquireLockOnDemand( final Element element ) {
        Event.sinkEvents( element,
                          lockDemandDetector.getLockDemandEventTypes() );

        EventListener lockDemandListener = new EventListener() {

            @Override
            public void onBrowserEvent( Event event ) {
                elementEventListener.callback( event );
            }
        };

        Event.setEventListener( element,
                                lockDemandListener );

        return lockDemandListener;
    }

    private Widget getLockTargetWidget() {
        final IsWidget isWidget = lockTarget.getWidget();
        if ( isWidget instanceof Widget ) {
            return ( (Widget) isWidget );
        }
        return isWidget.asWidget();
    }

    @Override
    public boolean isVisible() {
        final Widget widget = getLockTargetWidget();
        final Element element = widget.getElement();
        boolean visible = UIObject.isVisible( element ) &&
                ( element.getAbsoluteLeft() != 0 ) && ( element.getAbsoluteTop() != 0 );

        return visible;
    }

    private boolean isLockedByCurrentUser() {
        return lockInfo.isLocked() && lockInfo.lockedBy().equals( user.getIdentifier() );
    }
}
