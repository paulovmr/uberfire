/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.backend.vfs.impl.LockInfo;
import org.uberfire.backend.vfs.impl.LockResult;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.resources.i18n.WorkbenchConstants;
import org.uberfire.client.workbench.VFSLockServiceProxy;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

/**
 * Default implementation of {@link LockManager} using the
 * {@link VFSLockServiceProxy} for lock management.
 */
@Dependent
public class LockManagerImpl implements LockManager {

    @Inject
    private VFSLockServiceProxy lockService;

    @Inject
    private javax.enterprise.event.Event<UpdatedLockStatusEvent> updatedLockStatusEvent;

    @Inject
    private javax.enterprise.event.Event<NotificationEvent> lockNotification;

    @Inject
    private LockDemandDetector lockDemandDetector;

    @Inject
    private User user;

    @Inject
    private WidgetLockUIHandler widgetLockUIHandler;

    private LockTarget lockTarget;
    private LockUIHandler lockUIHandler;

    private LockInfo lockInfo = LockInfo.unlocked();
    private HandlerRegistration closeHandler;

    private boolean lockRequestPending;
    private boolean unlockRequestPending;

    private boolean lockSyncComplete;
    private List<Runnable> syncCompleteRunnables = new ArrayList<Runnable>();

    private Timer reloadTimer;

    @Override
    public void init( final LockTarget lockTarget ) {
        init( lockTarget, widgetLockUIHandler );
    }

    @Override
    public void init( final LockTarget lockTarget,
                      final LockUIHandler lockUIHandler ) {
        this.lockTarget = lockTarget;
        this.lockUIHandler = lockUIHandler;

        final ParameterizedCommand<LockInfo> command = new ParameterizedCommand<LockInfo>() {

            @Override
            public void execute( final LockInfo lockInfo ) {
                if ( !lockRequestPending && !unlockRequestPending ) {
                    updateLockInfo( lockInfo );
                }
            }
        };
        lockService.retrieveLockInfo( lockTarget.getPath(),
                                      command );

        this.lockUIHandler.init( lockTarget,
                                 lockInfo,
                                 lockDemandDetector,
                                 user,
                                 new Callback<Event>() {
                                     @Override
                                     public void callback( final Event event ) {
                                         if ( isLockedByCurrentUser() ) {
                                             return;
                                         }

                                         if ( lockDemandDetector.isLockRequired( event ) ) {
                                             acquireLock();
                                         }
                                     }
                                 } );
    }

    @Override
    public void onFocus() {
        publishJsApi();
        lockUIHandler.fireChangeTitleEvent( lockInfo );
        fireUpdatedLockStatusEvent();
    }

    @Override
    public void acquireLockOnDemand() {
        lockUIHandler.acquireLockOnDemand();
    }

    @Override
    public void acquireLock() {
        if ( lockTarget == null ) {
            return;
        }
        if ( isLockedByCurrentUser() ) {
            lockUIHandler.fireChangeTitleEvent( lockInfo );
            return;
        }

        if ( lockInfo.isLocked() ) {
            handleLockFailure( lockInfo );
        } else if ( !lockRequestPending ) {
            lockRequestPending = true;
            final ParameterizedCommand<LockResult> command = new ParameterizedCommand<LockResult>() {

                @Override
                public void execute( final LockResult result ) {
                    if ( result.isSuccess() ) {
                        updateLockInfo( result.getLockInfo() );
                        releaseLockOnClose();
                    } else {
                        handleLockFailure( result.getLockInfo() );
                    }
                    lockRequestPending = false;
                }
            };
            lockService.acquireLock( lockTarget.getPath(),
                                     command );
        }
    }

    @Override
    public void releaseLock() {
        final Runnable releaseLock = new Runnable() {

            @Override
            public void run() {
                releaseLockInternal();
            }
        };
        if ( lockSyncComplete ) {
            releaseLock.run();
        } else {
            syncCompleteRunnables.add( releaseLock );
        }
    }

    private void releaseLockInternal() {
        if ( isLockedByCurrentUser() && !unlockRequestPending ) {
            unlockRequestPending = true;

            ParameterizedCommand<LockResult> command = new ParameterizedCommand<LockResult>() {

                @Override
                public void execute( final LockResult result ) {
                    updateLockInfo( result.getLockInfo() );

                    if ( result.isSuccess() ) {
                        if ( closeHandler != null ) {
                            closeHandler.removeHandler();
                        }
                    }

                    unlockRequestPending = false;
                }
            };
            lockService.releaseLock( lockTarget.getPath(),
                                     command );
        }
    }

    private void releaseLockOnClose() {
        closeHandler = Window.addWindowClosingHandler( new ClosingHandler() {
            @Override
            public void onWindowClosing( ClosingEvent event ) {
                releaseLock();
            }
        } );
    }

    private void handleLockFailure( final LockInfo lockInfo ) {

        if ( lockInfo != null ) {
            updateLockInfo( lockInfo );
            lockNotification.fire( new NotificationEvent( WorkbenchConstants.INSTANCE.lockedMessage( lockInfo.lockedBy() ),
                                                          NotificationEvent.NotificationType.INFO,
                                                          true,
                                                          lockTarget.getPlace(),
                                                          20 ) );
        } else {
            lockNotification.fire( new NotificationEvent( WorkbenchConstants.INSTANCE.lockError(),
                                                          NotificationEvent.NotificationType.ERROR,
                                                          true,
                                                          lockTarget.getPlace(),
                                                          20 ) );
        }
        // Delay reloading slightly in case we're dealing with a flood of events
        if ( reloadTimer == null ) {
            reloadTimer = new Timer() {

                public void run() {
                    reload();
                }
            };
        }

        if ( !reloadTimer.isRunning() ) {
            reloadTimer.schedule( 250 );
        }
    }

    private void reload() {
        lockTarget.getReloadRunnable().run();
    }

    private boolean isLockedByCurrentUser() {
        return lockInfo.isLocked() && lockInfo.lockedBy().equals( user.getIdentifier() );
    }

    private void updateLockInfo( @Observes LockInfo lockInfo ) {
        if ( lockTarget != null && lockInfo.getFile().equals( lockTarget.getPath() ) ) {
            this.lockInfo = lockInfo;
            this.lockSyncComplete = true;

            lockUIHandler.fireChangeTitleEvent( lockInfo );
            fireUpdatedLockStatusEvent();

            for ( Runnable runnable : syncCompleteRunnables ) {
                runnable.run();
            }
            syncCompleteRunnables.clear();
        }
    }

    void onResourceAdded( @Observes ResourceAddedEvent res ) {
        if ( lockTarget != null && res.getPath().equals( lockTarget.getPath() ) ) {
            releaseLock();
        }
    }

    void onResourceUpdated( @Observes ResourceUpdatedEvent res ) {
        if ( lockTarget != null && res.getPath().equals( lockTarget.getPath() ) ) {
            if ( !res.getSessionInfo().getIdentity().equals( user ) ) {
                reload();
            }
            releaseLock();
        }
    }

    void onSaveInProgress( @Observes SaveInProgressEvent evt ) {
        if ( lockTarget != null && evt.getPath().equals( lockTarget.getPath() ) ) {
            releaseLock();
        }
    }

    void onLockRequired( @Observes LockRequiredEvent event ) {
        if ( lockTarget != null && lockUIHandler.isVisible() && !isLockedByCurrentUser() ) {
            acquireLock();
        }
    }

    private native void publishJsApi()/*-{
        var lockManager = this;
        $wnd.isLocked = function () {
            return lockManager.@org.uberfire.client.mvp.LockManagerImpl::isLocked()();
        }
        $wnd.isLockedByCurrentUser = function () {
            return lockManager.@org.uberfire.client.mvp.LockManagerImpl::isLockedByCurrentUser()();
        }
        $wnd.acquireLock = function () {
            lockManager.@org.uberfire.client.mvp.LockManagerImpl::acquireLock()();
        }
        $wnd.releaseLock = function () {
            lockManager.@org.uberfire.client.mvp.LockManagerImpl::releaseLock()();
        }
        $wnd.reload = function () {
            return lockManager.@org.uberfire.client.mvp.LockManagerImpl::reload()();
        }
    }-*/;

    private boolean isLocked() {
        return lockInfo.isLocked();
    }

    protected LockInfo getLockInfo() {
        return lockInfo;
    }

    protected void fireUpdatedLockStatusEvent() {
        if ( lockUIHandler.isVisible() ) {
            updatedLockStatusEvent.fire( new UpdatedLockStatusEvent( lockInfo.getFile(),
                                                                     lockInfo.isLocked(),
                                                                     isLockedByCurrentUser() ) );
        }
    }

}