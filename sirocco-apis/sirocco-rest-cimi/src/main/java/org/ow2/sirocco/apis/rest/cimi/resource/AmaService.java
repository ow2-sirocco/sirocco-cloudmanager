/**
 * 
 */
package org.ow2.sirocco.apis.rest.cimi.resource;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 
 * 
 */
@Component("AmaService")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class AmaService {

    public String getAma() {
        return "Amadeus";
    }
}
