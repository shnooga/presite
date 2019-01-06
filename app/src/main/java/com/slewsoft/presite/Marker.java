package com.slewsoft.presite;


import com.google.android.gms.maps.model.LatLng;
import com.slewsoft.presite.util.MarkerHelper.MarkerType;

/**
 * This will get persisted
 */
public class Marker {
    private String id;
    private LatLng location;
    public MarkerType type;
    public String detail1;
    public String detail2;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public Marker(String id, MarkerType type, String detail1, String detail2) {
        this.id = id;
        this.type = type;
        this.detail1 = detail1;
        this.detail2 = detail2;
    }
}
