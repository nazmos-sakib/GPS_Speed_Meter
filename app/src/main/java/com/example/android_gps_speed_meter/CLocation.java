package com.example.android_gps_speed_meter;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CLocation extends Location {

    private boolean bUserMetricUnits = true;

    public CLocation( Location location, boolean bUserMetricUnits) {
        super(location);

        this.bUserMetricUnits = bUserMetricUnits;
    }

    public boolean getUserMetricUnits(){
        return this.bUserMetricUnits;
    }

    public void setbUserMetricUnits(boolean bUserMetricUnits){
        this.bUserMetricUnits = bUserMetricUnits;
    }

    @Override
    public float distanceTo(@NonNull Location dest) {
        if (this.getUserMetricUnits()){
            //convert meter to feet
            return super.distanceTo(dest) * 3.28084f;
        }
        return super.distanceTo(dest);
    }

    @Override
    public float getAccuracy() {
        if (this.getUserMetricUnits()){
            //convert meter to feet
            return  super.getAccuracy() * 3.28084f;
        }
        return super.getAccuracy();
    }

    @Override
    public double getAltitude() {
        if (this.getUserMetricUnits()){
            //convert meter to feet
            return  super.getAltitude() * 3.28084d;
        }
        return super.getAltitude();
    }

    @Override
    public float getSpeed() {
        if (this.getUserMetricUnits()){
            //convert meter/second to mile/hour
            return  super.getSpeed() * 2.23693629f;
        }
        return super.getSpeed();
    }
}
