package com.example.dyyao.mapchat;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button btry;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btry = (Button) findViewById(R.id.btn_try);


        btry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Login.class));
            }
        });
    }


    protected void onDestroy(){
        super.onDestroy();
        Login.mLogCommandBuffer.add("logout:" + Login.UserID);
        try {
            ClientTaskWR.mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ClientTaskR.mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
