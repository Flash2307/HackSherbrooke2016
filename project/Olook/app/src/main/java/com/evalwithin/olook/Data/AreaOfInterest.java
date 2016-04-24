package com.evalwithin.olook.Data;

import java.io.Serializable;

/**
 * Created by Pascal on 23/04/2016.
 */
public class AreaOfInterest implements Serializable {

    protected double locX;
    protected double locY;
    protected String locationName;

    public AreaOfInterest(double locX, double locY, String locationName)
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
