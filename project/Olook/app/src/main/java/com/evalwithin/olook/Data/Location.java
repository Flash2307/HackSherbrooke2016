package com.evalwithin.olook.Data;

/**
 * Created by Pascal on 23/04/2016.
 */
public class Location {

    protected double locX;
    protected double locY;
    protected String locationName;

    public Location(double locX, double locY, String locationName)
    {
        this.locX = locX;
        this.locY = locY;
        this.locationName = locationName;
    }

    public double getLocX() {
        return locX;
    }

    public double getLocY() {
        return locY;
    }

    public String getLocationName() {
        return locationName;
    }
}
