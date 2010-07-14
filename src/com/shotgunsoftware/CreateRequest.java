package com.shotgunsoftware;

import java.util.Map;
import java.util.HashMap;

/**
 * The specification for a Create request.
 * 
 * @author      Matt Daw
 * @version     1.0
 */
public class CreateRequest {
    String entityType;
    Map data;
    
    /**
     * Sole Constructor
     * 
     * @param   entityType      the type of entity to create
     */
    public CreateRequest(String entityType) {
        this.entityType = entityType;
        this.data = new HashMap();
    }
    
    /**
     * Specify field values for new entity.
     * 
     * @param   data            map of field name to field value
     */
    public void setData(Map data) {
        this.data = data;
    }
}
