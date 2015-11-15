package com.example.dyyao.mapchat;

import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Hua Deng on 11/15/2015.
 */
public class clientTask extends AsyncTask<Void, Void, Void> {

    String serverIpAddr;
    int serverPort;
    String serverResponse = "";

    clientTask(String addr, int port){
        serverIpAddr = addr;
        serverPort = port;
    }

    @Override
    protected Void doInBackground(Void... arg0){

        Socket mSocket = null;
        try{
            mSocket = new Socket(serverIpAddr, serverPort);
            ByteArrayOutputStream mByteArrayOutputStream = new ByteArrayOutputStream(1024);
            byte[] mBuffer = new byte[1024];

            int mBytesRead;
            InputStream mInputStream = mSocket.getInputStream();
            while((mBytesRead = mInputStream.read(mBuffer)) != -1){
                mByteArrayOutputStream.write(mBuffer, 0, mBytesRead);
                serverResponse += mByteArrayOutputStream.toString("UTF-8");
            }
        } catch (UnknownHostException e){
            e.printStackTrace();
            serverResponse = "UnknownHostException: " + e.toString();
        } catch (IOException e){
            e.printStackTrace();
            serverResponse = "IOException: " + e.toString();
        } finally {
            if (mSocket != null){
                try{
                    mSocket.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result){
        super.onPostExecute(result);
    }
}
