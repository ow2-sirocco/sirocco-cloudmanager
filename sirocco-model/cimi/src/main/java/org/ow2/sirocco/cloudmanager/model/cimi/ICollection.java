package org.ow2.sirocco.cloudmanager.model.cimi;

import java.util.Set;

public interface ICollection<A> {
    
    public Set<A> getItems();
    
    public void setItems(Set<A> items);

}
