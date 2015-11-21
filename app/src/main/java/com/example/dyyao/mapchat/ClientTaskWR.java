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
            //Log.d(TAG, "before isEmpty()");
            if (!mCommandBuffer.isEmpty()){
                Log.d(TAG, "enter isEmpty()");
                mCommand = mCommandBuffer.remove();
                Log.d(TAG,"mCommand is: " + mCommand);
                try{
                    OutputStream mOutStream = mSocket.getOutputStream();
                    mPrintWriterOut = new PrintWriter(mOutStream, true);
                    Log.d(TAG, mCommand);
                    mPrintWriterOut.println(mCommand);

                    InputStream mInputStream = mSocket.getInputStream();
                    BufferedReader mBufferedReader = new BufferedReader(new InputStreamReader(mInputStream));

                    Log.d(TAG, "execute line");
                    serverResponse = mBufferedReader.readLine();
                    Log.d(TAG, serverResponse);
                    /*
                    if (line.equals("no") && mSocket != null){
                        mSocket.close();
                    }
                    */
                    //Log.d(TAG, serverResponse);
                    //publishProgress(serverResponse);
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
        //Log.d(TAG, result[0]);
    }
}
