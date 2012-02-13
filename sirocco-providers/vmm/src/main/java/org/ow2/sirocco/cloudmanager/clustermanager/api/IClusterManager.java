/**
 *
 * SIROCCO
 * Copyright (C) 2011 France Telecom
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
 *  $Id$
 *
 */

package org.ow2.sirocco.cloudmanager.clustermanager.api;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ow2.sirocco.vmm.api.ResourcePartitionMXBean;
import org.ow2.sirocco.vmm.api.ServerPoolMXBean;
import org.ow2.sirocco.vmm.api.VMMException;
import org.ow2.sirocco.vmm.api.VirtualMachineMXBean;
import org.ow2.sirocco.vmm.api.monitoring.PerfMetric;
import org.ow2.sirocco.vmm.api.monitoring.PerfMetricInfo;

public interface IClusterManager {
    // void createVirtualMachine(final VMConfigSpec spec, Map<String, String>
    // constraints, AllocationMode allocationMode)
    // throws VMMException;

    /**
     * @param spec
     * @param constraints
     * @param allocationMode
     * @param startVM
     * @return the vmId.
     * @throws VMMException
     */
    String createVirtualMachine(VirtualMachineCreationSpec spec, Map<String, String> constraints,
        AllocationMode allocationMode, boolean startVM) throws VMMException;

    String uploadVMImage(String url, String format, String name, String description, String checkSum) throws VMMException;

    /**
     * @param spec
     * @param handback
     * @return volume's providerId
     * @throws VMMException
     */
    String createVolume(VolumeSpec spec) throws VMMException;

    void destroyVolume(String providerId) throws VMMException;

    void attachVolumeToVM(String vmProviderId, String volumeId) throws VMMException;

    void detachVolumeFromVM(String vmProviderId, String volumeId) throws VMMException;

    List<String> getVMVolumes(String vmProviderId) throws VMMException;

    void stopVirtualMachine(String vmRef) throws VMMException;

    void destroyVirtualMachine(String vmRef) throws VMMException;

    void rebootVirtualMachine(String vmRef) throws VMMException;

    void pauseVirtualMachine(String vmRef) throws VMMException;

    void unpauseVirtualMachine(String vmRef) throws VMMException;

    void suspendVirtualMachine(String vmRef) throws VMMException;

    void resumeVirtualMachine(String vmRef) throws VMMException;

    String createImageFromVirtualMachine(String providerId, final String vmiName, final String vmiDescription)
        throws VMMException;

    void startVirtualMachine(String vmRef) throws VMMException;

    void migrateVirtualMachine(String vmRef, String hostRef) throws VMMException;

    String getVirtualMachineConsole(String vmRef) throws VMMException;

    List<String> getLocations();

    Domain getResourceTree();

    List<ServerPool> getServerPools();

    ResourcePartitionMXBean getResourcePartition(final AllocationMode allocationMode, final String hypervisorType,
        final String location);

    List<ResourcePartitionInfo> getResourcePartitions();

    List<Host> getHostRefFromServerPool(String poolRef);

    VirtualMachineMXBean getVirtualMachineMBean(String vmRef);

    List<String> getVirtualMachineRefByHost(final String hostRef);

    Map<String, Long> getVirtualMachineSchedulingParams(String vmRef);

    long getVirtualMachineMemoryUsedMB(String vmRef);

    float getVirtualMachineCPULoad(String vmRef);

    void destroyVMImage(String providerId) throws VMMException;

    // TODO may be move to List<PerfMetricInfo>.
    PerfMetricInfo[] getHostAvailablePerfMetrics(String hostId) throws VMMException;

    // TODO may be move to List<PerfMetricInfo>.
    PerfMetricInfo[] getVirtualMachineAvailablePerfMetrics(String vmRef) throws VMMException;

    // TODO may be move to List<PerfMetric>.
    PerfMetric[] getVirtualMachinePerfMetrics(final String vmRef, final String metricName, final Date startTime,
        final Date endTime) throws Exception;

    // TODO may be move to List<PerfMetric>.
    PerfMetric[] getHostPerfMetrics(String id, final String metricName, final Date startTime, final Date endTime)
        throws Exception;

    // TODO may be move to List<PerfMetric>.
    PerfMetric[] getServerPoolPerfMetrics(String id, final String metricName, final Date startTime, final Date endTime)
        throws Exception;

    Map<String, ServerPoolMXBean> getPoolMBeanTable();

}
