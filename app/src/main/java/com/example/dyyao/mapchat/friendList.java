package com.example.dyyao.mapchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class friendList extends AppCompatActivity {
    private Button addF;
    private Button addG;
    private Button test;
    private ListView friendlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        addF = (Button) findViewById(R.id.addFriendButton);
        addG = (Button) findViewById(R.id.addGroupButton);
        test = (Button) findViewById(R.id.testButton);

        final ArrayList<String> friends = new ArrayList<>();
        String[] values = new String[] { "a", "b", "c", "d", "e", "f", "g",
                "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
                "t", "u", "w", "x", "y", "z" };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_multiple_choice, values);

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

            }
        });

        addF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


}
