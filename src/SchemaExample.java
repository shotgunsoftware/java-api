import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import com.shotgunsoftware.*;

class SchemaExample {
    private static final String SHOTGUN_SERVER;
    private static final String SCRIPT_KEY;
    private static final String SCRIPT_NAME;

    private static final String DEFAULT_SHOTGUN_SERVER = "http://shotgun-dev.fas.fa.disney.com/api3";
    private static final String DEFAULT_SHOTGUN_KEY = "4b5a0063ed0bba6fea045b7eff7b77a9375353d5";
    private static final String DEFAULT_SCRIPT_NAME = "testScript";

    static {
    	SHOTGUN_SERVER = (System.getProperty("SHOTGUN_SERVER") != null) ? (String) System.getProperty("SHOTGUN_SERVER") : DEFAULT_SHOTGUN_SERVER;
    	SCRIPT_KEY = (System.getProperty("SCRIPT_KEY") != null) ? (String) System.getProperty("SCRIPT_KEY") : DEFAULT_SHOTGUN_KEY;
    	SCRIPT_NAME = (System.getProperty("SCRIPT_NAME") != null) ? (String) System.getProperty("SCRIPT_NAME") : DEFAULT_SCRIPT_NAME;
    }


    public static void main(String[] args) {
        try {
            URL u = new URL(SHOTGUN_SERVER);
            Shotgun s = new Shotgun(u, SCRIPT_NAME, SCRIPT_KEY);
            
            Map schema = s.schema_read();
            System.out.println(schema);
            Map fields = s.schema_field_read("Shot", null);
            int i = 4;
            
            for (Iterator it = fields.keySet().iterator(); it.hasNext(); ) {
            	String key = (String) it.next();
            	System.out.println("Key: " + key + " Value: " + fields.get(key));
            }
            String field_name = s.schema_field_create("Shot", "number", "schema_create_test", null);
            System.out.println(field_name);
            Map props = new HashMap();
            props.put("name", "Test Number Field Renamed");
            props.put("description","this is only a test");
            boolean success = s.schema_field_update("Shot", field_name, props);
        	System.out.println(Boolean.toString(success));        
            success = s.schema_field_delete("Shot", field_name);
            System.out.println(Boolean.toString(success));
//            Map schema = s.schema_read();            
        } catch ( Exception e ) {
            System.out.println(e.getMessage());
        }
    }
}

