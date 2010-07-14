package com.shotgunsoftware;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Compound condition used in a Find request.
 * 
 * @author      Matt Daw
 * @version     1.0
 */
public class ConditionGroup implements ConditionInterface {
    boolean isAndGroup;
    List children;
    
    /**
     * Default Constructor, creates ANDed compound condition
     */
    public ConditionGroup() {
        this(true);
    }
    
    /**
     * Secondary Constructor
     * 
     * @param   isAndGroup  false for an ORed compound condition
     */
    public ConditionGroup(boolean isAndGroup) {
        this.isAndGroup = isAndGroup;
        this.children = new ArrayList();
    }
    
    /**
     * Append a condition to the group.
     * 
     * @param   c           condition
     */
    public void addCondition(Condition c) {
        children.add(c);
    }
    
    /**
     * Append a condition group to the group.
     * 
     * @param   cg          condition group
     */
    public void addConditionGroup(ConditionGroup cg) {
        children.add(cg);
    }
    
    /**
     * Convert from Condition to the Map needed for XML-RPC transport. 
     */
    public Map toHash() {
        Map h = new HashMap();
        List l = new ArrayList();
        
        for ( Iterator iter = children.iterator(); iter.hasNext(); ) {
            ConditionInterface ci = (ConditionInterface)iter.next();
            l.add(ci.toHash());
        }
        
        h.put("logical_operator", this.isAndGroup ? "and" : "or");
        h.put("conditions", l);
        
        return h;
    }
}
