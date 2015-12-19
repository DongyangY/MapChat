/**
 * The map chat room
 * Synchronize both self and other members' location, message, pin, and image
 *
 * @author Dongyang Yao
 *         Hua Deng
 *         Xi Zhang
 *         Lulu Zhao
 */

package com.example.dyyao.mapchat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
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
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapChat extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    // Send message button
    private ImageButton bSend;

    // Show chat history button
    private ImageButton show;

    // Text field for message
    private EditText Inputchat;

    // Group member list
    public static ListView selectF;

    private ViewFlipper page;

    private Animation animFlipInForeward;

    private Animation animFlipInBackward;

    // Google map reference
    public static GoogleMap mMap;

    private String[] values;

    private String[] fNames;

    public static List<Friend> friendInfo;

    // Self location marker
    private Marker mMarker;

    // Self pin marker
    private Marker mPin = null;

    // Image for people
    private final int[] colors = {R.drawable.peopleblue, R.drawable.peoplered, R.drawable.peoplegreen, R.drawable.peopleyellow, R.drawable.peoplepurple};

    // Image for pins
    private final int[] colorspin = {R.drawable.pinblue, R.drawable.pinred, R.drawable.pingreen, R.drawable.pinyellow, R.drawable.pinpurple};

    // Image for photos
    private static final int[] colorsImage = {R.drawable.imageblue, R.drawable.imagered, R.drawable.imagegreen, R.drawable.imageyellow, R.drawable.imagepurple};

    // Reference for this
    public static MapChat mapChat;

    // Group name
    private String groupName;

    // Local user name
    private String userName;

    // Self pin
    private String pinID;

    public static final String TAG = "MapChat";

    private boolean initialLocation = false;

    private boolean isExist = false;

    // Self current location
    private Location currentLocation = null;

    // Google plat service for location
    private GoogleApiClient mGoogleApiClient;

    // Periodical request for location update
    private LocationRequest mLocationRequest;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    private Uri fileUri;

    public static File mediaStorageDir;

    private String timeStamp;

    public static File mediaFile;

    public static Vibrator vibrator;

    private static ArrayList<String> imageIds;

    private static ChatArrayAdapter chatArrayAdapter;

    private ListView listView;

    static boolean side = false;

    private boolean exited = false;

    private boolean takePhoto = false;

    private boolean isOnLog = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapchat);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Inputchat = (EditText) findViewById(R.id.et_input);
        selectF = (ListView) findViewById(R.id.listSelect);
        bSend = (ImageButton) findViewById(R.id.btn_send);
        friendInfo = new ArrayList<>();
        ClientTaskR.mMapChat = this;

        imageIds = new ArrayList<String>();

        show = (ImageButton) findViewById(R.id.btn_show);
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SwipeRight();
            }
        });

        mapChat = this;
        values = getIntent().getStringArrayExtra("friendNames");
        fNames = Arrays.copyOfRange(values, 2, values.length);

        // Send message
        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Inputchat.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please text something before sending ",
                            Toast.LENGTH_LONG).show();

                } else {
                    String sentence = Inputchat.getText().toString();
                    side = false;
                    sendChatMessage();
                    mMarker.setSnippet(sentence);
                    mMarker.showInfoWindow();
                    for (int i = 0; i < friendInfo.size(); i++) {
                        if (friendInfo.get(i).isSelected()) {
                            sentence += ":";
                            sentence += friendInfo.get(i).getName();
                        }
                    }
                    Login.mLogCommandBuffer.add("send_message:" + groupName + ":" + userName + ":" + sentence);
                    Inputchat.setText("");
                }

            }

        });

        userName = Login.UserID;
        groupName = fNames[0];

        page = (ViewFlipper) findViewById(R.id.flipper);
        animFlipInForeward = AnimationUtils.loadAnimation(this, R.anim.flipin);
        animFlipInBackward = AnimationUtils.loadAnimation(this, R.anim.flipin_reverse);

        // Connect google play service
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        // Request location updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000);       // 10 seconds, in milliseconds
        mLocationRequest.setFastestInterval(5000); // 5 second, in milliseconds

        setUpMap();

        initialFriend();

        initialSelf();

        // Bind group member list to view
        final SelectAdapter selectAdapter = new SelectAdapter(this, friendInfo);
        selectF.setAdapter(selectAdapter);

        // Action for a group member selected or unselected
        selectF.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);

                if (friendInfo.get(position).isSelected()) {
                    friendInfo.get(position).setSelected(false);
                    view.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    friendInfo.get(position).setSelected(true);
                    view.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                }
            }
        });

        // Set a negotiating pin after clicking for a long time on the map
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);

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

                // Send the request to change self pin location on others' map
                Login.mLogCommandBuffer.add("change_pin:" + groupName + ":" + userName + ":" + latLng.latitude + ":" + latLng.longitude);
            }
        });

        // Action when clicking the marker
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                // Click the image marker to show image
                if (imageIds.contains(marker.getId())) {
                    showImage(marker.getSnippet());
                } else {
                    // Click the pin marker to show address
                    if (marker.getId().equals(pinID)) {
                        open(marker);
                    } else {

                        // Get the location address
                        Geocoder geocoder = new Geocoder(MapChat.this, Locale.getDefault());
                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1);
                        } catch (IOException e) {

                        }
                        if (addresses == null || addresses.size() == 0) {
                            marker.setSnippet("No address found.");
                        } else {
                            Address address = addresses.get(0);
                            String addr = "";
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

        listView = (ListView) findViewById(R.id.listView1);

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.activity_chat_singlemessage);
        listView.setAdapter(chatArrayAdapter);

        Inputchat = (EditText) findViewById(R.id.et_input);

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });

    }

    /**
     * Exit the group
     */
    @Override
    protected void onStop() {
        super.onStop();

        // Do not exit group if is taking the photo
        if (!exited && !takePhoto) {
            Log.d(TAG, userName + "exit group");
            Login.mLogCommandBuffer.add("exit_group:" + groupName + ":" + userName);
            mGoogleApiClient.disconnect();
            exited = true;
        }
    }

    /**
     * Swipe the chat history view
     */
    @Override
    public void onBackPressed() {
        if (isOnLog) {
            SwipeLeft();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Back from taking the photo
     */
    @Override
    protected void onRestart() {
        super.onRestart();

        if (!takePhoto) {
            finish();
        }

        takePhoto = false;
    }

    /**
     * Log the chat message from self
     * @return
     */
    private boolean sendChatMessage() {
        chatArrayAdapter.add(new ChatMessage(side, Inputchat.getText().toString()));
        Inputchat.setText("");
        return true;
    }

    /**
     * Display the image
     * @param fileName image file directory
     */
    public void showImage(final String fileName) {

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;

        Bitmap bitmap = BitmapFactory.decodeFile(fileName);

        BitmapDrawable resizedBitmap = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, screenWidth, screenHeight, false));

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.image_dialog_layout);

        ImageView image = (ImageView) dialog.findViewById(R.id.image_dialog);

        image.setBackground(resizedBitmap);
        dialog.getWindow().setBackgroundDrawable(null);

        dialog.show();
    }

    /**
     * Set people marker on map
     * @param drawableColor
     * @param peopleUserID
     * @param hasText
     * @return
     */
    public Marker setPeopleMarker(int drawableColor, String peopleUserID, boolean hasText) {

        Bitmap bm = BitmapFactory.decodeResource(getResources(), drawableColor).copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bm);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setTextSize(40);
        paint.setColor(Color.GRAY);

        if (hasText)
            canvas.drawText(peopleUserID, bm.getWidth() / 3, bm.getHeight() / 3, paint); // paint defines the text color, stroke width, size

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

    /**
     * Init self marker
     */
    private void initialSelf() {
        mMarker = setPeopleMarker(colors[0], userName, true);
    }

    /**
     * Init group members' marker
     */
    private void initialFriend() {
        int j = 1;
        for (int i = 1; i < fNames.length; i++) {
            if (!fNames[i].equals(userName)) {
                Marker m = setPeopleMarker(colors[j], fNames[i], true);

                Marker p = setPeopleMarker(colorspin[j], fNames[i], false);

                friendInfo.add(new Friend(fNames[i], m, j, p));
                Log.e(TAG, fNames[i]);
                j++;
            }
        }
    }

    /**
     * Init the Google map
     */
    private void setUpMap() {
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        // Check if we were successful in obtaining the map.
        if (mMap != null) {
            mMap.moveCamera(CameraUpdateFactory.zoomTo(12));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.setMyLocationEnabled(false);
        }
    }

    /**
     * Change the local group member' location
     * Callback when receiving the update request from server
     * @param name The group member's name
     * @param latLng The updating location
     */
    public static void changeLocation(String name, LatLng latLng) {
        for (int i = 0; i < friendInfo.size(); i++) {
            if (friendInfo.get(i).getName().equals(name)) {
                friendInfo.get(i).setLocation(latLng);
            }
        }
    }

    /**
     * Change the local group member's pin location
     * Callback when receiving the update request from server
     * @param name The group member's name
     * @param latLng The updating location
     */
    public static void changeUserPin(String name, LatLng latLng) {
        for (int i = 0; i < friendInfo.size(); i++) {
            if (friendInfo.get(i).getName().equals(name)) {
                friendInfo.get(i).getUserPin().setVisible(true);
                friendInfo.get(i).setPin(latLng);
            }
        }
    }

    /**
     * Show chat history
     */
    private void SwipeRight() {
        isOnLog = true;
        page.setInAnimation(animFlipInBackward);
        page.showPrevious();
    }

    /**
     * Dismiss chat history
     */
    private void SwipeLeft() {
        isOnLog = false;
        page.setInAnimation(animFlipInForeward);
        page.showNext();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        return gestureDetector.onTouchEvent(event);
    }

    GestureDetector.SimpleOnGestureListener simpleOnGestureListener
            = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {

            float sensitvity = 50;
            if ((e1.getX() - e2.getX()) > sensitvity) {
                SwipeLeft();
            } else if ((e2.getX() - e1.getX()) > sensitvity) {
                SwipeRight();
            }

            return true;
        }

    };

    GestureDetector gestureDetector = new GestureDetector(simpleOnGestureListener);

    /**
     * Delete self's pin marker
     * @param marker pin marker reference
     */
    public void open(final Marker marker) {
        AlertDialog.Builder alerDialogBuilder = new AlertDialog.Builder(this);
        alerDialogBuilder.setMessage("Do you want to delete this marker?");
        alerDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MapChat.this, "You have deleted the marker", Toast.LENGTH_LONG).show();
                marker.remove();
                isExist = false;

                // Send request for deleting self's pin marker to server
                Login.mLogCommandBuffer.add("change_pin:" + groupName + ":" + userName + ":" + 0 + ":" + 0);
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

    /**
     * Callback when google play service connected
     * @param bundle
     */
    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            // Center camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /**
     * Callback when self's location updated
     * @param location
     */
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

        // Send self location update request to server
        Login.mLogCommandBuffer.add("update_location:" + groupName + ":" + userName + ":" + lat + ":" + lng);

        if (!initialLocation) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            for (Friend f : friendInfo) {
                f.getMarker().setPosition(latLng);
                f.getMarker().setVisible(true);
            }
            initialLocation = true;
        }
    }

    /**
     * Add group member's message to local
     * Triggered by server's command
     * @param name
     * @param text
     * @param notification
     */
    public static void setText(String name, String text, String notification) {
        for (int i = 0; i < friendInfo.size(); i++) {
            if (friendInfo.get(i).getName().equals(name)) {
                friendInfo.get(i).getMarker().setSnippet(text);
                friendInfo.get(i).getMarker().showInfoWindow();
                side = true;
                chatArrayAdapter.add(new ChatMessage(side, name + ": " + text));
                if (notification.equals("yes")) {
                    bounceMarker(friendInfo.get(i).getMarker());
                    vibrator.vibrate(500);
                }
            }
        }
    }

    /**
     * Take a photo using implicit activity
     * @param view
     */
    public void takePhoto(View view) {
        takePhoto = true;

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");
        fileUri = android.net.Uri.fromFile(mediaFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    /**
     * Save the photo and send to group members
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                new ImageShowTask().execute(mediaFile);

                // Send photo to group members
                Login.mPhotoCommandBuffer.add("send_photo:" + groupName + ":" + userName + ":" + mediaFile.length());
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
    }

    /**
     * Receive the image from other group members via server
     * @param file The local directory for saved image
     * @param name The user name who take the photo
     */
    public static void setImage(File file, String name) {
        MarkerOptions markerOptions = new MarkerOptions();
        for (int i = 0; i < friendInfo.size(); i++) {
            if (friendInfo.get(i).getName().equals(name)) {
                markerOptions.position(friendInfo.get(i).getMarker().getPosition());
                markerOptions.title("image");
                markerOptions.snippet(String.valueOf(file));

                Bitmap bm = BitmapFactory.decodeResource(mapChat.getResources(), colorsImage[friendInfo.get(i).getColor()]).copy(Bitmap.Config.ARGB_8888, true);

                BitmapDrawable draw = new BitmapDrawable(mapChat.getResources(), Bitmap.createScaledBitmap(bm, 108, 108, false));

                Bitmap drawBmp = draw.getBitmap();

                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(drawBmp))
                        .anchor(0.5f, 1);

                Marker marker = mMap.addMarker(markerOptions);
                imageIds.add(marker.getId());
            }
        }
    }

    /**
     * Show image
     */
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

    /**
     * The animation for marker bounce
     * @param marker
     */
    public static void bounceMarker(final Marker marker) {
        final Handler handler = new Handler();
        final long startTime = SystemClock.uptimeMillis();
        final long duration = 2000;

        Projection projection = mMap.getProjection();
        final LatLng markerLatLng = marker.getPosition();
        Point startPoint = projection.toScreenLocation(markerLatLng);
        startPoint.offset(0, -100);
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

    /**
     * Remover a group member
     * @param usrName
     */
    public static void removeUser(String usrName) {
        for (int i = 0; i < MapChat.friendInfo.size(); i++) {
            if (MapChat.friendInfo.get(i).getName().equals(usrName)) {
                MapChat.friendInfo.get(i).getMarker().remove();
                MapChat.friendInfo.get(i).getUserPin().remove();
                MapChat.friendInfo.remove(i);
            }
        }
    }

}
