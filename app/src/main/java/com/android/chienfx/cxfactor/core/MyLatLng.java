package com.android.chienfx.cxfactor.core;

import com.android.chienfx.cxfactor.core.helper.MyHelper;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.Map;

public class MyLatLng implements Serializable {
    public double latitude;
    public double longitude;
    public String date;
    public String time;

    public MyLatLng(){
        latitude = 0.0f;
        longitude = 0.0f;
        date = "empty";
        time = "empty";
    }

    public MyLatLng(double lat, double lng, String date, String time){
        latitude = lat;
        longitude = lng;
        this.date = date;
        this.time = time;
    }

    public static MyLatLng createLatLng(LatLng loc) {
        MyLatLng m = new MyLatLng(loc.latitude, loc.longitude, MyHelper.getStringToday(), MyHelper.getDayTime());
        return m;
    }
}
