package com.example.dyyao.mapchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.LinkedList;
import java.util.Queue;

public class AddFriend extends AppCompatActivity {
    EditText userID;
    Button bAddFriend;
    public Queue<String> mFriendInfoBuffer;
    private static final String TAG = "AddFriend";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ClientTaskWR.addFriend = this;
        setContentView(R.layout.activity_addfriend);
        bAddFriend = (Button) findViewById(R.id.btn_add);
        userID = (EditText) findViewById(R.id.et_userid);

        //mFriendInfoBuffer = new LinkedList<>();

        bAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "at add friend");
                Login.mLogCommandBuffer.add("add_friend:" + userID.getText().toString());
                Log.d(TAG, "buffer size " + String.valueOf(Login.mLogCommandBuffer.size()));
                //startActivity(new Intent(AddFriend.this, friendList.class));
            }
        });

    }
}
