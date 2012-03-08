/**
 * 
 */
package org.ow2.sirocco.apis.rest.cimi.resource;

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

    /*
     * (non-Javadoc)
     * @see
     * org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager#createMachineImage
     * (org.ow2.sirocco.cloudmanager.model.cimi.MachineImage)
     */
    @Override
    public Job createMachineImage(MachineImage paramMachineImage) throws CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager#getMachineImages
     * ()
     */
    @Override
    public List<MachineImage> getMachineImages() throws CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * @see org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager#
     *      getMachineImageById(java.lang.String)
     */
    @Override
    public MachineImage getMachineImageById(String paramString) throws ResourceNotFoundException,
            CloudProviderException {

        MachineImage image = new MachineImage();
        image.setId(834752);
        image.setDescription("descriptionValue");
        image.setName("nameValue");
        image.setState(MachineImage.State.AVAILABLE);
        image.setType(MachineImage.Type.IMAGE);
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("keyOne", "valueOne");
        properties.put("keyTwo", "valueTwo");
        properties.put("keyThree", "valueThree");
        image.setProperties(properties);
        Calendar cal = new GregorianCalendar(2012, 3, 7, 13, 25, 37);
        cal.add(Calendar.MILLISECOND, 987);
        image.setCreated(cal.getTime());
        return image;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager#deleteMachineImage
     * (java.lang.String)
     */
    @Override
    public void deleteMachineImage(String paramString) throws ResourceNotFoundException, CloudProviderException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * @see org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager#
     * updateMachineImageAttributes(java.lang.String, java.util.Map)
     */
    @Override
    public void updateMachineImageAttributes(String paramString, Map<String, Object> paramMap)
            throws ResourceNotFoundException, InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * @see
     * org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager#getMachineImages
     * (java.util.List, java.lang.String)
     */
    @Override
    public List<MachineImage> getMachineImages(List<String> paramList, String paramString)
            throws InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager#getMachineImages
     * (int, int, java.util.List)
     */
    @Override
    public List<MachineImage> getMachineImages(int paramInt1, int paramInt2, List<String> paramList)
            throws InvalidRequestException, CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager#
     * getMachineImageCollection()
     */
    @Override
    public MachineImageCollection getMachineImageCollection() throws CloudProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.ow2.sirocco.cloudmanager.core.api.IMachineImageManager#
     * updateMachineImageCollection(java.util.Map)
     */
    @Override
    public void updateMachineImageCollection(Map<String, Object> paramMap) throws CloudProviderException {
        // TODO Auto-generated method stub

    }

}
