/**
 * Friend structure
 *
 * @author Dongyang Yao
 *         Hua Deng
 *         Xi Zhang
 *         Lulu Zhao
 */

package com.example.dyyao.mapchat;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class Friend {

    // Name
    private String userName;

    // Is selected
    private boolean selected;

    // People marker reference
    private Marker userMarker;

    // Pin marker reference
    private Marker userPin;

    // Color assigned
    private int color;

    // Name Letter
    private static int[] iconImages = {R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e, R.drawable.f, R.drawable.g, R.drawable.h, R.drawable.i,
            R.drawable.j, R.drawable.k, R.drawable.l, R.drawable.m, R.drawable.n, R.drawable.o, R.drawable.p, R.drawable.q,
            R.drawable.r, R.drawable.s, R.drawable.t, R.drawable.u, R.drawable.v, R.drawable.w, R.drawable.x, R.drawable.y, R.drawable.z};

    private static int defaultSheep = R.drawable.sheep;

    /**
     * Constructor
     * @param name
     * @param marker
     * @param c
     * @param p
     */
    public Friend(String name, Marker marker, int c, Marker p) {
        userName = name;
        selected = false;
        userMarker = marker;
        color = c;
        userPin = p;
    }

    /**
     * Get name
     * @return
     */
    public String getName() {
        return userName;
    }

    /**
     * Set name
     * @param name
     */
    public void setName(String name) {
        this.userName = name;
    }

    /**
     * Is selected
     * @return
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Set selected
     * @param selected
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Set people marker's location
     * @param latLng
     */
    public void setLocation(LatLng latLng) {
        userMarker.setPosition(latLng);
    }

    /**
     * Set pin's location
     * @param latLng
     */
    public void setPin(LatLng latLng) {
        userPin.setPosition(latLng);
    }

    /**
     * Inverse selected status
     */
    public void inverseSelect() {
        if (selected) selected = false;
        else selected = true;
    }

    /**
     * Get people marker reference
     * @return
     */
    public Marker getMarker() {
        return userMarker;
    }

    /**
     * Get pin marker reference
     * @return
     */
    public Marker getUserPin() {
        return userPin;
    }

    /**
     * Get the color
     * @return
     */
    public int getColor() {
        return color;
    }

    /**
     * Get letter image for portrait
     * @return
     */
    public int getImage() {
        Character f = getName().charAt(0);
        switch (f) {
            case 'a':
                return iconImages[0];
            case 'b':
                return iconImages[1];
            case 'c':
                return iconImages[2];
            case 'd':
                return iconImages[3];
            case 'e':
                return iconImages[4];
            case 'f':
                return iconImages[5];
            case 'g':
                return iconImages[6];
            case 'h':
                return iconImages[7];
            case 'i':
                return iconImages[8];
            case 'j':
                return iconImages[9];
            case 'k':
                return iconImages[10];
            case 'l':
                return iconImages[11];
            case 'm':
                return iconImages[12];
            case 'n':
                return iconImages[13];
            case 'o':
                return iconImages[14];
            case 'p':
                return iconImages[15];
            case 'q':
                return iconImages[16];
            case 'r':
                return iconImages[17];
            case 's':
                return iconImages[18];
            case 't':
                return iconImages[19];
            case 'u':
                return iconImages[20];
            case 'v':
                return iconImages[21];
            case 'w':
                return iconImages[22];
            case 'x':
                return iconImages[23];
            case 'y':
                return iconImages[24];
            case 'z':
                return iconImages[25];
            default:
                return defaultSheep;

        }
    }


}
