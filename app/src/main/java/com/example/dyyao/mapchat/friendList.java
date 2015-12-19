/**
 * Add friend with friend's user name
 *
 * @author Dongyang Yao
 *         Hua Deng
 *         Xi Zhang
 *         Lulu Zhao
 */

package com.example.dyyao.mapchat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

public class FriendList extends AppCompatActivity {
    private ImageButton addF;
    private ImageButton addG;

    // The list view for showing the friends
    private ListView friendList;
    private EditText gpNames;
    private static String TAG = "FriendList";

    // The selected friends for grouping
    private ArrayList<String> selectedItems;
    protected static ArrayList<Friend> friends;
    private String selectedNames;
    public static FriendAdapter adapterTest = null;
    public static String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        ClientTaskWR.friendlist = this;
        ClientTaskR.fl = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            CharSequence title = "My Friends";
            getSupportActionBar().setTitle(title);
        }

        addF = (ImageButton) findViewById(R.id.addFriendButton);
        addG = (ImageButton) findViewById(R.id.addGroupButton);

        friends = new ArrayList<>();
        final String[] values = getIntent().getStringArrayExtra("friendNames");

        // Receive friend names in response from server at previous activity
        final String[] fNames = Arrays.copyOfRange(values, 2, values.length);
        selectedItems = new ArrayList<>();

        // Init friends
        for (int i = 0; i < fNames.length; i++) {
            Friend f = new Friend(fNames[i], null, 0, null);
            friends.add(f);
        }

        // Bind friends to the friend list
        adapterTest = new FriendAdapter(this, friends);
        friendList = (ListView) findViewById(R.id.listView);
        friendList.setAdapter(adapterTest);

        // Action when a friend is selected or unselected
        friendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                if (friends.get(position).isSelected()) {
                    friends.get(position).setSelected(false);
                    view.setBackgroundColor(Color.WHITE);
                } else {
                    friends.get(position).setSelected(true);
                    view.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                }
            }
        });

        // Add group triggered
        addG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNames = "";
                for (int i = 0; i < friends.size(); i++) {
                    if (friends.get(i).isSelected()) {
                        selectedNames += ":";
                        selectedNames += friends.get(i).getName();
                    }
                }

                // Open the dialog to enter group name
                open();
            }
        });

        // Add friend triggered
        addF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent afIntent = new Intent(FriendList.this, FriendInvitation.class);
                startActivityForResult(afIntent, 1);
            }
        });
    }

    /**
     * Add the user name to friend list from add friend activity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                friends = new ArrayList<>();
                String[] result = data.getStringArrayExtra("result");
                for (int i = 0; i < result.length; i++) {
                    Friend f = new Friend(result[i], null, 0, null);
                    friends.add(f);
                }

                // Reset the binding adapter
                adapterTest = new FriendAdapter(this, friends);
                friendList = (ListView) findViewById(R.id.listView);
                friendList.setAdapter(adapterTest);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }

    /**
     * Show the dialog for typing the group name
     * Sending the create group request to server
     */
    public void open() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_sign, null);
        alertDialogBuilder.setView(v);
        gpNames = (EditText) v.findViewById(R.id.groupname);

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                groupName = gpNames.getText().toString();

                // Push the 'create_group' command to the buffer sending to server
                Login.mLogCommandBuffer.add("create_group:" + groupName + selectedNames);
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * Add friend to friend list
     * @param name
     */
    public static void addFriend(String name) {
        Friend f = new Friend(name, null, 0, null);
        friends.add(f);
        if (adapterTest != null) {
            adapterTest.notifyDataSetChanged();
        } else {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Login.mLogCommandBuffer.add("logout:" + Login.UserID);
    }
}

