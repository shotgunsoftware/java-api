package com.shotgunsoftware;

import java.util.Map;
import java.util.HashMap;

/**
 * Simple condition used in a Find request.
 * 
 * @author      Matt Daw
 * @version     1.0
 */
public class Condition implements ConditionInterface {
    String path;
    String relation;
    Object[] values;
    
    /**
     * Sole Constructor
     * 
     * @param   path        field name, or deep filter path (eg. entity.Shot.code)
     * @param   relation    filter operation (eg. contains). 
     *                      See https://support.shotgunsoftware.com/entries/38359-reference-filter-syntax
     * @param   values      parameters to filter operation. Most operations only take a single parameter,
     *                      but others take two (eg. the "between" relation). 
     */
    public Condition(String path, String relation, Object[] values) {
        this.path = path;
        this.relation = relation;
        this.values = values;
    }
    
    /**
     * Convert from Condition to the Map needed for XML-RPC transport. 
     */
    public Map toHash() {
        Map h = new HashMap();
        
        h.put("path", this.path);
        h.put("relation", this.relation);
        h.put("values", this.values);
        
        return h;
    }
}
