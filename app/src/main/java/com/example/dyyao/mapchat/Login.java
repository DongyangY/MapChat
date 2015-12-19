package com.example.dyyao.mapchat;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class Login extends AppCompatActivity implements View.OnClickListener {
    Button bLogin;
    EditText etUsername, etPassword;
    TextView tvRegisterLink;
    public static String serverIpAddress;
    public static final int SERVER_PORT_WR = 4444;
    public static final int SERVER_PORT_R = 5555;
    public static final int SERVER_PORT_PHOTO = 6666;
    public static Queue<String> mLogCommandBuffer;
    public static Queue<String> mPhotoCommandBuffer;
    public static String UserID;
    private static final String TAG = "Login";

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StartAnimations();
        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        tvRegisterLink = (TextView) findViewById(R.id.tv_RegisterLink);
        bLogin = (Button) findViewById(R.id.b_Login);
        bLogin.setOnClickListener(this);
        tvRegisterLink.setOnClickListener(this);

        mLogCommandBuffer = new LinkedList<>();
        mPhotoCommandBuffer = new LinkedList<>();

        ClientTaskWR mClientTaskWR = new ClientTaskWR(mLogCommandBuffer, this);
        mClientTaskWR.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        ClientTaskR mClientTaskR = new ClientTaskR();
        mClientTaskR.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        ClientTaskWRPhoto mClientTaskWRPhoto = new ClientTaskWRPhoto(mPhotoCommandBuffer, this);
        mClientTaskWRPhoto.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        Log.d(TAG, "WR connected");

    }


    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        RelativeLayout l=(RelativeLayout) findViewById(R.id.lin_lay1);
        l.clearAnimation();
        l.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.imageView4);

        iv.clearAnimation();
        iv.startAnimation(anim);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_Login:
                Log.d(TAG, "at login");
                UserID = etUsername.getText().toString();
                if (ClientTaskWR.mSocket != null && ClientTaskWR.mSocket.isConnected() && ClientTaskR.mSocket.isConnected()){
                    mLogCommandBuffer.add("login:" + etUsername.getText().toString() + ":" + etPassword.getText().toString());
                    Log.d(TAG, "buffer size " + String.valueOf(mLogCommandBuffer.size()));
                } else {
                    Toast.makeText(this, "No Connection to Server",Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.tv_RegisterLink:
                if (ClientTaskWR.mSocket != null && ClientTaskWR.mSocket.isConnected() && ClientTaskR.mSocket.isConnected()){
                    startActivity(new Intent(this, Register.class));
                } else {
                    Toast.makeText(this, "No Connection to Server",Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    @Override
    protected void onDestroy(){
        Log.d(TAG, "Login onDestroy");
        super.onDestroy();
        try {
            if (ClientTaskWR.mSocket != null && ClientTaskWR.mSocket.isConnected() && ClientTaskR.mSocket.isConnected() ) {

                ClientTaskR.mSocket.close();
                ClientTaskWR.mSocket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (ClientTaskWRPhoto.mSocketPhoto != null && ClientTaskWRPhoto.mSocketPhoto.isConnected()) {
                ClientTaskWRPhoto.mSocketPhoto.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}