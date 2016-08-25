/**
 * The push command description
 *
 * @author Dongyang Yao (dongyang.yao@rutgers.edu)
 */

import java.net.Socket;

public class Push {

    // The client socket reference
    public Socket client;

    // The command content
    public String command;

    /**
     * Constructor
     *
     * @param c socket reference
     * @param d the command content
     */
    public Push(Socket c, String d) {
        client = c;
        command = d;
    }
}
