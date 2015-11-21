package com.example.dyyao.mapchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by luluzhao on 11/15/15.
 */
public class Register extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "Register ";
    Button bRegister;
    EditText etFirstName, etLastName, etUsername, etPassword, etCPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.d(TAG, "Enter Register");
        etFirstName = (EditText) findViewById(R.id.et_firstname);
        etLastName = (EditText) findViewById(R.id.et_lastname);
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
                startActivity(new Intent(this, friendList.class));
                break;

        }
    }
}
