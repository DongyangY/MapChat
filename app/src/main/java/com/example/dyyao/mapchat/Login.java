package com.example.dyyao.mapchat;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by luluzhao on 11/15/15.
 */
public class Login extends AppCompatActivity implements View.OnClickListener {
    Button bLogin;
    EditText etUsername, etPassword;
    TextView tvRegisterLink;
    public static final String SERVER_IP_ADDRESS = "192.168.1.220";
    public static final int SERVER_PORT_WR = 4444;
    public static final int SERVER_PORT_R = 5555;
    public static  Queue<String> mLogCommandBuffer;
    public static String UserID;
    private static final String TAG = "Login";
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        tvRegisterLink = (TextView) findViewById(R.id.tv_RegisterLink);
        bLogin = (Button) findViewById(R.id.b_Login);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        //setSupportActionBar(toolbar);
        bLogin.setOnClickListener(this);
        tvRegisterLink.setOnClickListener(this);

        mLogCommandBuffer = new LinkedList<>();

        ClientTaskWR mClientTaskWR = new ClientTaskWR(mLogCommandBuffer, this);
        mClientTaskWR.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        ClientTaskR mClientTaskR = new ClientTaskR();
        mClientTaskR.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        Log.d(TAG, "WR connected");

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_Login:
                Log.d(TAG, "at");
                UserID = etUsername.getText().toString();
                mLogCommandBuffer.add("login:" + etUsername.getText().toString() + ":" + etPassword.getText().toString());
                Log.d(TAG, "buffer size " + String.valueOf(mLogCommandBuffer.size()));
                break;

            case R.id.tv_RegisterLink:

                startActivity(new Intent(this, Register.class));
                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_search:
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

}
