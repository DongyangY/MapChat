package com.example.dyyao.mapchat;

import android.content.Intent;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by luluzhao on 11/15/15.
 */
public class Register extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "Register";
    Button bRegister;
    EditText etFirstName, etLastName, etUsername, etPassword, etCPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ClientTaskWR.register = this;
        Log.d(TAG, "Enter Register");
        //etFirstName = (EditText) findViewById(R.id.et_firstname);
        //etLastName = (EditText) findViewById(R.id.et_lastname);
        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        etCPassword = (EditText) findViewById(R.id.et_cpassword);
        bRegister = (Button) findViewById(R.id.btn_register);
        bRegister.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                Log.d(TAG, "at");
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
