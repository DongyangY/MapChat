package com.example.dyyao.mapchat;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luluzhao on 11/15/15.
 */
public class MapChat extends FragmentActivity implements OnMapReadyCallback {

    Button bSend;
    EditText Inputchat;
    TextView chatlog;
    ViewFlipper page;
    Animation animFlipInForeward;
    Animation animFlipInBackward;
    private GoogleMap mMap;
    final String[] values = new String[] { "a", "b", "c" };

    List<myFriend> friendInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapchat);
        Inputchat = (EditText) findViewById(R.id.et_input);
        chatlog = (TextView) findViewById(R.id.tv_chatlogview);
        bSend = (Button) findViewById(R.id.btn_send);
        friendInfo = new ArrayList<>();

        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String sentence = Inputchat.getText().toString() + "\n";
                //chatlog.append(sentence);
                LatLng l = new LatLng(40.513817, -74.464844);
                changeLocation("a", l);
                updateMarker();
            }

        });

        page = (ViewFlipper)findViewById(R.id.flipper);
        animFlipInForeward = AnimationUtils.loadAnimation(this, R.anim.flipin);
        animFlipInBackward = AnimationUtils.loadAnimation(this, R.anim.flipin_reverse);

        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        mMap.moveCamera(CameraUpdateFactory.zoomTo(10));
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setMyLocationEnabled(true);
        //Set up Map friend list

        initialFriend();


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

    GestureDetector gestureDetector = new GestureDetector(simpleOnGestureListener);

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.moveCamera(CameraUpdateFactory.zoomTo(12));
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setMyLocationEnabled(true);
/*
        Marker high = mMap.addMarker(new MarkerOptions().position(new LatLng(40.513817,-74.464844)).title("HighPoint Solutions Stadium"));
        Marker bcc = mMap.addMarker(new MarkerOptions().position(new LatLng(40.523128, -74.458797)).title("Busch Campus Center"));
        Marker rsc = mMap.addMarker(new MarkerOptions().position(new LatLng(40.502661,-74.451771)).title("Rutgers Student Center"));
        Marker ee = mMap.addMarker(new MarkerOptions().position(new LatLng(40.521663,-74.460665)).title("Electrical Engineering Building"));
        Marker old = mMap.addMarker(new MarkerOptions().position(new LatLng(40.498720, -74.446229)).title("Old Queens"));
*/
    }

    private void initialFriend(){
        for( int i = 0; i < values.length; i++) {
            Marker m = mMap.addMarker(
                    new MarkerOptions().position(new LatLng(40.502661,-74.451771)).title(values[i]));
            m.setVisible(false);
            friendInfo.add(new myFriend(values[i], m));
        }
    }

    private void changeLocation(String name, LatLng latLng){
        for( int i = 0; i < friendInfo.size(); i++){
            if(friendInfo.get(i).getName().equals(name)){
                friendInfo.get(i).setLocation(latLng);
            }
        }

    }

    private void updateMarker(){
        for( int i = 0; i < friendInfo.size(); i++){
            friendInfo.get(i).getMarker().setVisible(true);
        }
    }
}


