/**
 * The client link description
 *
 * @author Dongyang Yao (dongyang.yao@rutgers.edu)
 */

import java.net.Socket;

public class Link {

    // The client name
    public String name;

    // The client Socket reference
    public Socket client;

    /**
     * Constructor
     * @param n the name
     * @param c the socket reference
     */
    public Link(String n, Socket c) {
        name = n;
        client = c;
    }
}
