package com.example.dyyao.mapchat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class friendList extends AppCompatActivity {
    private Button addF;
    private Button addG;
    private ListView friendlist;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        addF = (Button) findViewById(R.id.addFriendButton);
        addG = (Button) findViewById(R.id.addGroupButton);

        textView = (TextView) findViewById(R.id.textView);

        final String[] values = new String[] { "a", "b", "c", "d", "e", "f", "g",
                "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
                "t", "u", "w", "x", "y", "z" };
        final ArrayList<String> selectedItems = new ArrayList<>();


        //final ArrayAdapter<myFriend> adapter = new myFriendAdapter(this, getModel());

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, values);

        friendlist = (ListView) findViewById(R.id.listView);
        friendlist.setAdapter(adapter);
        friendlist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);


        friendlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Click ListItem Number " + values[position], Toast.LENGTH_LONG)
                        .show();
            }
        });
/*
        SparseBooleanArray checked = friendlist.getCheckedItemPositions();
        for (int i = 0; i < checked.size(); i++) {
            int position = checked.keyAt(i);
            if (checked.valueAt(i))
                selectedItems.add(adapter.getItem(position));
        }

*/

        addG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open();
                startActivity(new Intent(friendList.this, MapChat.class));


            }
        });

        addF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray checked = friendlist.getCheckedItemPositions();
                for (int i = 0; i < checked.size(); i++) {
                    int position = checked.keyAt(i);
                    if (checked.valueAt(i)){
                        selectedItems.add(adapter.getItem(position));
                        textView.append(selectedItems.get(i));
                    }
                }


            }
        });
    }

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

    private List<myFriend> getModel() {
        List<myFriend> list = new ArrayList<>();
        list.add(get("A"));
        list.add(get("B"));
        list.add(get("Suse"));
        list.add(get("Eclipse"));
        list.add(get("Ubuntu"));
        list.add(get("Solaris"));
        list.add(get("Android"));
        list.add(get("iPhone"));
        list.add(get("C"));
        list.add(get("X"));
        list.add(get("M"));
        list.add(get("W"));
        list.add(get("Z"));
        // Initially select one of the items
        list.get(0).setSelected(true);
        return list;
    }


    private myFriend get(String s) {
        return new myFriend(s);
    }



}
