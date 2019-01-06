package com.slewsoft.presite.fragment;


import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.model.LatLng;
import com.slewsoft.presite.DraggableCircle;
import com.slewsoft.presite.Marker;
import com.slewsoft.presite.util.LocationHelper;

import java.util.List;

public class PreSiteEventHandler
        implements
        OnMapLongClickListener,
        OnMarkerClickListener,
        OnMarkerDragListener {

    private static double DEFAULT_CRANE_RADIUS_FT = 150;
    private GoogleMap mMap;
    private Context mContext;
    private LocationHelper mLocationHelper = new LocationHelper();
    public List<DraggableCircle> mCraneMarkers;
    public List<com.google.android.gms.maps.model.Marker> mUnitMarkers;

    public PreSiteEventHandler(GoogleMap mMap, Context mContext, List<DraggableCircle> mCraneMarkers, List<com.google.android.gms.maps.model.Marker> mUnitMarkers) {
        this.mMap = mMap;
        this.mContext = mContext;
        this.mCraneMarkers = mCraneMarkers;
        this.mUnitMarkers = mUnitMarkers;
    }

    @Override
    public void onMapLongClick(LatLng point) {
        int craneCount = mCraneMarkers.size() + 1;
        DraggableCircle circle = new DraggableCircle("Crane " + craneCount, mMap, point, DEFAULT_CRANE_RADIUS_FT);
        mCraneMarkers.add(circle);
    }

    @Override
    public boolean onMarkerClick(final com.google.android.gms.maps.model.Marker marker) {
        Marker info = (Marker) marker.getTag();
        switch (info.type) {
            case Unit:
                marker.setSnippet(info.detail1 + " " + info.detail2);
                break;
            case Crane:
                com.google.android.gms.maps.model.Marker unit = findNearestUnit(marker);
                String dist = mLocationHelper.distanceBetweenInFt(unit.getPosition(), marker.getPosition());
                marker.setSnippet(marker.getTitle() + " is " + dist + " feet from " + unit.getTitle());
                break;
            default:
                break;
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    private com.google.android.gms.maps.model.Marker findNearestUnit(com.google.android.gms.maps.model.Marker crane) {
        com.google.android.gms.maps.model.Marker retVal = null;
        float dist = Float.MAX_VALUE;
        float results[] = new float[1];

        for (com.google.android.gms.maps.model.Marker unit : mUnitMarkers) {
            Location.distanceBetween(crane.getPosition().latitude,
                    crane.getPosition().longitude,
                    unit.getPosition().latitude,
                    unit.getPosition().longitude, results);
            if (results[0] < dist) retVal = unit;
        }
        return retVal;
    }

    @Override
    public void onMarkerDragStart(com.google.android.gms.maps.model.Marker marker) {
        onMarkerMoved(marker);
    }

    @Override
    public void onMarkerDragEnd(com.google.android.gms.maps.model.Marker marker) {
        onMarkerMoved(marker);
    }

    @Override
    public void onMarkerDrag(com.google.android.gms.maps.model.Marker marker) {
        onMarkerMoved(marker);
    }

    private void onMarkerMoved(com.google.android.gms.maps.model.Marker marker) {
        for (DraggableCircle draggableCircle : mCraneMarkers) {
            if (draggableCircle.onMarkerMoved(marker)) {
                break;
            }
        }
    }
}
