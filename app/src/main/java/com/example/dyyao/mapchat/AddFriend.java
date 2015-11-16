package com.example.dyyao.mapchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by luluzhao on 11/15/15.
 */
public class AddFriend extends AppCompatActivity {
    EditText userID;
    Button bAddFriend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriend);
        bAddFriend = (Button) findViewById(R.id.btn_add);
        userID = (EditText) findViewById(R.id.et_userid);
        bAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddFriend.this, friendList.class));

            }


        });

    }

}
