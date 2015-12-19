/**
 * A Background thread for receiving the single-directional command from other clients via server
 * Update the UI thread as necessary
 *
 * @author Dongyang Yao
 *         Hua Deng
 *         Xi Zhang
 *         Lulu Zhao
 */

package com.example.dyyao.mapchat;

import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientTaskR extends AsyncTask<Void, String, Void> {

    // The connection link to the server
    public static Socket mSocket;

    // The response from the server
    private String serverResponse;

    private static final String TAG = "ClientTaskR";

    // The FriendList reference
    public static FriendList fl;

    // The directory to save the received image
    public static File mediaFile;

    // The time for image received
    private String timeStamp;

    // The user name who sends the image
    private String imageSender;

    // The MapChat reference - chatting room
    public static MapChat mMapChat;

    /**
     * Constructor
     */
    public ClientTaskR() {

    }

    /**
     * Continuously receive commands from server in background
     *
     * @param arg0
     * @return
     */
    @Override
    protected Void doInBackground(Void... arg0) {
        try {
            mSocket = new Socket(Login.serverIpAddress, Login.SERVER_PORT_R);
            InputStream mInputStream = mSocket.getInputStream();
            BufferedReader mBufferedReader = new BufferedReader(new InputStreamReader(mInputStream));

            while (true) {

                // Blocked if no command coming
                serverResponse = mBufferedReader.readLine();

                // Parse the command
                String[] cmds = serverResponse.split(":");

                // Receiving image
                if (cmds[0].equals("send_photo")) {
                    try {
                        OutputStream mOutStream = mSocket.getOutputStream();
                        PrintWriter mPrintWriterOut = new PrintWriter(mOutStream, true);

                        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        mediaFile = new File(MapChat.mediaStorageDir.getPath() + File.separator +
                                "IMG_" + timeStamp + ".jpg");

                        OutputStream out = new FileOutputStream(mediaFile.toString());

                        mPrintWriterOut.println("send_photo:ok");

                        ClientTaskWR.copy(mInputStream, out, Long.valueOf(cmds[2]));

                        imageSender = cmds[3];

                        out.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                publishProgress(serverResponse);
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
            serverResponse = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            e.printStackTrace();
            serverResponse = "IOException: " + e.toString();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }

    /**
     * Response the command in the UI thread
     *
     * @param result
     */
    @Override
    protected void onProgressUpdate(String... result) {
        super.onProgressUpdate(result);
        String[] cmds = result[0].split(":");

        // Actions for different commands
        switch (cmds[0]) {

            // A chatting group invitation from a friend
            case "create_group":
                Intent intent = new Intent(fl, MapChat.class);
                intent.putExtra("friendNames", cmds);
                fl.startActivity(intent);
                break;

            // Update other group members' location
            case "update_location":
                MapChat.changeLocation(cmds[2], new LatLng(Double.valueOf(cmds[3]), Double.valueOf(cmds[4])));
                break;

            // Show other group members' chatting message
            case "send_message":
                MapChat.setText(cmds[2], cmds[3], cmds[4]);
                break;

            // Update the pin location of others
            case "change_pin":
                MapChat.changeUserPin(cmds[2], new LatLng(Double.valueOf(cmds[3]), Double.valueOf(cmds[4])));
                break;

            // Show the received image
            case "send_photo":
                MapChat.setImage(mediaFile, imageSender);
                break;

            // Show the notification when a group member exits the group
            case "exit_group":
                MapChat.removeUser(cmds[2]);
                MapChat.selectF.setAdapter(new SelectAdapter(mMapChat, mMapChat.friendInfo));
                Toast.makeText(mMapChat, cmds[2] + " exit the group!", Toast.LENGTH_LONG).show();
                break;

            // Receive the friend invitation
            case "add_friend":
                FriendList.addFriend(cmds[1]);
                break;
        }
    }
}
