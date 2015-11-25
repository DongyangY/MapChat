package com.example.dyyao.mapchat;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Queue;

/**
 * Created by Hua Deng on 11/15/2015.
 */
public class ClientTaskWR extends AsyncTask<Void, String, Void> {

    private Queue<String> mCommandBuffer;
    private String mCommand;
    private Socket mSocket;
    private String serverResponse = "";
    private PrintWriter mPrintWriterOut;
    private Login login;
    public static AddFriend addFriend;
    public static friendList friendlist;
    public static Register register;
    //Queue<String> sResponseBuffer = new LinkedList<>();
    private static final String TAG = "ClientTaskWR";

    public ClientTaskWR(Queue<String> c, Login l) {
        mCommandBuffer = c;
        login = l;
    }

    protected Void doInBackground(Void... arg0){
        try {
            mSocket = new Socket(Login.SERVER_IP_ADDRESS, Login.SERVER_PORT_WR);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            //Log.d(TAG, "before isEmpty()");
            if (!mCommandBuffer.isEmpty()){
                Log.d(TAG, "enter isEmpty()");
                mCommand = mCommandBuffer.remove();
                Log.d(TAG,"mCommand is: " + mCommand);
                try{
                    OutputStream mOutStream = mSocket.getOutputStream();
                    mPrintWriterOut = new PrintWriter(mOutStream, true);
                    InputStream mInputStream = mSocket.getInputStream();
                    BufferedReader mBufferedReader = new BufferedReader(new InputStreamReader(mInputStream));


                    Log.d(TAG, mCommand);
                    mPrintWriterOut.println(mCommand);

                    String[] c = mCommand.split(":");
                    if (c[0].equals("send_photo")) {
                        serverResponse = mBufferedReader.readLine();
                        OutputStream out = mSocket.getOutputStream();
                        InputStream in = new FileInputStream(MapChat.mediaFile);
                        copy(in, out);
                        in.close();
                        out.close();
                    }

                    Log.d(TAG, "execute line");
                    serverResponse = mBufferedReader.readLine();
                    Log.d(TAG, serverResponse);
                    publishProgress(serverResponse);
                } catch (UnknownHostException e){
                    e.printStackTrace();
                    serverResponse = "UnknownHostException: " + e.toString();
                } catch (IOException e){
                    e.printStackTrace();
                    serverResponse = "IOException: " + e.toString();
                }
            }
        }
    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[8192];
        int len = 0;
        while ((len = in.read(buf)) != -1) {
            Log.i(TAG, String.valueOf(len));
            out.write(buf, 0, len);
        }
    }

    protected void onPostExecute(Void result){

        super.onPostExecute(result);
    }

    protected void onProgressUpdate(String... result){
        super.onProgressUpdate(result);
        Log.d(TAG,result[0]);
        String[] sResponses = result[0].split(":");
        String command = sResponses[0];
        Log.d(TAG, "command is: " + command);
        String status = sResponses[1];
        Log.d(TAG, "status is:" + status);

        switch (command){
            case "login":{
                Log.d(TAG, "login response received");
                if(status.equals("yes")){
                    Log.d(TAG, "Login Succeed!");
                    Intent loginIntent = new Intent(login, friendList.class);
                    loginIntent.putExtra("friendNames", sResponses);
                    login.startActivity(loginIntent);
                } else {
                    Log.d(TAG, "Login Failed");
                    Toast.makeText(login, "Login Failed!",Toast.LENGTH_SHORT).show();
                    login.etUsername.setText("");
                    login.etPassword.setText("");
                }
                break;
            }
            case "register":{
                Log.d(TAG, "register response received");
                if (status.equals("yes")){
                    Log.d(TAG, "Register Succeed!");
                    Intent registerIntent = new Intent(register, friendList.class);
                    registerIntent.putExtra("friendNames", sResponses);
                    register.startActivity(registerIntent);
                } else {
                    Log.d(TAG, "Register Failed!");
                    Toast.makeText(register,"Register Failed!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case "add_friend":{
                Log.d(TAG, "add_friend response received");
                if(status.equals("yes")){
                    Log.d(TAG, "Add Friend Succeed");
                    String[] fNames = Arrays.copyOfRange(sResponses, 2, sResponses.length);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result",fNames);
                    addFriend.setResult(Activity.RESULT_OK, returnIntent);
                    addFriend.finish();
                } else {
                    Log.d(TAG, "Add_Friend Failed!");
                    Toast.makeText(addFriend, "Add Friend Failed!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case "create_group":{
                Log.d(TAG, "create_group response received");
                if (status.equals("yes")){
                    Log.d(TAG, "Create Group Succeed");
                    Intent friendlistIntent = new Intent(friendlist, MapChat.class);
                    friendlistIntent.putExtra("friendNames", sResponses);
                    friendlist.startActivity(friendlistIntent);
                }else{
                    Log.d(TAG, "Create Group Failed");
                    Toast.makeText(friendlist, "Create Group Failed!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case  "update_location":{
                Log.d(TAG, "update_location response received");
                break;
            }
            case "change_pin":{
                Log.d(TAG,"change pin response received");
                break;
            }
            case "send_message":{
                Log.d(TAG,"send message response received");
                break;
            }
            case "send_photo":{
                Log.d(TAG,"SEND PHOTO RES GOT !!!!");
                break;
            }

        }

        //Log.d(TAG, result[0]);
    }
}
