/**
 * Add friend with friend's user name
 *
 * @author Dongyang Yao
 *         Hua Deng
 *         Xi Zhang
 *         Lulu Zhao
 */

package com.example.dyyao.mapchat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class FriendInvitation extends AppCompatActivity {
    private EditText userID;
    private Button bAddFriend;
    private static final String TAG = "FriendInvitation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ClientTaskWR.friendInvitation = this;
        setContentView(R.layout.activity_addfriend);
        bAddFriend = (Button) findViewById(R.id.btn_add);
        userID = (EditText) findViewById(R.id.et_userid);

        bAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Push the 'add_friend' command to the buffer sending to the server
                Login.mLogCommandBuffer.add("add_friend:" + userID.getText().toString());
            }
        });

    }
}
