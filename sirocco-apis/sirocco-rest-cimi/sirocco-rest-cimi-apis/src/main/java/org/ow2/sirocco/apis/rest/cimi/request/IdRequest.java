package org.ow2.sirocco.apis.rest.cimi.request;

import java.util.ArrayList;
import java.util.List;

public class IdRequest {
    /** The seniority for the current ID (youngest) */
    public static final int ID_CURRENT = 0;

    /** The seniority for the parent ID */
    public static final int ID_PARENT = 1;

    /** The seniority for the grandparent ID */
    public static final int ID_GRAND_PARENT = 2;

    /** The list of IDs */
    private List<String> idList;

    /**
     * Constructor by default.
     */
    public IdRequest() {
        this.idList = new ArrayList<String>();
    }

    /**
     * Constructor with all IDs of the resources ordered by seniority from
     * youngest to oldest.
     * <p>
     * By example, if they are 3 IDs : the first (index = 0) is the ID of the
     * current resource, the second (index = 1) is the ID of the parent
     * resource, the third (index = 2) is the ID of the grandparent resource.
     * </p>
     * 
     * @param ids A array with all IDs of the request ordered by seniority from
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
     * @return A array with all IDs of the request stored ordered by seniority
     *         from oldest to youngest or null if none ID
     * @see #setIds(String[])
     */
    public List<String> getIdList() {
        return this.idList;
    }

    /**
     * Get the ID of a resource by seniority.
     * <p>
     * For the seniority, zero is the youngest and more the value is high, more
     * is older. By example,
     * <ul>
     * <li>with 0, you get the current ID</li>
     * <li>with 1, you get the parent ID</li>
     * <li>with 2, you get the grandparent ID</li>
     * <li>. . .</li>
     * </p>
     * 
     * @param seniority The seniority value of the ID to get
     * @return The requested ID or null if none ID exist in the given seniority
     * @see CimiRequest#ID_CURRENT
     * @see CimiRequest#ID_PARENT
     * @see CimiRequest#ID_GRAND_PARENT
     */
    public String getId(final int seniority) {
        String id = null;
        if ((seniority >= 0) && (seniority < this.idList.size())) {
            id = this.idList.get(seniority);
        }
        return id;
    }

    /**
     * Get the ID of the current resource, the youngest.
     * 
     * @return The ID of the youngest resource or null if none ID
     */
    public String getId() {
        return this.getId(IdRequest.ID_CURRENT);
    }

    /**
     * Get the ID of the parent resource of the current.
     * 
     * @return The parent ID or null if none parent ID
     */
    public String getIdParent() {
        return this.getId(IdRequest.ID_PARENT);
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