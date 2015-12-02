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
 * Created by Hua Deng on 12/1/2015.
 */
public class ClientTaskWRPhoto extends AsyncTask <Void, String, Void>{
    private Queue<String> mCommandBuffer;
    private String mCommand;
    public static Socket mSocketPhoto;
    private String serverResponse = "";
    private PrintWriter mPrintWriterOut;
    private Login login;
    //public static AddFriend addFriend;
    //public static friendList friendlist;
    //public static Register register;
    //Queue<String> sResponseBuffer = new LinkedList<>();
    private static final String TAG = "ClientTaskWRPhoto";

    public ClientTaskWRPhoto(Queue<String> c, Login l) {
        mCommandBuffer = c;
        login = l;
    }

    protected Void doInBackground(Void... arg0){
        try {
            mSocketPhoto = new Socket(Login.SERVER_IP_ADDRESS, Login.SERVER_PORT_PHOTO);
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
                    OutputStream mOutStream = mSocketPhoto.getOutputStream();
                    mPrintWriterOut = new PrintWriter(mOutStream, true);
                    InputStream mInputStream = mSocketPhoto.getInputStream();
                    BufferedReader mBufferedReader = new BufferedReader(new InputStreamReader(mInputStream));


                    Log.d(TAG, mCommand);
                    mPrintWriterOut.println(mCommand);

                    Log.d(TAG, "send_photo command");
                    serverResponse = mBufferedReader.readLine();
                    InputStream in = new FileInputStream(MapChat.mediaFile);
                    copy(in, mOutStream, MapChat.mediaFile.length());
                    in.close();

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

    public static void copy(InputStream in, OutputStream out, long length) throws IOException {
        byte[] buf = new byte[8192];
        int len = 0;
        while (length > 0) {
            len = in.read(buf);
            out.write(buf, 0, len);
            length -= len;
            Log.i(TAG, String.valueOf(len));
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
            case "send_photo":{
                Log.d(TAG,"SEND PHOTO RES GOT !!!!");
                break;
            }

        }

        //Log.d(TAG, result[0]);
    }
}
