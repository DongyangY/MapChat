/**
 * Send the command from a client to another or serveral other clients
 * This is a uni-directional communication - server-to-client
 *
 * @author Dongyang Yao (dongyang.yao@rutgers.edu)
 */

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientThread_push implements Runnable {

    // The client's socket reference
    public Socket client;

    // The client's ip
    public String ip;

    // The client's server-to-client command buffer
    public ArrayList<String> pushes;

    // The structed command buffer
    public ArrayList<Push> pushes_origin;

    /**
     * Constructor
     * @param c Socket reference
     * @param i ip address
     */
    public ClientThread_push(Socket c, String i) {
        pushes = new ArrayList<String>();
        client = c;
        ip = i;
    }

    @Override
    public void run() {
        try {

	    // The writer to the client
            PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);

            while (true) {
                boolean flag = pushes.isEmpty();

		// There is no process if no command in buffer
                if (!flag) {

                    System.out.println("get the push from buffer");

                    String push = pushes.get(0);

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

                    pushes.remove(0);
                }

            }
        } catch (IOException e) {

        }
    }

}
