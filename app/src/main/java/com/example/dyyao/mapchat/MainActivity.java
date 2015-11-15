package com.example.dyyao.mapchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private static final String SERVERIPADDRESS = "192.168.1.111";
    private static final int SERVERPORT = 4444;
    EditText sendText;
    Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendText = (EditText) findViewById(R.id.sendText);
        sendButton = (Button) findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clientTask mClientTask = new clientTask(SERVERIPADDRESS, SERVERPORT);
                mClientTask.execute();
            }
        });

    }

}
