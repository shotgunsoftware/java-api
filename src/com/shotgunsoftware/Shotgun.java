package com.shotgunsoftware;

import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
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

    /**
     * Batch a series of update requests
     * 
     * @param   br  A list of Batch Requests
     * @return  a List of Map of field names to field values for the created entity
     * @throws  XmlRpcException if the request failed
     */
    public Object[] batch(BatchRequest[] br) throws XmlRpcException {
    	List req = new Vector();    	
    	for (int index = 0; index < br.length; index++) {
			Map hr = new HashMap();
			hr.put("request_type", br[index].requestType);
			hr.put("type", br[index].entityType);
    		if (br[index].requestType.equals("create")) {
    			List fields = new Vector();
    			for (Iterator entry_it = br[index].data.entrySet().iterator(); entry_it.hasNext(); ) {
        			Map.Entry entry = (Map.Entry) entry_it.next();
        			Map e = new HashMap();
        			e.put("field_name", entry.getKey());
        			e.put("value", entry.getValue());
        			fields.add(e);
    			}
    			hr.put("fields", fields.toArray());
    		} else if (br[index].requestType.equals("update")) {
    			hr.put("id", br[index].entityId);
    			List fields = new Vector();
    			for (Iterator entry_it = br[index].data.entrySet().iterator(); entry_it.hasNext(); ) {
        			Map.Entry entry = (Map.Entry) entry_it.next();
        			Map e = new HashMap();
        			e.put("field_name", entry.getKey());
        			e.put("value", entry.getValue());
        			fields.add(e);
    			}
    			hr.put("fields", fields.toArray());
    		} else if (br[index].requestType.equals("delete")) {
    			hr.put("id", br[index].entityId);
    		}
    		req.add(hr);
    	}
        Object[] params = new Object[] { this.auth, req.toArray() };
        Map response = (Map)this.client.execute("batch", params);
        Object[] results = (Object[]) response.get("results");        
        return results;
    }

    public Object[] batch(List br) throws XmlRpcException {
    	return batch((BatchRequest[]) br.toArray());
    }

    /**
     * Return the shotgun schema
     * 
     * Well, it would but can't figure out how to pass no arguments through java xmlrpc
     * @return  
     * @throws  XmlRpcException if the request failed
     */
    public Map schema_read() throws XmlRpcException {
    	throw new UnsupportedOperationException();
    	/**
    	Object[] params = new Object[] { this.auth, new HashMap()};
        Map response = (Map)this.client.execute("schema_read", params);
        Map results = (Map)response.get("results");        
        return results;
        **/
    }

    public Map schema_field_read(String entity_type, String field_name) throws XmlRpcException {
        HashMap req = new HashMap();
        req.put("type", entity_type);
        if (field_name != null)
            req.put("field_name", field_name);
        Object[] params = new Object[] { this.auth, req };
        Map response = (Map)this.client.execute("schema_field_read", params);
        Map results = (Map)response.get("results");        
        return results;
    }

    public String schema_field_create(String entity_type, String data_type, String display_name, 
    		Map properties) throws XmlRpcException {
        if (properties == null)
        	properties = new HashMap();
        List fields = new ArrayList();
        
        HashMap rf = new HashMap();
        rf.put("property_name", "name");
        rf.put("value", display_name);
        fields.add(rf);            
        for ( Iterator iter = properties.entrySet().iterator(); iter.hasNext(); ) {
            Entry e = (Entry)iter.next();
            rf = new HashMap();
            String key = (String)e.getKey();
            rf.put("property_name", key);
            rf.put("value", e.getValue());
            fields.add(rf);            
        }
        HashMap req = new HashMap();
        req.put("type", entity_type);
        req.put("data_type", data_type);
        req.put("properties", fields);
        Object[] params = new Object[] { this.auth, req };
        Map response = (Map)this.client.execute("schema_field_create", params);
        String field_name = (String)response.get("results");        
        return field_name;
    }

    public boolean schema_field_update(String entity_type, String field_name, Map properties) throws XmlRpcException {
        if (properties == null)
        	properties = new HashMap();
        List fields = new ArrayList();
        
        HashMap rf = new HashMap();
        for ( Iterator iter = properties.entrySet().iterator(); iter.hasNext(); ) {
            Entry e = (Entry)iter.next();
            rf = new HashMap();
            String key = (String)e.getKey();
            rf.put("property_name", key);
            rf.put("value", e.getValue());
            fields.add(rf);            
        }
        HashMap req = new HashMap();
        req.put("type", entity_type);
        req.put("field_name", field_name);
        req.put("properties", fields);
        Object[] params = new Object[] { this.auth, req };
        Map response = (Map)this.client.execute("schema_field_update", params);
        Boolean didDelete = (Boolean)response.get("results");        
        return (didDelete.equals(Boolean.TRUE));
    }

    public boolean schema_field_delete(String entity_type, String field_name) throws XmlRpcException {
        HashMap req = new HashMap();
        req.put("type", entity_type);
        req.put("field_name", field_name);
        Object[] params = new Object[] { this.auth, req };
        Map response = (Map)this.client.execute("schema_field_delete", params);
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
