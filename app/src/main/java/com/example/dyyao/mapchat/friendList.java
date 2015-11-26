package com.example.dyyao.mapchat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class friendList extends AppCompatActivity {
    private ImageButton addF;
    private ImageButton addG;
    private ListView friendlist;
    private EditText gpNames;
    private static String TAG = "friendList";
    ArrayList<String> selectedItems;
    String selectedNames;
    public static String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        ClientTaskWR.friendlist = this;
        ClientTaskR.fl = this;

        addF = (ImageButton) findViewById(R.id.addFriendButton);
        addG = (ImageButton) findViewById(R.id.addGroupButton);

        final ArrayList<myFriend> friends = new ArrayList<>();
        final String[] values = getIntent().getStringArrayExtra("friendNames");
        final String[] fNames = Arrays.copyOfRange(values, 2, values.length);
        selectedItems = new ArrayList<>();
        //String[] s = new String[] { "a", "b", "c", "d", "e", "f", "g","i","j","k"};

        //final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
         //       android.R.layout.simple_list_item_multiple_choice, fNames);

        for(int i = 0; i<fNames.length ; i++){
            myFriend f = new myFriend(fNames[i],null,0,null);
            friends.add(f);
        }
        final myFriendAdapter adapterTest = new myFriendAdapter(this, friends);

        friendlist = (ListView) findViewById(R.id.listView);
        friendlist.setAdapter(adapterTest);

        friendlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "check on: " + friends.get(position).getName());
                view.setSelected(true);
                //Toast.makeText(friendList.this, "check on: " + friends.get(position).getName(), Toast.LENGTH_LONG).show();
                if (friends.get(position).isSelected()) {
                    friends.get(position).setSelected(false);
                    view.setBackgroundColor(Color.TRANSPARENT);
                }
                else {
                    friends.get(position).setSelected(true);
                    view.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                }
            }
        });

        addG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "add group");
                /*
                SparseBooleanArray checked = friendlist.getCheckedItemPositions();
                Log.d(TAG, String.valueOf(checked.size()));
                selectedNames = "";
                for (int i = 0; i < checked.size(); i++) {
                    int position = checked.keyAt(i);
                    Log.d(TAG, String.valueOf(position));

                    if (checked.valueAt(i)) {
                        //HUGE BUGGGGGGGGGGGGGGGGGGGGGGG!
                        //login button should be clicked between addF and addG
                        Log.d(TAG, String.valueOf(adapterTest.getItem(position)));
                        selectedItems.add(adapterTest.getItem(position).getName());
                        selectedNames += ":";
                        selectedNames += selectedItems.get(i);
                    }

                }
                Log.d(TAG, "group member " + selectedNames);
        */
                selectedNames = "";
                for (int i = 0; i < friends.size(); i++) {
                    if (friends.get(i).isSelected()){
                        selectedNames += ":";
                        selectedNames += friends.get(i).getName();
                    }
                }
                open();
                Log.d(TAG, "group member " + selectedNames);
                //Login.mLogCommandBuffer.add("create_group:" + "new" + selectedNames);
            }
        });

        addF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent afIntent = new Intent(friendList.this, AddFriend.class);
                startActivityForResult(afIntent, 1);
                /*
                StringBuffer responseText = new StringBuffer();
                responseText.append("The following were selected...\n");
                for(int i=0;i<friends.size();i++){
                    myFriend country = friends.get(i);
                    if(country.isSelected()){
                        responseText.append("\n" + country.getName());
                    }
                }
                textView.setText(responseText);
                */
            }
        });
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
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.dialog_sign, null);
        alertDialogBuilder.setView(v);
        gpNames = (EditText) v.findViewById(R.id.groupname);
        Log.d(TAG, "Group name: " +  gpNames.getText().toString());
        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                //Log.d(TAG, "Group name: " +  String.valueOf(gpNames));
                groupName = gpNames.getText().toString();
                Login.mLogCommandBuffer.add("create_group:" + groupName + selectedNames);
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
