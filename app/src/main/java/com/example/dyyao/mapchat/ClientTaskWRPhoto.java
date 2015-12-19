/**
 * A Background thread for especially sending the image
 *
 * @author Dongyang Yao
 *         Hua Deng
 *         Xi Zhang
 *         Lulu Zhao
 */

package com.example.dyyao.mapchat;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Queue;

public class ClientTaskWRPhoto extends AsyncTask <Void, String, Void>{
    private Queue<String> mCommandBuffer;
    private String mCommand;
    public static Socket mSocketPhoto;
    private String serverResponse = "";
    private PrintWriter mPrintWriterOut;
    private Login login;
    private static final String TAG = "ClientTaskWRPhoto";

    /**
     * Constructor
     * @param c
     * @param l
     */
    public ClientTaskWRPhoto(Queue<String> c, Login l) {
        mCommandBuffer = c;
        login = l;
    }

    /**
     * Sending the image to server in background
     * @param arg0
     * @return
     */
    protected Void doInBackground(Void... arg0){
        try {
            mSocketPhoto = new Socket(Login.serverIpAddress, Login.SERVER_PORT_PHOTO);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            if (!mCommandBuffer.isEmpty()){
                mCommand = mCommandBuffer.remove();
                try{
                    OutputStream mOutStream = mSocketPhoto.getOutputStream();
                    mPrintWriterOut = new PrintWriter(mOutStream, true);
                    InputStream mInputStream = mSocketPhoto.getInputStream();
                    BufferedReader mBufferedReader = new BufferedReader(new InputStreamReader(mInputStream));

                    // Send the image transfer request
                    mPrintWriterOut.println(mCommand);

                    serverResponse = mBufferedReader.readLine();
                    InputStream in = new FileInputStream(MapChat.mediaFile);

                    // Send the image bytes
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

    /**
     * The method for coping the image's bytes
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
            Log.i(TAG, String.valueOf(len));
        }
    }

    protected void onPostExecute(Void result){

        super.onPostExecute(result);
    }

    /**
     * Response from server
     * @param result
     */
    protected void onProgressUpdate(String... result){
        super.onProgressUpdate(result);
        String[] sResponses = result[0].split(":");
        String command = sResponses[0];
        String status = sResponses[1];

        switch (command){
            case "send_photo":{
                break;
            }

        }
    }
}
