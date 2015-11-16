package com.example.dyyao.mapchat;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Hua Deng on 11/15/2015.
 */
public class ClientTaskR extends AsyncTask<Void, String, Void>{

    private Socket mSocket;
    private EditText receivedText;
    private String serverResponse = "";
    private static final String TAG = "ClientTaskR";

    public ClientTaskR() {

    }

    @Override
    protected Void doInBackground(Void... arg0){
        try{
            mSocket = new Socket(Login.SERVER_IP_ADDRESS, Login.SERVER_PORT_R);
            InputStream mInputStream = mSocket.getInputStream();
            BufferedReader mBufferedReader = new BufferedReader(new InputStreamReader(mInputStream));
            serverResponse = mBufferedReader.readLine();
            publishProgress(serverResponse);
            Log.d(TAG, serverResponse);
        } catch (UnknownHostException e){
            e.printStackTrace();
            serverResponse = "UnknownHostException: " + e.toString();
        } catch (IOException e){
            e.printStackTrace();
            serverResponse = "IOException: " + e.toString();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result){
        super.onPostExecute(result);
    }

    @Override
    protected void onProgressUpdate(String... result){
        super.onProgressUpdate(result);
        Log.d(TAG, result[0]);
    }
}
