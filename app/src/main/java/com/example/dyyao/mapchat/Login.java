package com.example.dyyao.mapchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by luluzhao on 11/15/15.
 */
public class Login extends AppCompatActivity implements View.OnClickListener {
    Button bLogin;
    EditText etUsername, etPassword;
    TextView tvRegisterLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        tvRegisterLink = (TextView) findViewById(R.id.tv_RegisterLink);
        bLogin = (Button) findViewById(R.id.b_Login);

        bLogin.setOnClickListener(this);
        tvRegisterLink.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_Login:
                startActivity(new Intent(this, friendList.class));

                break;

            case R.id.tv_RegisterLink:

                startActivity(new Intent(this, Register.class));
                break;

        }
    }
}
