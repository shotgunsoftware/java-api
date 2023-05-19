import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import com.shotgunsoftware.*;

class BatchExample {
    private static final String SHOTGUN_SERVER;
    private static final String SCRIPT_KEY;
    private static final String SCRIPT_NAME;

    private static final String DEFAULT_SHOTGUN_SERVER = "http://my-example.shotgunstudio.com/api3";
    private static final String DEFAULT_SHOTGUN_KEY = "da39a3ee5e6b4b0d3255bfef95601890afd80709";
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
            
            BatchRequest[] req = new BatchRequest[3];
            
            HashMap asset = new HashMap();
            asset.put("type", "Asset");
            asset.put("id", new Integer(23182));
            
            HashMap step = new HashMap();
            step.put("type", "Step");
            step.put("id", new Integer(10));
            
            HashMap project = new HashMap();
            project.put("type", "Project");
            project.put("id", new Integer(77));
            
            HashMap data = new HashMap();
            data.put("content", "New Batch Test Tast1");
            data.put("entity", asset);
            data.put("sg_status_list", "pre");
            data.put("step", step);
            data.put("project", project);
            
            req[0] = new BatchRequest("Task");
            req[0].create(data);
            req[0] = new BatchRequest("Task");
            req[0].create(data);

            data = new HashMap();
            data.put("content", "New Batch Test Tast2");
            data.put("entity", asset);
            data.put("sg_status_list", "wrk");
            data.put("step", step);
            data.put("project", project);
            
            req[1] = new BatchRequest("Task");
            req[1].create(data);

            data = new HashMap();
            data.put("content", "New Batch Test Tast3");
            data.put("entity", asset);
            data.put("sg_status_list", "cmpt");
            data.put("step", step);
            data.put("project", project);
            
            req[2] = new BatchRequest("Task");
            req[2].create(data);

            Object[] r = s.batch(req);
            data = new HashMap();
            data.put("sg_status_list", "omt");
            for (int index = 0; index < r.length; index++) {
            	req[index] = new BatchRequest("Task");
            	req[index].update((Integer) ((Map) r[index]).get("id"), data);
            }
            r = s.batch(req);
            for (int index = 0; index < r.length; index++) {
            	req[index] = new BatchRequest("Task");
            	req[index].delete((Integer) ((Map)r[index]).get("id"));
            }
            r = s.batch(req);
            
            int i = 4;

//            Object[] assets = (Object[])r.get("assets");
//            for (int index = 0; index < assets.length; index++)
//            	System.out.print(assets[index].toString());
//            System.out.println();
        } catch ( Exception e ) {
            System.out.println(e.getMessage());
        }
    }
}

