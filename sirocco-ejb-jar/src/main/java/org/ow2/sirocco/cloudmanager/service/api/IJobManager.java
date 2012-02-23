package org.ow2.sirocco.cloudmanager.service.api;

import java.util.Map;

import org.ow2.sirocco.cloudmanager.provider.api.entity.CloudEntity;
import org.ow2.sirocco.cloudmanager.provider.api.entity.Job;
import org.ow2.sirocco.cloudmanager.provider.api.exception.CloudProviderException;

import com.google.common.util.concurrent.ListenableFuture;

public interface IJobManager {

    static final String EJB_JNDI_NAME = "JobManagerBean";

    <T> Job createJob(String name, String description, String action, CloudEntity targetEntity, Map<String, String> properties,
        ListenableFuture<T> result) throws CloudProviderException;

    <T> Job createJob(String name, String description, String action, CloudEntity targetEntity, ListenableFuture<T> result)
        throws CloudProviderException;

    Job getJobById(final String jobId) throws InvalidJobException, CloudProviderException;

    void updateJob(String name, String description, String action, CloudEntity targetEntity, Map<String, String> properties)
        throws CloudProviderException;

    void deleteJob(final String jobId) throws CloudProviderException;

}
