package com.evalwithin.olook.Data;

import java.io.Serializable;

/**
 * Created by Pascal on 23/04/2016.
 */
public class AreaOfInterest implements Serializable {

    protected double locX;
    protected double locY;
    protected String locationName;
    protected String locationDesc;

    public AreaOfInterest(double locX, double locY, String locationName, String locationDesc)
    {
        this.locX = locX;
        this.locY = locY;
        this.locationDesc = locationDesc;
        this.locationDesc = locationDesc;
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

    public String getLocationDesc(){
        return locationDesc;
    }
}
