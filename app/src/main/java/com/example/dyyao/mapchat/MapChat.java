package com.example.dyyao.mapchat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
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
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by luluzhao on 11/15/15.
 */
public class MapChat extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{
    Button bSend, bshow;
    EditText Inputchat;
    static TextView chatlog;
    ViewFlipper page;
    Animation animFlipInForeward;
    Animation animFlipInBackward;
    static BitmapDescriptor icon;
    private GoogleMap mMap;
    String[] values;
    String[] fNames;

    public static List<myFriend> friendInfo;
    private Marker mMarker;
    private Marker mPin = null;
    private final String[] colors = { "blue", "red", "green", "yellow", "black"};
    private String groupName;
    private String userName;
    private final String pinName = "MyPin";
    public static final String TAG = "MapChat";

    private boolean initialLocation = false;
    private boolean isExist = false;

    private Location currentLocation = null;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapchat);
        Inputchat = (EditText) findViewById(R.id.et_input);
        chatlog = (TextView) findViewById(R.id.tv_chatlogview);
        bSend = (Button) findViewById(R.id.btn_send);
        friendInfo = new ArrayList<>();

        values = getIntent().getStringArrayExtra("friendNames");
        fNames = Arrays.copyOfRange(values, 2, values.length);

        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String sentence = Inputchat.getText().toString() + "\n";
                //chatlog.append(sentence);
                //mMarker.setPosition(new LatLng(40, -80));
                Login.mLogCommandBuffer.add("send_message:" + groupName + ":" + userName + ":" + Inputchat.getText().toString());

            }

        });

        userName = Login.UserID;
        groupName = fNames[0];
        Log.e(TAG,groupName);
        page = (ViewFlipper) findViewById(R.id.flipper);
        animFlipInForeward = AnimationUtils.loadAnimation(this, R.anim.flipin);
        animFlipInBackward = AnimationUtils.loadAnimation(this, R.anim.flipin_reverse);
/*
        try {
            if (mMap == null) {
                mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMap.moveCamera(CameraUpdateFactory.zoomTo(10));
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setMyLocationEnabled(true);
        //Set up Map friend list
*/
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        //Location service

        mGoogleApiClient.connect();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000);       // 10 seconds, in milliseconds
        mLocationRequest.setFastestInterval(5000); // 5 second, in milliseconds


        setUpMap();
        initialFriend();
        intialSelf();


        //LatLng sydney = new LatLng(40, -80);
        //icon = BitmapDescriptorFactory.fromResource(R.drawable.simpleapple);
        //markerOptions = new MarkerOptions().position(sydney).title("Current Location").snippet("Thinking of finding some thing....").icon(icon);
        //move = mMap.addMarker(markerOptions);

        bshow = (Button) findViewById(R.id.btn_show);
        bshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMarker.setPosition(new LatLng(40, -80));

                Login.mLogCommandBuffer.add("update_location:" + groupName + ":" + userName + ":" + "40" + ":" + "-80");

            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                //setting the position for the marker
                markerOptions.position(latLng);
                //setting the title for the marker.
                //this will be displayed on taping the marker
                markerOptions.title(pinName);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                if(!isExist) {
                    mPin = mMap.addMarker(markerOptions);
                    isExist = true;
                }else {
                    mPin.setPosition(latLng);
                }
                Login.mLogCommandBuffer.add("change_pin:" + groupName + ":" + userName + ":" + latLng.latitude + ":" + latLng.longitude);
                Log.e(TAG, "login_commandBuffer_change_mPin");
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        if(marker.getTitle().equals(pinName)) {
                            open(marker);
                            isExist = false;
                        }
                        return false;
                    }
                });
            }
        });

    }

    private void intialSelf() {
        mMarker =  mMap.addMarker(
                new MarkerOptions().position(new LatLng(40.502661,-74.451771)).title(userName));
        mMarker.setVisible(false);
    }

    private void setUpMap() {
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            Log.v(TAG, "Set map");

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.moveCamera(CameraUpdateFactory.zoomTo(12));
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setCompassEnabled(true);
                mMap.setMyLocationEnabled(true);
            }
        }


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

    public void open(final Marker marker){
        AlertDialog.Builder alerDialogBuilder = new AlertDialog.Builder(this);
        alerDialogBuilder.setMessage("Do you want to delete this marker?");
        alerDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MapChat.this, "You clicked yes button", Toast.LENGTH_LONG).show();
                marker.remove();
                Login.mLogCommandBuffer.add("change_pin:" + groupName + ":" + userName + ":" + 0 + ":" + 0);
                Log.e(TAG, "login_commandBuffer_delete_mPin");
            }
        });

        alerDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog = alerDialogBuilder.create();
        alerDialogBuilder.show();
    }

    private void initialFriend(){
        for( int i = 1; i < fNames.length; i++) {
            if(!fNames[i].equals(userName)) {
                Marker m = mMap.addMarker(
                        new MarkerOptions().position(new LatLng(40.502661, -74.451771)).title(fNames[i]));
                m.setVisible(false);
                Marker p = mMap.addMarker(
                        new MarkerOptions().position(new LatLng(40.502661, -74.451771)).title(fNames[i]));
                p.setVisible(false);
                friendInfo.add(new myFriend(fNames[i], m, colors[i], p));
                Log.e(TAG, fNames[i]);
            }
        }
    }

    public static void changeLocation(String name, LatLng latLng){
        Log.e(TAG,"!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+name + "/" + latLng.toString());
        for( int i = 0; i < friendInfo.size(); i++){
            if(friendInfo.get(i).getName().equals(name)){
                Log.e(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + name + "get!!!!!!!");
                friendInfo.get(i).setLocation(latLng);
            }
        }
    }

    public static void changeUserPin(String name, LatLng latLng){
        Log.e(TAG,"!!!!!!!!!!!!!!pin!!!!!!!!!!!!!!"+name + "/" + latLng.toString());
        for( int i = 0; i < friendInfo.size(); i++){
            if(friendInfo.get(i).getName().equals(name)){
                Log.e(TAG, "!!!!!!!!!!!!pin!!!!!!!!!!!!!!!!" + name + "get!!!!!!!");
                friendInfo.get(i).getUserPin().setVisible(true);
                friendInfo.get(i).setPin(latLng);
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            // Center camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
        Log.v(TAG,"onConnect");
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Log.i("Map: ", "google play service connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("Map: ", "google play service suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("Map: ", "google play service failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, location.toString());
        currentLocation = location;
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        Log.v(TAG, latLng.latitude + " " + latLng.longitude);
        double lat = latLng.latitude;
        double lng = latLng.longitude;
        Login.mLogCommandBuffer.add("update_location:" + groupName + ":" + userName + ":" + lat + ":" + lng);

        Log.e(TAG,"login_commandBuffer_update_location");
        if (!initialLocation) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            for (myFriend f : friendInfo) {
                f.getMarker().setPosition(latLng);
                f.getMarker().setVisible(true);
            }
            initialLocation = true;
        };
    }

    public static void setText(String name, String text){
        for( int i = 0; i < friendInfo.size(); i++){
            if(friendInfo.get(i).getName().equals(name)){
                friendInfo.get(i).getMarker().setSnippet(text);
                chatlog.append(text + "\n");
            }
        }
    }

}

