package com.shotgunsoftware;

/**
 * The specification for a Find request.
 * 
 * @author      Matt Daw
 * @version     1.0
 */
public class FindRequest {
    String entityType;
    String[] returnFields;
    Sort[] sorts;
    ConditionGroup conditions;
    
    /**
     * Sole Constructor
     * 
     * @param   entityType      the type of entity to find
     */
    public FindRequest(String entityType) {
        this.entityType = entityType;
        this.sorts = new Sort[] {};
        this.conditions = new ConditionGroup();
    }
    
    /**
     * Request extra fields in the result. 
     * If no fields are specified the result will only include "type" and "id".
     * 
     * @param   fields          an array of field names whose values should be included in the result
     */
    public void setFields(String fields[]) {
        this.returnFields = fields;
    }
    
    /**
     * Request entities matching the specified single condition.
     * If no conditions are specified the result will include all entities of the requested type.
     * 
     * @param   c               a single condition
     */
    public void setCondition(Condition c) {
        this.conditions = new ConditionGroup();
        this.conditions.addCondition(c);
    }
    
    /**
     * Request entities matching the specified conditions.
     * If no conditions are specified the result will include all entities of the requested type.
     * 
     * @param   cg              the root condition group
     */
    public void setConditions(ConditionGroup cg) {
        this.conditions = cg;
    }
    
    /**
     * Request the result be sorted by a single field / direction. 
     * If no sorts are specified the result will be ordered by id.
     * 
     * @param   sort            a sort descriptor
     */
    public void setSort(Sort sort) {
        this.sorts = new Sort[] { sort };
    }
    
    /**
     * Request the result be sorted by several fields. 
     * Equivalent to "Advanced Sorting" in the Shotgun UI.
     * If no sorts are specified the result will be ordered by id.
     * 
     * @param   sorts          an array of sort descriptors
     */
    public void setSorts(Sort[] sorts) {
        this.sorts = sorts;
    }
}
