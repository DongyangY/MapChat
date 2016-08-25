/**
 * Process the request from a client and response to the client
 * Relay the command from the client to another or serveral other clients
 * 
 * @author Dongyang Yao (dongyang.yao@rutgers.edu)
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class ClientThread_request implements Runnable {

    // The connection reference to a client
    public Socket client;

    // The client id
    public String identification;

    // The client ip
    public String ip;

    /**
     * Constructor
     * @param c socket reference
     * @param i local ip of client
     */
    public ClientThread_request(Socket c, String i) {
        client = c;
        ip = i;
    }

    @Override
    public void run() {
        try {

	    // Connect to MySQL database
            Connection connection;
            connection = DriverManager.getConnection(MapChatServer.URL + MapChatServer.DATABASE_NAME, MapChatServer.USER_NAME, MapChatServer.PASSWORD);
            Statement statement = connection.createStatement();

	    // Reader from client
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

	    // Writer to client
            PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);

            while (true) {

		// For close the connection
                boolean breakout = false;

                System.out.println("wait for command");

		// Blocked if no request in buffer
                String command = in.readLine();

                System.out.println("got command: " + command);

                String name;
                ResultSet res;
                StringBuilder sb;
                Group group;

		// Parse the request
                String[] cmds = command.split(":");

                switch (cmds[0]) {
		    
		    // Register request
		case "register":
		    System.out.println("register");
		    name = cmds[1];
		    String password = cmds[2];
		    
		    // Insert user info to database
		    statement.executeUpdate("INSERT INTO User VALUES (0, '" + name + "', '" + password + "')");
		    
		    // Response to client
		    out.println("register:yes");
		    
		    // Set identification as client user name
		    identification = name;
		    
		    // Add Link
		    MapChatServer.linkTable.put(name, new Link(name, client));
		    
		    break;

		    // Login request
		case "login":
		    System.out.println("login");
		    name = cmds[1];

		    // The password from client
		    String password_sender = cmds[2];

		    // The password in database
		    String password_truth = null;

		    // Query the password by name in database
		    res = statement.executeQuery("SELECT * FROM User WHERE name = '" + name + "'");
		    
		    while (res.next()) {
			password_truth = res.getString("password");
		    }
		    
		    System.out.println(name + " " + password_sender + " " + password_truth);

		    // Succeed
		    if (password_sender.equals(password_truth)) {
			System.out.println("login successfull");

			// Query the friends' names
			res = statement.executeQuery("SELECT * FROM Friend WHERE name = '" + name + "'");
			sb = new StringBuilder();
			sb.append("login:yes");
			while (res.next()) {
			    sb.append(":");
			    sb.append(res.getString("friend"));
			}

			// Response the friend list
			out.println(sb.toString());
			
			identification = name;
			
			MapChatServer.linkTable.put(name, new Link(name, client));
			
			// Fail
		    } else {
			System.out.println("login failed");

			// Response
			out.println("login:no");
			
		    }
		    
		    break;

		    // Exit group request
		case "exit_group":
		    System.out.println("exit group");

		    // Get the group reference by name
		    group = MapChatServer.groupTable.get(cmds[1]);

		    // Send exit notification to other group members
		    Link delete = MapChatServer.sendToAllGroupMembers(group, identification, cmds[0] + ":" + cmds[1] + ":" + cmds[2]);

		    // Delete the exited client 
		    group.links.remove(delete);

		    // Response
		    out.println("exit_group:yes");
		    
		    break;

		    // Logout request
		case "logout":
		    System.out.println("logout");

		    MapChatServer.linkTable.remove(cmds[1]);
		    
		    out.println("logout:yes");
                    
		    break;

		    // Add friend request
		case "add_friend":
		    System.out.println("add friend");
		    name = cmds[1];
		    
		    // Query registered user by name
		    res = statement.executeQuery("SELECT * FROM User WHERE name = '" + name + "'");
		    String name_friend = null;
		    while (res.next()) {
			name_friend = res.getString("name");
		    }
		    
		    // Response no if no this registered user
		    if (name_friend == null) {
			out.println("add_friend:no");
		    } else {
			
			// Add friend in both sides
			statement.executeUpdate("INSERT INTO Friend VALUES ('" + identification + "', '" + name_friend + "')");
			statement.executeUpdate("INSERT INTO Friend VALUES ('" + name_friend + "', '" + identification + "')");
			
			res = statement.executeQuery("SELECT * FROM Friend WHERE name = '" + identification + "'");
			sb = new StringBuilder();
			sb.append("add_friend:yes");
			
			while (res.next()) {
			    sb.append(":");
			    sb.append(res.getString("friend"));
			}

			System.out.println(sb.toString());

			// Response back the new friend list
			out.println(sb.toString());
		    }
	
		    Link friend = MapChatServer.linkTable.get(cmds[1]);
		
		    if (friend != null) {
			String[] ip = friend.client.getRemoteSocketAddress().toString().split(":");

			ClientThread_push push = MapChatServer.pushTable.get(ip[0]);

			if (push != null) {

			    // Send the friend invitation notification to the invited client
			    push.pushes.add("add_friend:" + identification);
			}
		    }

		    break;

		    // Create group request
		case "create_group":
		    System.out.println("create group");
		    String groupName = cmds[1];
		    group = new Group(groupName);
		    MapChatServer.groupTable.put(groupName, group);

		    sb = new StringBuilder();
		    sb.append("create_group:yes:" + groupName + ":" + identification);

		    for (int i = 2; i < cmds.length; i++) {
			Link link = MapChatServer.linkTable.get(cmds[i]);
			
			if (link != null) {
			    sb.append(":" + cmds[i]);
			}
		    }

		    // Add the group owner to group links
		    group.addLink(MapChatServer.linkTable.get(identification));

		    for (int i = 2; i < cmds.length; i++) {

			Link link = MapChatServer.linkTable.get(cmds[i]);
			
			if (link != null) {
			    
			    System.out.println("send to: " + cmds[i]);

			    // Add the online client to group links
			    group.addLink(link);

			    String[] ip = link.client.getRemoteSocketAddress().toString().split(":");

			    ClientThread_push push = MapChatServer.pushTable.get(ip[0]);

			    if (push != null) {

				// Send the group invitation to online selected friends
				push.pushes.add(sb.toString());
			    }
			}
		    }

		    out.println(sb.toString());
		    
		    break;

		    // Update requesting client's location to other group members
		case "update_location":
		    group = MapChatServer.groupTable.get(cmds[1]);

		    // Send the location to other group members
		    MapChatServer.sendToAllGroupMembers(group, identification, "update_location:" + cmds[1] + ":" + cmds[2] + ":" + cmds[3] + ":" + cmds[4]);

		    out.println("update_location:yes");

		    break;

		    // Send requesting client's message to other group members
		    // Send the vibration to only selected group members
		case "send_message":
		    
		    System.out.println("send message");
		    
		    group = MapChatServer.groupTable.get(cmds[1]);

		    // Get the selected members
		    ArrayList<String> notifications = new ArrayList<String>();
		    for (int i = 4; i < cmds.length; i++) {
			notifications.add(cmds[i]);
		    }
		    
		    for (Link l : group.links) {
			String member_name = l.name;
			if (!(member_name.equals(identification))) {

			    String[] ip = l.client.getRemoteSocketAddress().toString().split(":");
			    
			    ClientThread_push push = MapChatServer.pushTable.get(ip[0]);
			    
			    if (push != null) {
				String notify = notifications.contains(member_name) ? "yes" : "no";
				push.pushes.add(cmds[0] + ":" + cmds[1] + ":" + cmds[2] + ":" + cmds[3] + ":" + notify);
			    }
			}
		    }

		    out.println("send_message:yes");
		    
		    break;

		    // Send the requesting client's updated pin location to other group members
		case "change_pin":
		    
		    System.out.println("change pin");
		    
		    group = MapChatServer.groupTable.get(cmds[1]);

		    MapChatServer.sendToAllGroupMembers(group, identification, "change_pin:" + cmds[1] + ":" + cmds[2] + ":" + cmds[3] + ":" + cmds[4]);

		    out.println("change_pin:yes");
		    
		    break;
		    
		default:
		    out.println("no_command:yes");
		    break;
                }

		// Close the connection
                if (breakout) {
                    client.close();
                    connection.close();
                    System.out.println("status: a client disconnected");
                    break;
                }
            }
	    
        } catch (IOException e) {
            System.out.println("error: client processing failed");
        } catch (Exception e) {
            System.out.println("error: client database failed");
            e.printStackTrace();
        }

    }

}
