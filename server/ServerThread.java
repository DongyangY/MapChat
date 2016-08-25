/**
 * Accept client and start a client thread for it
 * The type of client thread is determined by port_num variable
 *
 * @author Dongyang Yao (dongyang.yao@rutgers.edu)
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread implements Runnable {

    // The port number of this server thread
    public int port_num;

    /**
     * Constructor
     * @param p port number
     */
    public ServerThread(int p) {
        port_num = p;
    }

    @Override
    public void run() {
        try {

	    // Open a socket on port number
            ServerSocket server = new ServerSocket(port_num);
            Socket client;

            while (true) {

		// Blocked if no client is connecting
                client = server.accept();

		// Get the ip address of the connecting client
                String[] ip = client.getRemoteSocketAddress().toString().split(":");

                System.out.println("port: " + port_num + " status: a client accepted ip: " + ip[0]);

		// Start different client thread based on port number
                if (port_num == MapChatServer.PORT_TRANSFER) {
                    ClientThread_transfer clientThread = new ClientThread_transfer(client);
                    (new Thread(clientThread)).start();
                } else if (port_num == MapChatServer.PORT_REQUEST) {
                    ClientThread_request clientThread = new ClientThread_request(client, ip[0]);
                    (new Thread(clientThread)).start();
                } else {
                    ClientThread_push clientThread = new ClientThread_push(client, ip[0]);

		    // Check if it is already connected
                    MapChatServer.pushTable.put(ip[0], clientThread);

                    (new Thread(clientThread)).start();
                }
            }
        } catch (IOException e) {
            System.out.println("port: " + port_num + " error: client connection failed");
        }
    }

}
