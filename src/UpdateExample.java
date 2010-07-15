import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import com.shotgunsoftware.*;

class UpdateExample {
    public static void main(String[] args) {
        try {
            URL u = new URL("http://yourshotgunserver.com/api3/");
            Shotgun s = new Shotgun(u, "kp_testing", "9e795d3df53d1aea43b61f7c4a8dfa93c29c4ead");
            
            HashMap asset = new HashMap();
            asset.put("type", "Asset");
            asset.put("id", new Integer(638));
            
            ArrayList l = new ArrayList();
            l.add(asset);
            
            HashMap data = new HashMap();
            data.put("sg_status_list", "ip");
            data.put("assets", l);
            
            // Shot.assets is a multi-entity column so we have three options for the update: set/add/remove.
            // For this example, let's add the asset. Note that if you don't pass a mode, the default is set,
            // which will overwrite the existing values.
            HashMap modes = new HashMap();
            modes.put("assets", "add");
            
            UpdateRequest ur = new UpdateRequest("Shot", new Integer(1520));
            ur.setData(data);
            ur.setMultiEntityUpdateModes(modes);
            
            Map r = s.update(ur);
            Object[] assets = (Object[])r.get("assets");
            System.out.println(java.util.Arrays.toString(assets));
            
            // Now let's re-run the update in "remove" mode.
            modes.put("assets", "remove");
            
            r = s.update(ur);
            assets = (Object[])r.get("assets");
            System.out.println(java.util.Arrays.toString(assets));
            
            // Add it back in for the next step.
            modes.put("assets", "add");
            
            r = s.update(ur);
            assets = (Object[])r.get("assets");
            System.out.println(java.util.Arrays.toString(assets));
            
            // Now set a field on the connection between the Shot and Asset. To do this,
            // we need to pass the desired asset to the request.
            HashMap parents = new HashMap();
            parents.put("assets.AssetShotConnection.sg_test_assetinshot_field", asset);
            
            data = new HashMap();
            data.put("assets.AssetShotConnection.sg_test_assetinshot_field", "test");
            
            ur = new UpdateRequest("Shot", new Integer(1520));
            ur.setData(data);
            ur.setMultiEntityParents(parents);
            
            r = s.update(ur);
            System.out.println(r);
        } catch ( Exception e ) {
            System.out.println(e.getMessage());  
        }
    }
}

