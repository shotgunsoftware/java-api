import java.net.URL;
import java.util.List;
import com.shotgunsoftware.*;

class FindExample {
    public static void main(String[] args) {
        try {
            URL u = new URL("http://yourshotgunserver.com/api3/");
            Shotgun s = new Shotgun(u, "kp_testing", "9e795d3df53d1aea43b61f7c4a8dfa93c29c4ead");
            
            FindRequest fr = new FindRequest("Shot");
            fr.setFields(new String[] { "code", "sg_status_list" } );
            
            // Use a single condition
            Condition descCond = new Condition("description", "is_not", new Object[] { null });
            fr.setCondition(descCond);
            
            fr.setSort(new Sort("code", false));
            
            List recs = s.find(fr);
            System.out.println("Returned " + recs.size() + " records.");
            
            // Use a compound condition, assumed to be ANDed together. Pass false to ConditionGroup
            // to get an ORed group.
            Condition statusCond = new Condition("sg_status_list", "is", new Object[] { "ip" });
            ConditionGroup cg = new ConditionGroup();
            cg.addCondition(descCond);
            cg.addCondition(statusCond);
            
            fr.setConditions(cg);
            
            recs = s.find(fr);
            System.out.println("Returned " + recs.size() + " records.");
        } catch ( Exception e ) {
            System.out.println(e.getMessage());
        }
    }
}

