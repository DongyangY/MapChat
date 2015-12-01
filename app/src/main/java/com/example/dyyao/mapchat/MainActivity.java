package com.example.dyyao.mapchat;

import android.content.Intent;
import android.graphics.PixelFormat;
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

public class MainActivity extends AppCompatActivity {

    Button enter;
    EditText ip;
    private static final String TAG = "MainActivity";

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        StartAnimations();

        ip = (EditText) findViewById(R.id.et_ip);
        enter = (Button) findViewById(R.id.btn_enter);


        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login.SERVER_IP_ADDRESS = ip.getText().toString();
                startActivity(new Intent(MainActivity.this, Login.class));
            }
        });
    }

    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        RelativeLayout l=(RelativeLayout) findViewById(R.id.lin_lay);
        l.clearAnimation();
        l.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.imageView3);

        iv.clearAnimation();
        iv.startAnimation(anim);


    }

    @Override
    protected void onDestroy(){
        Log.d(TAG, "Main onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onPause(){
        Log.d(TAG, "Main onPause");
        super.onPause();
    }

    @Override
    protected void onStop(){
        Log.d(TAG, "Main onStop");
        super.onStop();
    }

    @Override
    protected void onResume(){
        Log.d(TAG, "Main onResume");
        super.onResume();
    }

    @Override
    protected void onRestart(){
        Log.d(TAG, "Main onRestart");
        super.onRestart();
    }
}
