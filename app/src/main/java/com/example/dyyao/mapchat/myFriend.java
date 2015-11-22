package com.example.dyyao.mapchat;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by mxizhang on 15/11/15.
 */
public class myFriend {

    private String userName;
    private boolean selected;
    private Marker userMarker;
    private Marker userPin;
    private int color;

    public myFriend (String name, Marker marker, int c, Marker p){
        userName = name;
        selected = false;
        userMarker = marker;
        color = c;
        userPin = p;
    }

    public String getName() {
        return userName;
    }

    public void setName(String name) {
        this.userName = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setLocation(LatLng latLng){
        userMarker.setPosition(latLng);
    }

    public void setPin(LatLng latLng){
        userPin.setPosition(latLng);
    }

    public Marker getMarker(){
        return userMarker;
    }
    public Marker getUserPin(){
        return userPin;
    }
    public int getColor() {
        return color;
    }

}
