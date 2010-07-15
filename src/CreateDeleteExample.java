import java.net.URL;
import java.util.Map;
import java.util.HashMap;
import com.shotgunsoftware.*;

class CreateDeleteExample {
    public static void main(String[] args) {
        try {
            URL u = new URL("http://yourshotgunserver.com/api3/");
            Shotgun s = new Shotgun(u, "kp_testing", "9e795d3df53d1aea43b61f7c4a8dfa93c29c4ead");
            
            HashMap asset = new HashMap();
            asset.put("type", "Asset");
            asset.put("id", new Integer(638));
            
            HashMap step = new HashMap();
            step.put("type", "Step");
            step.put("id", new Integer(10));
            
            HashMap project = new HashMap();
            project.put("type", "Project");
            project.put("id", new Integer(64));
            
            HashMap data = new HashMap();
            data.put("content", "New Tast");
            data.put("entity", asset);
            data.put("sg_status_list", "ip");
            data.put("step", step);
            data.put("project", project);
            
            CreateRequest cr = new CreateRequest("Task");
            cr.setData(data);
            
            Map r = s.create(cr);
            Integer id = (Integer)r.get("id");
            
            System.out.println("Created Task with id = " + id.toString());
            System.out.println(r);
            
            DeleteRequest dr = new DeleteRequest("Task", id);
            boolean didDelete = s.delete(dr);
            
            if ( didDelete ) {
                System.out.println("Deleted Task with id = " + id.toString());
            }
            
            didDelete = s.delete(dr);
            
            if ( ! didDelete ) {
                System.out.println("Couldn't delete Task with id = " + id.toString() + ", already deleted.");
            }
        } catch ( Exception e ) {
            System.out.println(e.getMessage()); //Display the string.  
        }
    }
}

