/**
 *
 * SIROCCO
 * Copyright (C) 2013 Orange
 * Contact: sirocco@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 */
package org.ow2.sirocco.cloudmanager.core.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;

import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.Machine;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineVolume;
import org.ow2.sirocco.cloudmanager.model.cimi.Network;
import org.ow2.sirocco.cloudmanager.model.cimi.Volume;
import org.ow2.sirocco.cloudmanager.model.cimi.system.System;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Lock(LockType.WRITE)
public class ResourceWatcherManager {
    private static Logger logger = LoggerFactory.getLogger(ResourceWatcherManager.class.getName());

    @EJB
    private ResourceWatcher resourceWatcher;

    private List<Future<Void>> watchers = new ArrayList<>();

    @PreDestroy
    public void stopWatchers() {
        ResourceWatcherManager.logger.info("Stopping resource watchers...");
        int count = 0;
        for (Future<Void> watcher : this.watchers) {
            if (!watcher.isDone()) {
                watcher.cancel(true);
                count++;
            }
        }
        ResourceWatcherManager.logger.info(count + " resource watchers stopped");
    }

    @Schedule(minute = "*/5", persistent = false)
    public void cleanupWatchers() {
        ResourceWatcherManager.logger.info("Cleaning up resource watchers...");
        for (Iterator<Future<Void>> it = this.watchers.iterator(); it.hasNext();) {
            Future<Void> watcher = it.next();
            if (watcher.isDone()) {
                it.remove();
            }
        }
        ResourceWatcherManager.logger.info(this.watchers.size() + " resource watchers in progress");
    }

    public void createMachineStateWatcher(final Machine machine, final Job job, final Machine.State... expectedStates)
        throws CloudProviderException {
        Future<Void> watcher = this.resourceWatcher.watchMachine(machine, job, expectedStates);
        this.watchers.add(watcher);
    }

    public void createMachineImageStateWatcher(final MachineImage machineImage, final Job job,
        final MachineImage.State... expectedStates) throws CloudProviderException {
        Future<Void> watcher = this.resourceWatcher.watchMachineImage(machineImage, job, expectedStates);
        this.watchers.add(watcher);
    }

    public void createSystemStateWatcher(final System system, final Job job, final System.State... expectedStates)
        throws CloudProviderException {
        Future<Void> watcher = this.resourceWatcher.watchSystem(system, job, expectedStates);
        this.watchers.add(watcher);
    }

    public void createNetworkStateWatcher(final Network network, final Job job, final Network.State... expectedStates)
        throws CloudProviderException {
        Future<Void> watcher = this.resourceWatcher.watchNetwork(network, job, expectedStates);
        this.watchers.add(watcher);
    }

    public void createVolumeStateWatcher(final Volume volume, final Job job, final Volume.State... expectedStates)
        throws CloudProviderException {
        Future<Void> watcher = this.resourceWatcher.watchVolume(volume, job, expectedStates);
        this.watchers.add(watcher);
    }

    public void createVolumeAttachmentWatcher(final Machine machine, final MachineVolume volumeAttachement, final Job job,
        final MachineVolume.State... expectedStates) throws CloudProviderException {
        Future<Void> watcher = this.resourceWatcher.watchVolumeAttachment(machine, volumeAttachement, job, expectedStates);
        this.watchers.add(watcher);
    }

    public void createSystemWatcher(final org.ow2.sirocco.cloudmanager.model.cimi.system.System system, final Job job)
        throws CloudProviderException {
        Future<Void> watcher = this.resourceWatcher.watchSystem(system, job);
        this.watchers.add(watcher);

    }

}
