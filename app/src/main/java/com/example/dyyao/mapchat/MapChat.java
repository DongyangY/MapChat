package com.example.dyyao.mapchat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

/**
 * Created by luluzhao on 11/15/15.
 */
public class MapChat extends AppCompatActivity {

    Button bSend;
    EditText Inputchat;
    TextView chatlog;
    ViewFlipper page;
    Animation animFlipInForeward;
    Animation animFlipInBackward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapchat);
        Inputchat = (EditText) findViewById(R.id.et_input);
        chatlog = (TextView) findViewById(R.id.tv_chatlogview);
        bSend = (Button) findViewById(R.id.btn_send);

        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sentence = Inputchat.getText().toString() + "\n";
                chatlog.append(sentence);

            }

        });

        page = (ViewFlipper)findViewById(R.id.flipper);
        animFlipInForeward = AnimationUtils.loadAnimation(this, R.anim.flipin);
        animFlipInBackward = AnimationUtils.loadAnimation(this, R.anim.flipin_reverse);

    }

    private void SwipeRight(){
        page.setInAnimation(animFlipInBackward);
        //page.setOutAnimation(animFlipOutBackward);
        page.showPrevious();
    }

    private void SwipeLeft(){
        page.setInAnimation(animFlipInForeward);
        //page.setOutAnimation(animFlipOutForeward);
        page.showNext();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        return gestureDetector.onTouchEvent(event);
    }

    GestureDetector.SimpleOnGestureListener simpleOnGestureListener
            = new GestureDetector.SimpleOnGestureListener(){

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {

            float sensitvity = 50;
            if((e1.getX() - e2.getX()) > sensitvity){
                SwipeLeft();
            }else if((e2.getX() - e1.getX()) > sensitvity){
                SwipeRight();
            }

            return true;
        }

    };

    GestureDetector gestureDetector
            = new GestureDetector(simpleOnGestureListener);
}


