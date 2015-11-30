package com.example.dyyao.mapchat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by luluzhao on 11/15/15.
 */
public class MapChat extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{
    ImageButton bSend;
    EditText Inputchat;
    public static ListView selectF;
    static TextView chatlog;
    ViewFlipper page;
    Animation animFlipInForeward;
    Animation animFlipInBackward;
    public static GoogleMap mMap;
    String[] values;
    String[] fNames;

    public static List<myFriend> friendInfo;
    private Marker mMarker;
    private Marker mPin = null;
    private final int[] colors = { R.drawable.peopleblue, R.drawable.peoplered, R.drawable.peoplegreen, R.drawable.peopleyellow, R.drawable.peoplepurple};
    private final int[] colorspin = { R.drawable.pinblue, R.drawable.pinred, R.drawable.pingreen, R.drawable.pinyellow, R.drawable.pinpurple};
    private static final int[] colorsImage = { R.drawable.imageblue, R.drawable.imagered, R.drawable.imagegreen, R.drawable.imageyellow, R.drawable.imagepurple };

    private String groupName;
    private String userName;
    private String pinID ;
    public static final String TAG = "MapChat";

    private boolean initialLocation = false;
    private boolean isExist = false;

    private Location currentLocation = null;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri;
    public static File mediaStorageDir;
    private String timeStamp;
    public static File mediaFile;

    public static Vibrator vibrator;
    private static ArrayList<String> imageIds;

    public static MapChat mapChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapchat);
        Inputchat = (EditText) findViewById(R.id.et_input);
        chatlog = (TextView) findViewById(R.id.tv_chatlogview);
        selectF = (ListView) findViewById(R.id.listSelect);
        bSend = (ImageButton) findViewById(R.id.btn_send);
        friendInfo = new ArrayList<>();
        ClientTaskR.mMapchat = this;
        mapChat = this;

        imageIds = new ArrayList<String>();

        values = getIntent().getStringArrayExtra("friendNames");
        fNames = Arrays.copyOfRange(values, 2, values.length);
        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sentence = Inputchat.getText().toString();
                chatlog.append(sentence + "\n");
                mMarker.setSnippet(sentence);
                mMarker.showInfoWindow();
                for( int i = 0; i < friendInfo.size(); i++) {
                    if (friendInfo.get(i).isSelected()) {
                        sentence += ":";
                        sentence += friendInfo.get(i).getName();
                    }
                }
                //mMarker.setPosition(new LatLng(40, -80));
                Login.mLogCommandBuffer.add("send_message:" + groupName + ":" + userName + ":" + sentence);
                Inputchat.setText("");
            }

        });

        userName = Login.UserID;
        groupName = fNames[0];
        Log.e(TAG,groupName);
        page = (ViewFlipper) findViewById(R.id.flipper);
        animFlipInForeward = AnimationUtils.loadAnimation(this, R.anim.flipin);
        animFlipInBackward = AnimationUtils.loadAnimation(this, R.anim.flipin_reverse);

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
        initialSelf();

        final SelectAdapter selectAdapter = new SelectAdapter(this, friendInfo);
        selectF.setAdapter(selectAdapter);

        selectF.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "check on: " + friendInfo.get(position).getName());
                view.setSelected(true);
                //Toast.makeText(friendList.this, "check on: " + friends.get(position).getName(), Toast.LENGTH_LONG).show();
                if (friendInfo.get(position).isSelected()) {
                    friendInfo.get(position).setSelected(false);
                    view.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    friendInfo.get(position).setSelected(true);
                    view.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                }
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
                //markerOptions.title(pinName);

                Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.pinblue).copy(Bitmap.Config.ARGB_8888, true);
                BitmapDrawable draw = new BitmapDrawable(getResources(), bm);
                Bitmap drawBmp = draw.getBitmap();

                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(drawBmp))
                        .anchor(0.5f, 1);


                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                if (!isExist) {
                    mPin = mMap.addMarker(markerOptions);
                    pinID = mPin.getId();
                    isExist = true;
                } else {
                    mPin.setPosition(latLng);
                }
                Login.mLogCommandBuffer.add("change_pin:" + groupName + ":" + userName + ":" + latLng.latitude + ":" + latLng.longitude);
                Log.e(TAG, "login_commandBuffer_change_mPin");
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                Log.i(TAG, "CLICK MARKER!!!!");

                if (imageIds.contains(marker.getId())) {
                    Log.i(TAG, "CLICK IMAGE!!!!");
                    showImage(marker.getSnippet());
                } else {
                    if (marker.getId().equals(pinID)) {
                        open(marker);
                    } else {
                        Geocoder geocoder = new Geocoder(MapChat.this, Locale.getDefault());
                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1);
                        } catch (IOException e) {
                            Log.d(TAG, "IOException!");
                        }
                        // Print out address
                        if (addresses == null || addresses.size() == 0) {
                            marker.setSnippet("No address found.");
                        } else {
                            Address address = addresses.get(0);
                            String addr = "";
                            // Fetch the address lines using getAddressLine,
                            // join them, and send them to the thread.
                            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                                addr += address.getAddressLine(i) + " ";
                            }
                            marker.setSnippet(addr);
                        }
                    }
                }
                return false;
            }
        });

        mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "MapChat");

        if (!mediaStorageDir.exists()) mediaStorageDir.mkdirs();

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

    }

    public void showImage(final String fileName) {
        Log.i(TAG, "SHOW IMAGE!!!!");

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;

        Bitmap bitmap = BitmapFactory.decodeFile(fileName);
        int bitmapHeight = bitmap.getHeight();
        int bitmapWidth = bitmap.getWidth();

        while(bitmapHeight > (screenHeight - 250) || bitmapWidth > (screenWidth - 250)) {
            bitmapHeight = bitmapHeight / 2;
            bitmapWidth = bitmapWidth / 2;
        }

        BitmapDrawable resizedBitmap = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, bitmapWidth, bitmapHeight, false));

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.image_dialog_layout);

        ImageView image = (ImageView) dialog.findViewById(R.id.image_dialog);

        image.setBackground(resizedBitmap);
        dialog.getWindow().setBackgroundDrawable(null);

        dialog.show();
    }

    public Marker setPeopleMarker(int drawableColor, String peopleUserID, boolean hasText){

        Bitmap bm = BitmapFactory.decodeResource(getResources(), drawableColor).copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bm);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setTextSize(40);
        paint.setColor(Color.GRAY);

        if (hasText)
            canvas.drawText(peopleUserID, bm.getWidth()/3, bm.getHeight()/3, paint); // paint defines the text color, stroke width, size

        BitmapDrawable draw = new BitmapDrawable(getResources(), bm);
        Bitmap drawBmp = draw.getBitmap();
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(40.502661, -74.451771))
                .icon(BitmapDescriptorFactory.fromBitmap(drawBmp))
                .title(peopleUserID)
                .anchor(0.5f, 1));
        marker.setVisible(false);

        return marker;

    }

    private void initialSelf() {
        Log.d(TAG,"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + "enter initialSelf");
        mMarker = setPeopleMarker(colors[0], userName, true);
    }

    private void setUpMap() {
        Log.d(TAG,"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + "enter setUpMap");
        //if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            Log.v(TAG, "Set map");

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.moveCamera(CameraUpdateFactory.zoomTo(12));
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setCompassEnabled(true);
                mMap.setMyLocationEnabled(false);
            }
        //}
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
            if((e1.getX() - e2.getX()) > sensitvity) {
                SwipeLeft();
            } else if ((e2.getX() - e1.getX()) > sensitvity) {
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
                isExist = false;
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
        Log.d(TAG,"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + "enter initialFriend");
        int j = 1;
        for( int i = 1; i < fNames.length; i++) {
            if(!fNames[i].equals(userName)) {
                Marker m = setPeopleMarker(colors[j], fNames[i], true);

                Marker p = setPeopleMarker(colorspin[j], fNames[i], false);

                friendInfo.add(new myFriend(fNames[i], m, colors[j], p));
                Log.e(TAG, fNames[i]);
                j++;
            }
        }
    }

    public static void changeLocation(String name, LatLng latLng){
        Log.e(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + name + "/" + latLng.toString());
        for( int i = 0; i < friendInfo.size(); i++){
            if(friendInfo.get(i).getName().equals(name)){
                Log.e(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + name + "get!!!!!!!");
                friendInfo.get(i).setLocation(latLng);
            }
        }
    }

    public static void changeUserPin(String name, LatLng latLng){
        Log.e(TAG, "!!!!!!!!!!!!!!pin!!!!!!!!!!!!!!" + name + "/" + latLng.toString());
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
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Log.v(TAG, latLng.latitude + " " + latLng.longitude);
        double lat = latLng.latitude;
        double lng = latLng.longitude;

        mMarker.setPosition(latLng);
        mMarker.setVisible(true);

        Login.mLogCommandBuffer.add("update_location:" + groupName + ":" + userName + ":" + lat + ":" + lng);

        Log.e(TAG, "login_commandBuffer_update_location");

        if (!initialLocation) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            for (myFriend f : friendInfo) {
                f.getMarker().setPosition(latLng);
                f.getMarker().setVisible(true);
            }
            initialLocation = true;
        }
    }

    public static void setText(String name, String text, String notification){
        Log.d(TAG, "-----------------------------------------------setText entered!----------------------------------");
        for( int i = 0; i < friendInfo.size(); i++){
            if(friendInfo.get(i).getName().equals(name)){
                friendInfo.get(i).getMarker().setSnippet(text);
                friendInfo.get(i).getMarker().showInfoWindow();
                chatlog.append(text + "\n");
                if(notification.equals("yes")){
                    markerBounce(friendInfo.get(i).getMarker());
                    vibrator.vibrate(500);
                }
            }
        }
    }

    public void takePhoto(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
        fileUri = android.net.Uri.fromFile(mediaFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                new ImageShowTask().execute(mediaFile);
                Login.mLogCommandBuffer.add("send_photo:" + groupName + ":" + userName + ":" + mediaFile.length());

            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
    }

    public static void setImage(File file, String name) {
        MarkerOptions markerOptions = new MarkerOptions();
        for( int i = 0; i < friendInfo.size(); i++){
            if(friendInfo.get(i).getName().equals(name)){
                markerOptions.position(friendInfo.get(i).getMarker().getPosition());
                markerOptions.title("image");
                markerOptions.snippet(String.valueOf(file));

                Bitmap bm = BitmapFactory.decodeResource(mapChat.getResources(), colorsImage[i + 1]).copy(Bitmap.Config.ARGB_8888, true);

                BitmapDrawable draw = new BitmapDrawable(mapChat.getResources(), Bitmap.createScaledBitmap(bm, 108, 108, false));

                Bitmap drawBmp = draw.getBitmap();

                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(drawBmp))
                        .anchor(0.5f, 1);

                Marker marker = mMap.addMarker(markerOptions);
                imageIds.add(marker.getId());
            }
        }
    }

    public class ImageShowTask extends AsyncTask<File, Integer, MarkerOptions> {

        @Override
        protected MarkerOptions doInBackground(File... params) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
            markerOptions.title("image");
            markerOptions.snippet(String.valueOf(mediaFile));

            Bitmap bm = BitmapFactory.decodeResource(mapChat.getResources(), colorsImage[0]).copy(Bitmap.Config.ARGB_8888, true);
            BitmapDrawable draw = new BitmapDrawable(mapChat.getResources(), Bitmap.createScaledBitmap(bm, 120, 120, false));
            Bitmap drawBmp = draw.getBitmap();

            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(drawBmp))
                    .anchor(0.5f, 1);

            return markerOptions;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {

        }

        @Override
        protected void onPostExecute(MarkerOptions result) {
            Marker marker = mMap.addMarker(result);
            imageIds.add(marker.getId());
        }
    }

    public static void markerBounce(final Marker marker){
        final Handler handler = new Handler();
        final long startTime = SystemClock.uptimeMillis();
        final long duration = 2000;

        Projection projection = mMap.getProjection();
        final LatLng markerLatLng = marker.getPosition();
        Point startPoint = projection.toScreenLocation(markerLatLng);
        startPoint.offset(0,-100);
        final LatLng startLatLng = projection.fromScreenLocation(startPoint);

        final BounceInterpolator bounceInterpolator = new BounceInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - startTime;
                float t = bounceInterpolator.getInterpolation((float) elapsed / duration);
                double lng = t * markerLatLng.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * markerLatLng.latitude + (1 - t) * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    public static void removeUser(String usrName){
        for (int i = 0; i < MapChat.friendInfo.size(); i++){
            if (MapChat.friendInfo.get(i).getName().equals(usrName)){
                MapChat.friendInfo.get(i).getMarker().remove();
                MapChat.friendInfo.get(i).getUserPin().remove();
                MapChat.friendInfo.remove(i);
            }
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d(TAG, userName + "exit group");
        Login.mLogCommandBuffer.add("exit_group:" + groupName + ":" + userName);
        mGoogleApiClient.disconnect();
    }

}

