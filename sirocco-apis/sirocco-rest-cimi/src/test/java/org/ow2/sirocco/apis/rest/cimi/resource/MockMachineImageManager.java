/**
 * 
 */
package org.ow2.sirocco.apis.rest.cimi.resource;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager;
import org.ow2.sirocco.cloudmanager.core.api.exception.CloudProviderException;
import org.ow2.sirocco.cloudmanager.core.api.exception.InvalidRequestException;
import org.ow2.sirocco.cloudmanager.core.api.exception.ResourceNotFoundException;
import org.ow2.sirocco.cloudmanager.model.cimi.Job;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImage;
import org.ow2.sirocco.cloudmanager.model.cimi.MachineImageCollection;

/**
 * 
 */
public class MockMachineImageManager implements IMachineImageManager {

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager#createMachineImage
     *      (org.ow2.sirocco.cloudmanager.model.cimi.MachineImage)
     */
    @Override
    public Job createMachineImage(final MachineImage paramMachineImage) throws CloudProviderException {
        return this.buildJob(1);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager#getMachineImages
     *      ()
     */
    @Override
    public List<MachineImage> getMachineImages() throws CloudProviderException {
        List<MachineImage> images = new ArrayList<MachineImage>();
        for (int i = 0; i < 3; i++) {
            images.add(this.buildMachineImage(i + 1));
        }
        return images;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager#
     *      getMachineImageById(java.lang.String)
     */
    @Override
    public MachineImage getMachineImageById(final String paramString) throws ResourceNotFoundException, CloudProviderException {
        Integer id = null;
        try {
            id = Integer.valueOf(paramString);
        } catch (Exception e) {
            throw new ResourceNotFoundException();
        }
        return this.buildMachineImage(id);
    }

    @Override
    public MachineImage getMachineImageAttributes(final String imageId, final List<String> attributes)
        throws ResourceNotFoundException, CloudProviderException {
        // TODO
        return null;
    }

    protected MachineImage buildMachineImage(final Integer id) {
        MachineImage image = new MachineImage();
        image.setId(id);
        image.setDescription("descriptionValue" + id);
        image.setName("nameValue" + id);
        image.setState(MachineImage.State.AVAILABLE);
        image.setType(MachineImage.Type.IMAGE);
        image.setImageLocation("hrefImageLocationValue");

        Map<String, String> properties = new HashMap<String, String>();
        properties.put("keyOne" + id, "valueOne" + id);
        properties.put("keyTwo" + id, "valueTwo" + id);
        properties.put("keyThree" + id, "valueThree" + id);
        image.setProperties(properties);

        Calendar cal = new GregorianCalendar(2012, 3, 7, 13, 25, 37);
        cal.add(Calendar.MILLISECOND, 987);
        image.setCreated(cal.getTime());
        return image;
    }

    protected Job buildJob(final Integer id) {
        Calendar cal;
        Job job = new Job();
        job.setId(id);
        job.setDescription("descriptionValue" + id);
        job.setName("nameValue" + id);

        job.setAction("actionValue");
        job.setIsCancellable(Boolean.FALSE);
        job.setProgress(50);
        job.setReturnCode(10);
        job.setStatus(Job.Status.RUNNING);
        job.setStatusMessage("statusMessageValue");
        job.setTargetEntity("targetEntityValue");

        cal = new GregorianCalendar(0, 0, 0, 1, 2, 3);
        cal.add(Calendar.MILLISECOND, 456);
        job.setTimeOfStatusChange(cal.getTime());

        Job jobOther = new Job();
        jobOther.setId(id + 10);
        job.setParentJob(jobOther);

        List<Job> list = new ArrayList<Job>();
        for (int i = 0; i < 3; i++) {
            jobOther = new Job();
            jobOther.setId(id + 100 + i);
            list.add(jobOther);
        }
        job.setNestedJobs(list);

        cal = new GregorianCalendar(2012, 3, 7, 13, 25, 37);
        cal.add(Calendar.MILLISECOND, 987);
        job.setCreated(cal.getTime());

        cal = new GregorianCalendar(2012, 3, 7, 13, 28, 12);
        cal.add(Calendar.MILLISECOND, 1);
        job.setUpdated(cal.getTime());
        return job;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager#deleteMachineImage
     *      (java.lang.String)
     */
    @Override
    public void deleteMachineImage(final String paramString) throws ResourceNotFoundException, CloudProviderException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager#
     *      updateMachineImageAttributes(java.lang.String, java.util.Map)
     */
    @Override
    public void updateMachineImageAttributes(final String paramString, final Map<String, Object> paramMap)
        throws ResourceNotFoundException, InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateMachineImage(final MachineImage machineImage) throws ResourceNotFoundException, InvalidRequestException,
        CloudProviderException {
        // TODO
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager#getMachineImages
     *      (java.util.List, java.lang.String)
     */
    @Override
    public List<MachineImage> getMachineImages(final List<String> paramList, final String paramString)
        throws InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager#getMachineImages
     *      (int, int, java.util.List)
     */
    @Override
    public List<MachineImage> getMachineImages(final int paramInt1, final int paramInt2, final List<String> paramList)
        throws InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager#
     *      getMachineImageCollection()
     */
    @Override
    public MachineImageCollection getMachineImageCollection() throws CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager#
     *      updateMachineImageCollection(java.util.Map)
     */
    @Override
    public void updateMachineImageCollection(final Map<String, Object> paramMap) throws CloudProviderException {
        // TODO Auto-generated method stub

    }

}
