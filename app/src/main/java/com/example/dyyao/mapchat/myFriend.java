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
    private Marker marker;

    public myFriend (String name){
        userName = name;
        selected = false;
        //marker.setTitle(name);
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
        marker.setPosition(latLng);
    }

    public Marker getMarker(){
        return marker;
    }
}
