package com.example.dyyao.mapchat;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
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
import java.util.Arrays;
import java.util.Date;

/**
 * Created by Hua Deng on 11/15/2015.
 */
public class ClientTaskR extends AsyncTask<Void, String, Void>{

    private Socket mSocket;
    private String serverResponse;
    private static final String TAG = "ClientTaskR";
    public static friendList fl;
    public static File mediaFile;
    private String timeStamp;

    public ClientTaskR() {

    }

    @Override
    protected Void doInBackground(Void... arg0){
        try{
            mSocket = new Socket(Login.SERVER_IP_ADDRESS, Login.SERVER_PORT_R);
            InputStream mInputStream = mSocket.getInputStream();
            BufferedReader mBufferedReader = new BufferedReader(new InputStreamReader(mInputStream));

            while (true) {
                serverResponse = mBufferedReader.readLine();
                String[] cmds = serverResponse.split(":");

                if (cmds[0].equals("send_photo")) {
                    try {
                        OutputStream mOutStream = mSocket.getOutputStream();
                        PrintWriter mPrintWriterOut = new PrintWriter(mOutStream, true);

                        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        mediaFile = new File(MapChat.mediaStorageDir.getPath() + File.separator +
                                "IMG_"+ timeStamp + ".jpg");

                        OutputStream out = new FileOutputStream(mediaFile.toString());

                        mPrintWriterOut.println("send_photo:ok");

                        ClientTaskWR.copy(mInputStream, out, Long.valueOf(cmds[2]));
                        out.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                publishProgress(serverResponse);
            }

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
        String[] cmds = result[0].split(":");

        switch (cmds[0]) {
            case "create_group":
                Intent intent = new Intent(fl, MapChat.class);
                intent.putExtra("friendNames", cmds);
                fl.startActivity(intent);
                break;
            case "update_location":
                MapChat.changeLocation(cmds[2], new LatLng(Double.valueOf(cmds[3]), Double.valueOf(cmds[4])));
                break;
            case "send_message":
                MapChat.setText(cmds[2], cmds[3], cmds[4]);
                break;
            case "change_pin":
                MapChat.changeUserPin(cmds[2], new LatLng(Double.valueOf(cmds[3]), Double.valueOf(cmds[4])));
                break;
            case "send_photo":
                MapChat.setImage(mediaFile);
                break;
        }
    }
}
