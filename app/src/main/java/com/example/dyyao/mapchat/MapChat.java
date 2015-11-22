package com.example.dyyao.mapchat;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by luluzhao on 11/15/15.
 */
public class MapChat extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    Button bSend, bshow;
    EditText Inputchat;
    TextView chatlog;
    ViewFlipper page;
    Animation animFlipInForeward;
    Animation animFlipInBackward;
    static MarkerOptions markerOptions;
    static BitmapDescriptor icon;
    static Marker move;
    private GoogleMap mMap;
    String userId;
    String[] values;
    String[] fNames;

    public static List<myFriend> friendInfo;

    private GoogleApiClient mGoogleApiClient;

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
                String sentence = Inputchat.getText().toString() + "\n";
                chatlog.append(sentence);
                //LatLng l = new LatLng(40.513817, -74.464844);
                //changeLocation("a", l);
                //updateMarker();
            }

        });

        userId = Login.UserID;

        page = (ViewFlipper) findViewById(R.id.flipper);
        animFlipInForeward = AnimationUtils.loadAnimation(this, R.anim.flipin);
        animFlipInBackward = AnimationUtils.loadAnimation(this, R.anim.flipin_reverse);

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

        LatLng sydney = new LatLng(40, -80);
        icon = BitmapDescriptorFactory.fromResource(R.drawable.simpleapple);
        markerOptions = new MarkerOptions().position(sydney).title("Current Location").snippet("Thinking of finding some thing....").icon(icon);
        move = mMap.addMarker(markerOptions);

        bshow = (Button) findViewById(R.id.btn_show);
        bshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                move.remove();
                LatLng where = new LatLng(40, -70);
                markerOptions = new MarkerOptions().position(where).title("Chat log").snippet("please keep talking....").icon(icon);
                markerOptions.position(where);
                move = mMap.addMarker(markerOptions);
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
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.addMarker(markerOptions);
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        open(marker);
                        return false;
                    }
                });
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });

        buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient() {
        Log.i("Map: ", "build google play service");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
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
        /*
        mMap = googleMap;

        mMap.moveCamera(CameraUpdateFactory.zoomTo(12));
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setMyLocationEnabled(true);
        */
/*
        Marker high = mMap.addMarker(new MarkerOptions().position(new LatLng(40.513817,-74.464844)).title("HighPoint Solutions Stadium"));
        Marker bcc = mMap.addMarker(new MarkerOptions().position(new LatLng(40.523128, -74.458797)).title("Busch Campus Center"));
        Marker rsc = mMap.addMarker(new MarkerOptions().position(new LatLng(40.502661,-74.451771)).title("Rutgers Student Center"));
        Marker ee = mMap.addMarker(new MarkerOptions().position(new LatLng(40.521663,-74.460665)).title("Electrical Engineering Building"));
        Marker old = mMap.addMarker(new MarkerOptions().position(new LatLng(40.498720, -74.446229)).title("Old Queens"));
*/
    }

    public void open(final Marker marker){
        AlertDialog.Builder alerDialogBuilder = new AlertDialog.Builder(this);
        alerDialogBuilder.setMessage("Do you want to delete this marker?");
        alerDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MapChat.this, "You clicked yes button", Toast.LENGTH_LONG).show();
                marker.remove();
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
        for( int i = 0; i < fNames.length; i++) {
            Marker m = mMap.addMarker(
                    new MarkerOptions().position(new LatLng(40.502661,-74.451771)).title(fNames[i]));
            m.setVisible(false);
            friendInfo.add(new myFriend(fNames[i], m));
        }
    }

    public static void changeLocation(String name, LatLng latLng){
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

    @Override
    public void onConnected(Bundle bundle) {
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
}

