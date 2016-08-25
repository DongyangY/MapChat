/**
 * This is a special request thread to receive the large file e.g. images from clients
 *
 * @author Dongyang Yao (dongyang.yao@rutgers.edu)
 */

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread_transfer implements Runnable {

    public String identification;
    public Socket client;

    public ClientThread_transfer(Socket c) {
        client = c;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);

            while (true) {
                System.out.println("transfer: wait for command");
                String command = in.readLine();

                System.out.println("got command: " + command);

                String[] cmds = command.split(":");

                switch (cmds[0]) {

		    // Send photo request
		case "send_photo":
		    
		    identification = cmds[2];
		    
		    System.out.println("SEND PHOTO!!");
		    
		    out.println("send_photo:ok");

		    // Start to copy the image bytes
		    InputStream inC = client.getInputStream();
		    String fileName = cmds[2] + ".jpg";
		    OutputStream outS = new FileOutputStream(fileName);
		    MapChatServer.copy(inC, outS, Long.valueOf(cmds[3]));
		    outS.close();
		    
		    System.out.println("SAVED PHOTO!!");
		    
		    Group group = MapChatServer.findGroupByName(cmds[1]);

		    // Push the sending photo command to group members' push thread buffers
		    MapChatServer.sendToAllGroupMembers(group, identification, "send_photo:" + fileName + ":" + cmds[3] + ":" + identification);
		    
		    break;
                }
            }
        } catch (IOException e) {
            System.out.println("error: client processing failed");
        }
    }
}
