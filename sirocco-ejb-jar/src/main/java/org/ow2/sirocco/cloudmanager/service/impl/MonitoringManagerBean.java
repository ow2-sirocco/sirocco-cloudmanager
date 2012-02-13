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

package org.ow2.sirocco.cloudmanager.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.ow2.easybeans.osgi.annotation.OSGiResource;
import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudProviderAccount;
import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudProviderLocation;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Machine;
import org.ow2.sirocco.cloudmanager.provider.api.entity.PerfMetric;
import org.ow2.sirocco.cloudmanager.provider.api.entity.PerfMetricInfo;
import org.ow2.sirocco.cloudmanager.provider.api.entity.vo.PerfMetricInfoVO;
import org.ow2.sirocco.cloudmanager.provider.api.entity.vo.PerfMetricVO;
import org.ow2.sirocco.cloudmanager.provider.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidArgumentException;
import org.ow2.sirocco.cloudmanager.provider.api.exception.InvalidPerfMetricIdException;
import org.ow2.sirocco.cloudmanager.provider.api.service.ICloudProvider;
import org.ow2.sirocco.cloudmanager.provider.api.service.ICloudProviderFactory;
import org.ow2.sirocco.cloudmanager.provider.api.service.ICloudProviderFactoryFinder;
import org.ow2.sirocco.cloudmanager.provider.api.service.IMonitoringService;
import org.ow2.sirocco.cloudmanager.service.api.IMonitoringManager;

@Stateless(name = IMonitoringManager.EJB_JNDI_NAME, mappedName = IMonitoringManager.EJB_JNDI_NAME)
@Remote(IMonitoringManager.class)
public class MonitoringManagerBean implements IMonitoringManager {
    private static Logger log = Logger.getLogger(MonitoringManagerBean.class.getName());

    @PersistenceContext(unitName = "persistence-unit/main", type = PersistenceContextType.TRANSACTION)
    private EntityManager em = null;

    @OSGiResource
    private ICloudProviderFactoryFinder cloudProviderFactoryFinder;

    private ICloudProvider getCloudProvider(final CloudProviderAccount cloudProviderAccount, final String location)
        throws CloudProviderException {
        ICloudProviderFactory cloudProviderFactory = this.cloudProviderFactoryFinder
            .getCloudProviderFactory(cloudProviderAccount.getCloudProvider().getCloudProviderType());
        return cloudProviderFactory.getCloudProviderInstance(cloudProviderAccount, new CloudProviderLocation(location));
    }

    @Override
    public List<PerfMetricInfoVO> listPerfMetricInfos(final Entity type, final String id) throws InvalidArgumentException,
        CloudProviderException {

        Machine vm = null;
        if (type.equals(IMonitoringManager.Entity.VIRTUALMACHINE)) {
            vm = this.em.find(Machine.class, Integer.valueOf(id));
            if (vm == null) {
                throw new InvalidArgumentException("Illegal VM Id " + id);
            }

        }

        try {
            IMonitoringService monitoringService = this.getCloudProvider(vm.getCloudProviderAccount(), vm.getLocation())
                .getMonitoringService();
            List<PerfMetricInfo> metricInfos = monitoringService.getAvailableMachinePerfMetrics(vm.getProviderAssignedId());

            List<PerfMetricInfoVO> result = new ArrayList<PerfMetricInfoVO>();
            if (metricInfos != null) {
                for (PerfMetricInfo pmi : metricInfos) {
                    PerfMetricInfoVO pmitmp = new PerfMetricInfoVO();
                    pmitmp.setName(pmi.getName());
                    pmitmp.setUnit(pmi.getUnit().toString());
                    pmitmp.setDescription(pmi.getDescription());
                    result.add(pmitmp);
                }
            }
            return result;
        } catch (Exception ex) {
            throw new CloudProviderException(ex.getMessage());
        }
    }

    private PerfMetricInfo findMetricInfoByName(final String name, final List<PerfMetricInfo> metricInfos) {
        for (PerfMetricInfo info : metricInfos) {
            if (info.getName().equals(name)) {
                return info;
            }
        }
        return null;
    }

    public List<PerfMetricVO> getPerfMetrics(final Entity type, final String id, final String metricId, final Date startTime,
        Date endTime) throws InvalidArgumentException, InvalidPerfMetricIdException, CloudProviderException {
        if (endTime == null) {
            endTime = new Date();
        }
        MonitoringManagerBean.log.info("getPerfMetrics id=" + id + " type=" + type + " metricId" + metricId + " start="
            + startTime + " endTime=" + endTime);

        if (type == Entity.VIRTUALMACHINE) {
            Machine vm = this.em.find(Machine.class, Integer.valueOf(id));
            if (vm == null) {
                throw new InvalidArgumentException("Illegal VM Id " + id);
            }
            try {
                IMonitoringService monitoringService = this.getCloudProvider(vm.getCloudProviderAccount(), vm.getLocation())
                    .getMonitoringService();
                List<PerfMetricInfo> metricInfos = monitoringService.getAvailableMachinePerfMetrics(vm.getProviderAssignedId());
                PerfMetricInfo metricInfo = this.findMetricInfoByName(metricId, metricInfos);
                if (metricInfo == null) {
                    throw new InvalidPerfMetricIdException("Invalid metric id " + metricId);
                }
                List<PerfMetric> metrics = monitoringService.getMachinePerfMetrics(vm.getProviderAssignedId(), metricInfo,
                    startTime, endTime);

                List<PerfMetricVO> result = new ArrayList<PerfMetricVO>();
                for (PerfMetric metric : metrics) {
                    result.add(PerfMetricVO.from(metric));
                }
                return result;
            } catch (Exception ex) {
                throw new CloudProviderException(ex.getMessage());
            }
        } else {
            throw new CloudProviderException("Unsupported entity " + type);
        }
    }

}
