package com.example.dyyao.mapchat;

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
import android.widget.Toast;

public class Register extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "Register";
    Button bRegister;
    EditText etUsername, etPassword, etCPassword;


    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        StartAnimations();
        ClientTaskWR.register = this;
        Log.d(TAG, "Enter Register");
        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        etCPassword = (EditText) findViewById(R.id.et_cpassword);
        bRegister = (Button) findViewById(R.id.btn_register);
        bRegister.setOnClickListener(this);
    }


    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        RelativeLayout l=(RelativeLayout) findViewById(R.id.lin_lay2);
        l.clearAnimation();
        l.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.imageView5);

        iv.clearAnimation();
        iv.startAnimation(anim);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                Log.d(TAG, "at register");
                /*Check password consistency*/
                if(etPassword.getText().toString().equals(etCPassword.getText().toString())){
                    Login.mLogCommandBuffer.add("register:" + etUsername.getText().toString() + ":" + etPassword.getText().toString());
                    Log.d(TAG, "buffer size " + String.valueOf(Login.mLogCommandBuffer.size()));
                }else{
                    etPassword.setText("");
                    etCPassword.setText("");
                    Toast.makeText(this, "Try Again!", Toast.LENGTH_SHORT).show();
                }
                Login.UserID = etUsername.getText().toString();
                break;
        }
    }
}
