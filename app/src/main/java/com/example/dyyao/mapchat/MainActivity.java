package com.example.dyyao.mapchat;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button enter;
    EditText ip;
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);


        ip = (EditText) findViewById(R.id.et_ip);
        enter = (Button) findViewById(R.id.btn_enter);


        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login.SERVER_IP_ADDRESS=ip.getText().toString();
                startActivity(new Intent(MainActivity.this, Login.class));
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "Main onDestroy");
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
