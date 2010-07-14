package com.shotgunsoftware;

/**
 * Sort descriptor used in a Find request.
 * 
 * @author      Matt Daw
 * @version     1.0
 */
public class Sort {
    String fieldName;
    boolean ascending;
    
    /**
     * Default Constructor, sort by field name in ascending order
     * 
     * @param   fieldName        field name
     */
    public Sort(String fieldName) {
        this(fieldName, true);
    }
    
    /**
     * Secondary Constructor, sort by field name in ascending order
     * 
     * @param   fieldName        field name
     * @param   ascending        false to sort in descending order
     */
    public Sort(String fieldName, boolean ascending) {
        this.fieldName = fieldName;
        this.ascending = ascending;
    }
}
