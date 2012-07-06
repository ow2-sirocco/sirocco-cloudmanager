package org.ow2.sirocco.apis.rest.cimi.request;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Container for all identifiers passed on a REST request.
 */
public class IdRequest implements Serializable {

    /** Serial number */
    private static final long serialVersionUID = 1L;

    /**
     * Enumerate all types of identifiers of a request.
     */
    public static enum Type {
        // If you add a type, be careful about sequencing of the
        // enumeration: youngest to oldest.
        // It's used to calculate the position in the list.
        RESOURCE, RESOURCE_PARENT, RESOURCE_GRAND_PARENT
    }

    /** The list of IDs */
    private List<String> idList;

    /**
     * Constructor by default.
     */
    public IdRequest() {
        this.idList = new ArrayList<String>();
    }

    /**
     * Constructor with all IDs of the resources ordered by type from youngest
     * to oldest.
     * <p>
     * By example, if they are 3 IDs : the first (index = 0) is the ID of the
     * current resource, the second (index = 1) is the ID of the parent
     * resource, the third (index = 2) is the ID of the grandparent resource.
     * </p>
     * 
     * @param ids A array with all IDs of the request ordered by type from
     *        youngest to oldest
     */
    public IdRequest(final String... ids) {
        this.idList = new ArrayList<String>();
        for (String id : ids) {
            this.idList.add(id);
        }
    }

    /**
     * Get all IDs of the resources.
     * 
     * @return A array with all IDs of the request stored ordered by type from
     *         oldest to youngest or null if none ID
     * @see #setIds(String[])
     */
    public List<String> getIdList() {
        return this.idList;
    }

    /**
     * Get the ID of a resource by type.
     * 
     * @param type The type of the ID to get
     * @return The requested ID or null if none ID exist in the given type
     * @see IdRequest.Type
     */
    public String getId(final IdRequest.Type type) {
        String id = null;
        if ((type.ordinal() < this.idList.size())) {
            id = this.idList.get(type.ordinal());
        }
        return id;
    }

    /**
     * Get the ID of the current resource, the youngest.
     * 
     * @return The ID of the youngest resource or null if none ID
     */
    public String getId() {
        return this.getId(IdRequest.Type.RESOURCE);
    }

    /**
     * Get the ID of the parent resource of the current.
     * 
     * @return The parent ID or null if none parent ID
     */
    public String getIdParent() {
        return this.getId(IdRequest.Type.RESOURCE_PARENT);
    }

    /**
     * Get the ID of the parent resource of the current.
     * 
     * @return The parent ID or null if none parent ID
     */
    public String[] makeArrayWithParents(final String id) {
        List<String> ids = new ArrayList<String>();
        for (int i = this.idList.size() - 1; i > 0; i--) {
            ids.add(this.idList.get(i));
        }
        if (null != id) {
            ids.add(id);
        }
        return ids.toArray(new String[ids.size()]);
    }
}