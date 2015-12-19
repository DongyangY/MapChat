/**
 * Enter the local ip address of the server
 *
 * @author Dongyang Yao
 *         Hua Deng
 *         Xi Zhang
 *         Lulu Zhao
 */

// A correction for previous incorrect GitHub account email
// Try to resume the con

package com.example.dyyao.mapchat;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    private Button enter;
    private EditText ip;
    private static final String TAG = "MainActivity";

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        startAnimations();

        ip = (EditText) findViewById(R.id.et_ip);
        enter = (Button) findViewById(R.id.btn_enter);


        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login.serverIpAddress = ip.getText().toString();
                startActivity(new Intent(MainActivity.this, Login.class));
            }
        });
    }

    /**
     * Perform floating up animation
     */
    private void startAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        RelativeLayout l = (RelativeLayout) findViewById(R.id.lin_lay);
        l.clearAnimation();
        l.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.imageView3);

        iv.clearAnimation();
        iv.startAnimation(anim);
    }
}
