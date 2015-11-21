package com.example.dyyao.mapchat;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by luluzhao on 11/15/15.
 */
public class MapChat extends FragmentActivity implements OnMapReadyCallback {

    Button bSend,bShow;
    EditText Inputchat;
    TextView chatlog;
    ViewFlipper page;
    Animation animFlipInForeward;
    Animation animFlipInBackward;
    private GoogleMap mMap;
    static final String TAG = "test icon";
    static MarkerOptions markerOptions;
    static BitmapDescriptor icon;
    static Marker move;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapchat);

        try {
            if (mMap == null) {
                mMap = ((MapFragment) getFragmentManager().
                        findFragmentById(R.id.map)).getMap();
            }


        }
        catch (Exception e) {
            e.printStackTrace();
        }
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
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
        bShow = (Button) findViewById(R.id.btn_show);


        bShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                move.remove();
                LatLng where = new LatLng(40, -70);
                markerOptions = new MarkerOptions().position(where)
                        .title("Current Location")
                        .snippet("Thinking of finding some thing...")
                        .icon(icon);
                Log.e(TAG, "add icon");
                markerOptions.position(where);
                move = mMap.addMarker(markerOptions);

            }

        });




        LatLng sydney = new LatLng(40, -80);
        icon = BitmapDescriptorFactory.fromResource(R.drawable.simpleapple);


        markerOptions = new MarkerOptions().position(sydney)
                .title("Current Location")
                .snippet("Thinking of finding some thing...")
                .icon(icon);
        Log.e(TAG, "add icon");
        move = mMap.addMarker(markerOptions);

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

            float sensitvity = 200;
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

    @Override
    public void onMapReady(GoogleMap googleMap) {

        //mMap = googleMap;
        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }
}


