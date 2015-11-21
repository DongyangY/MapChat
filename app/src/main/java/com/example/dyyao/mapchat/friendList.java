package com.example.dyyao.mapchat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class friendList extends AppCompatActivity {
    private Button addF;
    private Button addG;
    private Button test;
    private ListView friendlist;
    private static String TAG = "friendList";
    ArrayList<String> selectedItems;
    String selectedNames = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        ClientTaskWR.friendlist = this;

        addF = (Button) findViewById(R.id.addFriendButton);
        addG = (Button) findViewById(R.id.addGroupButton);
        //test = (Button) findViewById(R.id.testButton);

        final ArrayList<String> friends = new ArrayList<>();
        String[] values = getIntent().getStringArrayExtra("friendNames");
        final String[] fNames = Arrays.copyOfRange(values, 2, values.length);
        selectedItems = new ArrayList<>();
        /*String[] values = new String[] { "a", "b", "c", "d", "e", "f", "g",
                "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
                "t", "u", "w", "x", "y", "z" };
        */
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_multiple_choice, fNames);

        friendlist = (ListView) findViewById(R.id.listView);

        friendlist.setAdapter(adapter);

        friendlist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        addG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "add group");
                SparseBooleanArray checked = friendlist.getCheckedItemPositions();
                Log.d(TAG, String.valueOf(checked.size()));
                for (int i = 0; i < checked.size(); i++) {
                    int position = checked.keyAt(i);
                    Log.d(TAG, String.valueOf(position));

                    if (checked.valueAt(i)){
                        Log.d(TAG, String.valueOf(adapter.getItem(position)));
                        selectedItems.add(adapter.getItem(position));
                        selectedNames +=":";
                        selectedNames += selectedItems.get(i);
                    }

                }
                Log.d(TAG, "group member " + selectedNames);
                Login.mLogCommandBuffer.add("create_group:" + "new" + selectedNames);
                //startActivity(new Intent(friendList.this, MapChat.class));
            }
        });

        addF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent afIntent = new Intent(friendList.this, AddFriend.class);
                startActivityForResult(afIntent, 1);

                //startActivity(new Intent(friendList.this, AddFriend.class));

            }
        });
        /*
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(friendList.this, TestDialog.class));
                open();

            }
        });
        */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String[] result = data.getStringArrayExtra("result");
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_multiple_choice, result);

                friendlist = (ListView) findViewById(R.id.listView);

                friendlist.setAdapter(adapter);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult
    public void open(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setMessage("Do you want to accept this request? ");

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Toast.makeText(friendList.this, "You clicked yes button", Toast.LENGTH_LONG).show();
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }



}
