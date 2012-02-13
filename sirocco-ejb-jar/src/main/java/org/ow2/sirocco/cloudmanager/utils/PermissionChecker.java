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

package org.ow2.sirocco.cloudmanager.utils;

import org.ow2.sirocco.cloudmanager.common.jndilocator.ServiceLocator;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Machine;
import org.ow2.sirocco.cloudmanager.provider.api.entity.MachineImage;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Project;
import org.ow2.sirocco.cloudmanager.provider.api.entity.RoleAssignment.Rights;
import org.ow2.sirocco.cloudmanager.provider.api.entity.User;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Volume;
import org.ow2.sirocco.cloudmanager.service.api.IRemoteUserProjectManager;
import org.ow2.sirocco.cloudmanager.service.api.IUserProjectManager;

public class PermissionChecker {

    public PermissionChecker() {
        this.initialize();
    }

    private transient IRemoteUserProjectManager projectManager;

    private void initialize() {
        this.projectManager = (IRemoteUserProjectManager) ServiceLocator.getInstance().getRemoteObject(
            IUserProjectManager.EJB_JNDI_NAME);
    }

    public boolean canCreateVm(final User user, final Project project) {
        if (user.getAdmin()) {
            return true;
        } else if (this.projectManager.checkIfUserBelongsToProject(user.getUsername(), project.getProjectId())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean canStartVm(final User user, final Project project) {
        if (user.getAdmin()) {
            return true;
        } else if (this.projectManager.checkIfUserBelongsToProject(user.getUsername(), project.getProjectId())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean canCreateVolume(final User user, final Project project) {
        if (user.getAdmin()) {
            return true;
        } else if (this.projectManager.checkIfUserBelongsToProject(user.getUsername(), project.getProjectId())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean canUploadImage(final User user, final Project project) {
        if (user.getAdmin()) {
            return true;
        } else if (this.projectManager.checkIfUserBelongsToProject(user.getUsername(), project.getProjectId())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean canStopVm(final User user, final Machine vm) {
        if (user.getAdmin()) {
            return true;
        }
        if (user.equals(vm.getUser())) {
            return true;
        }
        if (Rights.ADMIN
            .equals(this.projectManager.getRightsOfUserInProject(user.getUsername(), vm.getProject().getProjectId()))) {
            return true;
        }
        return false;
    }

    public boolean canPauseVm(final User fromUser, final Machine vm) {
        if (fromUser.getAdmin()) {
            return true;
        }
        if (fromUser.equals(vm.getUser())) {
            return true;
        }
        if (Rights.ADMIN.equals(this.projectManager.getRightsOfUserInProject(fromUser.getUsername(), vm.getProject()
            .getProjectId()))) {
            return true;
        }
        return false;
    }

    public boolean canDestroyVm(final User fromUser, final Machine vm) {
        if (fromUser.getAdmin()) {
            return true;
        }
        if (fromUser.equals(vm.getUser())) {
            return true;
        }
        if (Rights.ADMIN.equals(this.projectManager.getRightsOfUserInProject(fromUser.getUsername(), vm.getProject()
            .getProjectId()))) {
            return true;
        }
        return false;
    }

    public boolean canDestroyVolume(final User fromUser, final Volume vol) {
        if (fromUser.getAdmin()) {
            return true;
        }
        if (fromUser.equals(vol.getUser())) {
            return true;
        }
        if (Rights.ADMIN.equals(this.projectManager.getRightsOfUserInProject(fromUser.getUsername(), vol.getProject()
            .getProjectId()))) {
            return true;
        }
        return false;
    }

    public boolean canRebootVM(final User fromUser, final Machine vm) {
        if (fromUser.getAdmin()) {
            return true;
        }
        if (fromUser.equals(vm.getUser())) {
            return true;
        }
        if (Rights.ADMIN.equals(this.projectManager.getRightsOfUserInProject(fromUser.getUsername(), vm.getProject()
            .getProjectId()))) {
            return true;
        }
        return false;
    }

    public boolean canUpdateVirtualMachineExpirationDate(final User fromUser, final Machine vm) {
        if (fromUser.getAdmin()) {
            return true;
        }
        if (fromUser.equals(vm.getUser())) {
            return true;
        }
        if (Rights.ADMIN.equals(this.projectManager.getRightsOfUserInProject(fromUser.getUsername(), vm.getProject()
            .getProjectId()))) {
            return true;
        }
        return false;
    }

    public boolean canDestroyVmImage(final User fromUser, final MachineImage vmi) {
        if (fromUser.getAdmin()) {
            return true;
        }
        if (fromUser.equals(vmi.getUser())) {
            return true;
        }
        if (Rights.ADMIN.equals(this.projectManager.getRightsOfUserInProject(fromUser.getUsername(), vmi.getProject()
            .getProjectId()))) {
            return true;
        }
        return false;
    }

    public boolean canDestroyProject(final User fromUser, final Project project) {
        if (fromUser.getAdmin()) {
            return true;
        }
        return false;
    }

    public boolean canDestroyUser(final User fromUser) {
        if (fromUser.getAdmin()) {
            return true;
        }
        return false;
    }

    public boolean canImportOVF(final User user) {
        // XXX Can everyone import OVF files ?
        // if (user.getAdmin()) {
        // return true;
        // } else {
        // return true;
        // }
        return true;
    }

    public boolean canManageSystemInstances(final User user) {
        // XXX Can everyone manage systeminstances ?
        // if (user.getAdmin()) {
        // return true;
        // } else {
        // return true;
        // }
        return true;
    }

    public boolean canManageSystemTemplates(final User user) {
        // XXX Can everyone manage systeminstances ?
        // if (user.getAdmin()) {
        // return true;
        // } else {
        // return true;
        // }
        return true;
    }

    public boolean canManageCloudProviders(final User user) {
        // Only super-admin, i.e. admin, can manage cloudProviders.
        return user.getAdmin();
    }

    public boolean canManageCloudProviderAccounts(final User user, final String projectId) {
        if (user.getAdmin()) {
            return true;
        } else if (Rights.ADMIN.equals(this.projectManager.getRightsOfUserInProject(user.getUsername(), projectId))) {
            return true;
        } else {
            return false;
        }
    }
}
