package com.example.dyyao.mapchat;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
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
    private static final String TAG = "ClientTaskWR";

    public ClientTaskWR(Queue<String> c) {
        mCommandBuffer = c;
    }

    protected Void doInBackground(Void... arg0){
        try {
            mSocket = new Socket(Login.SERVER_IP_ADDRESS, Login.SERVER_PORT_WR);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            if (!mCommandBuffer.isEmpty()){
                Log.d(TAG, "enter isEmpty()");
                mCommand = mCommandBuffer.remove();
                try{
                    OutputStream mOutStream = mSocket.getOutputStream();
                    mPrintWriterOut = new PrintWriter(mOutStream, true);
                    mPrintWriterOut.println(mCommand);

                    InputStream mInputStream = mSocket.getInputStream();
                    BufferedReader mBufferedReader = new BufferedReader(new InputStreamReader(mInputStream));

                    String line;
                    Log.d(TAG, "execute line");
                    while ((line = mBufferedReader.readLine()) != null) {
                        Log.d(TAG, line);
                        serverResponse += line;
                    }
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

    protected void onPostExecute(Void result){
        super.onPostExecute(result);
    }

    protected void onProgressUpdate(String... result){
        super.onProgressUpdate(result);
        Log.d(TAG, result[0]);
    }
}
