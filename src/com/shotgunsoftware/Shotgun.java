package com.shotgunsoftware;

import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.xmlrpc.client.*;
import org.apache.xmlrpc.common.*;
import org.apache.xmlrpc.parser.*;
import org.apache.xmlrpc.serializer.NullSerializer;
import org.apache.ws.commons.util.NamespaceContextImpl;
import org.apache.xmlrpc.XmlRpcException;

/**
 * Shotgun XML-RPC API Wrapper
 * 
 * @author      Matt Daw
 * @version     1.0
 */
public class Shotgun {
    private Map auth;
    private XmlRpcClient client;
    
    /**
     * Sole Constructor
     * 
     * @param   url             the URL to access, should end with /api3/
     * @param   script_name     the Script Name of the Script (ApiUser) entity to authenticate
     * @param   script_key      the Application Key of the Script (ApiUser) entity to authenticate
     */
    public Shotgun(URL url, String script_name, String script_key) {
        this.auth = new HashMap();
        this.auth.put("script_name", script_name);
        this.auth.put("script_key", script_key);
        
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(url);
        config.setEnabledForExtensions(true);    
               
        this.client = new XmlRpcClient();
        client.setConfig(config);
        client.setTypeFactory(new MyTypeFactory(client));
    }
    
    /**
     * Find entities of a single type and their field values.
     * 
     * @param   fr  the specification for the request
     * @return  a List of the requested entities. Each item of the list is a Map of field name to field value.
     * @throws  XmlRpcException if the request failed
     */
    public List find(FindRequest fr) throws XmlRpcException {
        HashMap req = new HashMap();
        req.put("type", fr.entityType);
        req.put("return_fields", fr.returnFields);
        
        req.put("filters", fr.conditions.toHash());
        
        List sorts = new ArrayList();
        for ( int i=0; i < fr.sorts.length; i++ ) {
            Map sort = new HashMap();
            sort.put("field_name", fr.sorts[i].fieldName);
            sort.put("direction", fr.sorts[i].ascending ? "asc" : "desc");
            sorts.add(sort);
        }
        
        if ( fr.sorts != null )
            req.put("sorts", sorts);
        
        int currentPage = 1;
        HashMap paging = new HashMap();
        paging.put("entities_per_page", new Integer(500));
        paging.put("current_page", new Integer(currentPage));
        req.put("paging", paging);
        
        Object[] params = new Object[] { this.auth, req };
        
        boolean done = false;
        List records = new ArrayList();
        
        while ( ! done ) {
            Map response = (Map)this.client.execute("read", params);
            Map result = (Map)response.get("results");
            Map pagingInfo = (Map)result.get("paging_info");
            Object[] entities = (Object[])result.get("entities");
            Integer entityCount = (Integer)pagingInfo.get("entity_count");
            
            records.addAll(java.util.Arrays.asList(entities));
            
            if ( records.size() == entityCount.intValue() ) {
                done = true;
            } else {
                paging.put("current_page", new Integer(++currentPage));
            }
        } 
        
        return records;
    }
    
    /**
     * Create a single entity.
     * 
     * @param   cr  the specification for the request
     * @return  a Map of field names to field values for the created entity
     * @throws  XmlRpcException if the request failed
     */
    public Map create(CreateRequest cr) throws XmlRpcException {
        HashMap req = new HashMap();
        req.put("type", cr.entityType);
        
        List fields = new ArrayList();
        
        for ( Iterator iter = cr.data.entrySet().iterator(); iter.hasNext(); ) {
            Entry e = (Entry)iter.next();
            
            HashMap rf = new HashMap();
            rf.put("field_name", e.getKey());
            rf.put("value", e.getValue());
            
            fields.add(rf);
        }
        
        req.put("fields", fields);
        
        Object[] params = new Object[] { this.auth, req };
        Map response = (Map)this.client.execute("create", params);
        Map results = (Map)response.get("results");
        
        return results;
    }
    
    /**
     * Update a single entity.
     * 
     * @param   ur  the specification for the request
     * @return  a Map of field names to field values for the updated entity
     * @throws  XmlRpcException if the request failed
     */
    public Map update(UpdateRequest ur) throws XmlRpcException {
        HashMap req = new HashMap();
        req.put("type", ur.entityType);
        req.put("id", ur.entityId);
        
        List fields = new ArrayList();
        
        for ( Iterator iter = ur.data.entrySet().iterator(); iter.hasNext(); ) {
            Entry e = (Entry)iter.next();
            
            HashMap rf = new HashMap();
            String key = (String)e.getKey();
            rf.put("field_name", key);
            rf.put("value", e.getValue());
            
            if ( ur.multiEntityUpdateModes.containsKey(key) ) {
                rf.put("multi_entity_update_mode", ur.multiEntityUpdateModes.get(key));
            }
            
            if ( ur.multiEntityParents.containsKey(key) ) {
                rf.put("parent_entity", ur.multiEntityParents.get(key));
            }
            
            fields.add(rf);
        }
        
        req.put("fields", fields);
        
        Object[] params = new Object[] { this.auth, req };
        Map response = (Map)this.client.execute("update", params);
        Map results = (Map)response.get("results");
        
        return results;
    }
    
    /**
     * Delete a single entity.
     * 
     * @param   dr  the specification for the request
     * @return  true if the entity was deleted, false if it has already been deleted.
     * @throws  XmlRpcException if the request failed
     */
    public boolean delete(DeleteRequest dr) throws XmlRpcException {
        HashMap req = new HashMap();
        req.put("type", dr.entityType);
        req.put("id", dr.entityId);
        
        Object[] params = new Object[] { this.auth, req };   
         
        Map response = (Map)this.client.execute("delete", params);
        Boolean didDelete = (Boolean)response.get("results");
        
        return (didDelete.equals(Boolean.TRUE));
    }
    
    class MyTypeFactory extends TypeFactoryImpl {
        public MyTypeFactory(XmlRpcController pController) {
            super(pController);
        }

        public TypeParser getParser(XmlRpcStreamConfig pConfig,
          NamespaceContextImpl pContext, String pURI, String pLocalName) {

            if ("".equals(pURI) && NullSerializer.NIL_TAG.equals(pLocalName)) {
                return new NullParser();
            } else {
                return super.getParser(pConfig, pContext, pURI, pLocalName);
            }
        }
    }
}
