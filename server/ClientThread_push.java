/**
 * Send the command from a client to another or serveral other clients
 * This is a uni-directional communication - server-to-client
 *
 * @author Dongyang Yao (dongyang.yao@rutgers.edu)
 */

import java.io.*;
import java.net.*;
import java.util.*;

public class ClientThread_push implements Runnable {

    // The client's socket reference
    public Socket client;

    // The client's ip
    public String ip;

    // The client's server-to-client command buffer
    public Queue<String> pushQueue;

    /**
     * Constructor
     * @param c Socket reference
     * @param i ip address
     */
    public ClientThread_push(Socket c, String i) {
        pushQueue = new LinkedList<String>();
        client = c;
        ip = i;
    }

    public void enqueue(String cmd) {
	synchronized (pushQueue) {
	    pushQueue.add(cmd);
	}
    }

    public String dequeue() {
	synchronized (pushQueue) {
	    return pushQueue.remove();
	}
    }

    public boolean isEmpty() {
	synchronized (pushQueue) {
	    return pushQueue.isEmpty();
	}
    }

    @Override
    public void run() {
        try {

	    // The writer to the client
            PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);

	    // This is a polling method and needs to be fixed later
            while (true) {
		// There is no process if no command in buffer
                if (!isEmpty()) {

                    System.out.println("get the push from buffer");

                    String push = dequeue();

		    // Relay
                    out.println(push);

		    // Tranfer image byte by byte
                    String[] c = push.split(":");
                    if (c[0].equals("send_photo")) {

                        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        String back = in.readLine();

                        InputStream inS = new FileInputStream(c[1]);
                        OutputStream outS = client.getOutputStream();
                        MapChatServer.copy(inS, outS, Long.valueOf(c[2]));
                        inS.close();
                    }
                }

            }
        } catch (IOException e) {

        }
    }

}
