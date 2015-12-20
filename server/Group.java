/**
 * The chat group description
 *
 * @author Dongyang Yao (dongyang.yao@rutgers.edu)
 */

import java.util.ArrayList;

public class Group {

    // Group name
    public String groupName;

    // The clients' links in this group
    public ArrayList<Link> links;

    /**
     * Constructor
     * @param g group name
     */
    public Group(String g) {
        groupName = g;
        links = new ArrayList<Link>();
    }

    /**
     * Add a link to group
     * @param l the link reference
     */
    public void addLink(Link l) {
        links.add(l);
    }
}
