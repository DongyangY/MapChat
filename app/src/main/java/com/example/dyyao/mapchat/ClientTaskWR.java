/**
 * A Background thread for sending the request to server
 * Receiving the response from server
 * Update the UI thread as necessary
 *
 * @author Dongyang Yao
 *         Hua Deng
 *         Xi Zhang
 *         Lulu Zhao
 */

package com.example.dyyao.mapchat;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Queue;

public class ClientTaskWR extends AsyncTask<Void, String, Void> {

    private Queue<String> mCommandBuffer;
    private String mCommand;
    public static Socket mSocket;
    private String serverResponse = "";
    private PrintWriter mPrintWriterOut;
    private Login login;
    public static FriendInvitation friendInvitation;
    public static FriendList friendlist;
    public static Register register;
    private static final String TAG = "ClientTaskWR";

    /**
     * Constructor
     *
     * @param c
     * @param l
     */
    public ClientTaskWR(Queue<String> c, Login l) {
        mCommandBuffer = c;
        login = l;
    }

    /**
     * Sending the command to server from buffer in the background
     *
     * @param arg0
     * @return
     */
    protected Void doInBackground(Void... arg0) {
        try {
            mSocket = new Socket(Login.serverIpAddress, Login.SERVER_PORT_WR);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {

            // Send if buffer has commands
            if (!mCommandBuffer.isEmpty()) {
                mCommand = mCommandBuffer.remove();
                try {
                    OutputStream mOutStream = mSocket.getOutputStream();
                    mPrintWriterOut = new PrintWriter(mOutStream, true);
                    InputStream mInputStream = mSocket.getInputStream();
                    BufferedReader mBufferedReader = new BufferedReader(new InputStreamReader(mInputStream));

                    // Send the command
                    mPrintWriterOut.println(mCommand);

                    // Parse the command
                    String[] c = mCommand.split(":");

                    // Receive the response from server
                    serverResponse = mBufferedReader.readLine();

                    publishProgress(serverResponse);

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    serverResponse = "UnknownHostException: " + e.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                    serverResponse = "IOException: " + e.toString();
                }
            }
        }
    }

    /**
     * Copy the stream given number of bytes
     * Used for image transfer
     *
     * @param in
     * @param out
     * @param length
     * @throws IOException
     */
    public static void copy(InputStream in, OutputStream out, long length) throws IOException {
        byte[] buf = new byte[8192];
        int len = 0;
        while (length > 0) {
            len = in.read(buf);
            out.write(buf, 0, len);
            length -= len;
        }
    }

    protected void onPostExecute(Void result) {

        super.onPostExecute(result);
    }

    /**
     * Process response from server
     * React in UI thread
     *
     * @param result
     */
    protected void onProgressUpdate(String... result) {
        super.onProgressUpdate(result);
        String[] sResponses = result[0].split(":");
        String command = sResponses[0];
        String status = sResponses[1];

        switch (command) {

            // Login request
            case "login": {
                if (status.equals("yes")) {

                    // Succeed
                    Intent loginIntent = new Intent(login, FriendList.class);
                    loginIntent.putExtra("friendNames", sResponses);
                    login.startActivity(loginIntent);
                } else {

                    // Fail
                    Toast.makeText(login, "Login Failed!", Toast.LENGTH_SHORT).show();
                    login.etUsername.setText("");
                    login.etPassword.setText("");
                }
                break;
            }

            // Register request
            case "register": {
                if (status.equals("yes")) {

                    // Succeed
                    Intent registerIntent = new Intent(register, FriendList.class);
                    registerIntent.putExtra("friendNames", sResponses);
                    register.startActivity(registerIntent);
                } else {

                    // Fail
                    Toast.makeText(register, "Register Failed!", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            // Add friend request
            case "add_friend": {

                if (status.equals("yes")) {

                    // Succeed
                    String[] fNames = Arrays.copyOfRange(sResponses, 2, sResponses.length);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", fNames);
                    friendInvitation.setResult(Activity.RESULT_OK, returnIntent);
                    friendInvitation.finish();
                } else {

                    // Fail
                    Toast.makeText(friendInvitation, "Add Friend Failed!", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            // Create group request with selected friends
            case "create_group": {
                if (status.equals("yes")) {

                    // Succeed
                    Intent friendlistIntent = new Intent(friendlist, MapChat.class);
                    friendlistIntent.putExtra("friendNames", sResponses);
                    friendlist.startActivity(friendlistIntent);
                } else {

                    // Fail
                    Toast.makeText(friendlist, "Create Group Failed!", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            // Update self location in group members' local
            case "update_location": {
                break;
            }

            // Update self pin location in group members' local
            case "change_pin": {
                break;
            }

            // Send message to group members' local
            case "send_message": {
                break;
            }
        }

    }
}
