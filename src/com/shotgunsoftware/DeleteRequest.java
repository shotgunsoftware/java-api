package com.shotgunsoftware;

/**
 * The specification for a Delete request.
 * 
 * @author      Matt Daw
 * @version     1.0
 */
public class DeleteRequest {
    String entityType;
    Integer entityId;
    
    /**
     * Sole Constructor
     * 
     * @param   entityType      the type of entity to delete
     * @param   entityId        the id of entity to delete 
     */
    public DeleteRequest(String entityType, Integer entityId) {
        this.entityType = entityType;
        this.entityId = entityId;
    }
}
