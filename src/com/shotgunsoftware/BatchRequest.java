package com.shotgunsoftware;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * The specification for an Update request.
 * 
 * @author      Matt Daw
 * @version     1.0
 */
public class BatchRequest {
    String entityType;
    Integer entityId;
    Map data;
    Set returnFields;
    String requestType;
    
    /**
     * Sole Constructor
     * 
     * @param   entityType      the type of entity to update
     * @param   entityId        the id of entity to update
     */
    public BatchRequest(String entityType) {
        this.entityType = entityType;
        this.entityId = null;
        this.data = new HashMap();
        this.requestType = null;
    }
    
    /**
     * Specify entity values to create.
     * 
     * @param   data            map of field name to field value
     */
    public void create(Map data, Set returnFields) {
        this.data = data;
        this.requestType = "create";
        this.returnFields = returnFields;
    }
    
    public void create(Map data) {
    	create(data, null);
    }

    /**
     * Specify entity values to create.
     * 
     * @param   data            map of field name to field value
     */
    public void update(Integer entityId, Map data) {
    	this.entityId = entityId;
        this.data = data;
        this.requestType = "update";
    }
    /**
     * Specify entity values to create.
     * 
     * @param   data            map of field name to field value
     */
    public void delete(Integer entityId) {
        this.entityId = entityId;
        this.requestType = "delete";

    }

}
