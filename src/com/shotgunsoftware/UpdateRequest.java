package com.shotgunsoftware;

import java.util.Map;
import java.util.HashMap;

/**
 * The specification for an Update request.
 * 
 * @author      Matt Daw
 * @version     1.0
 */
public class UpdateRequest {
    String entityType;
    Integer entityId;
    Map data;
    Map multiEntityUpdateModes;
    Map multiEntityParents;
    
    /**
     * Sole Constructor
     * 
     * @param   entityType      the type of entity to update
     * @param   entityId        the id of entity to update
     */
    public UpdateRequest(String entityType, Integer entityId) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.data = new HashMap();
        this.multiEntityUpdateModes = new HashMap();
        this.multiEntityParents = new HashMap();
    }
    
    /**
     * Specify field values to update.
     * 
     * @param   data            map of field name to field value
     */
    public void setData(Map data) {
        this.data = data;
    }
    
    /**
     * Specify what operation should be performed for multi-entity fields. If no operation
     * is specified, "set" is the default.
     *
     * @param   modes            map of field name to update mode ("add", "remove", "set")
     */
    public void setMultiEntityUpdateModes(Map modes) {
        this.multiEntityUpdateModes = modes;
    }
    
    /**
     * Specify what parent entity should be used when updating field values on connection fields.
     * If omitted, the update will fail as we can't determine which connection entity to apply the
     * change to.
     *
     * @param   parents         map of field name to entity map ( { type => Shot, id => 9 } )
     */
    public void setMultiEntityParents(Map parents) {
        this.multiEntityParents = parents;
    }
}
