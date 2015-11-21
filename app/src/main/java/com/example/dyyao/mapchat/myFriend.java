package com.example.dyyao.mapchat;

/**
 * Created by mxizhang on 15/11/15.
 */
public class myFriend {

    private String userName;
    private boolean selected;

    public myFriend (String name){
        userName = name;
        selected = false;
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
}
