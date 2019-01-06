package com.slewsoft.presite.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.slewsoft.presite.DraggableCircle;
import com.slewsoft.presite.Marker;
import com.slewsoft.presite.util.LocationHelper;
import com.slewsoft.presite.util.MarkerHelper;
import com.slewsoft.presite.R;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class PreSite extends Fragment implements OnMapReadyCallback, View.OnClickListener {
    private static final LatLng DISNEYLAND = new LatLng(33.812324, -117.918942);

    private GoogleMap mMap;
    private View mView;
    private LocationHelper mLocationHelper = new LocationHelper();
    private MarkerHelper mMarkerHelper = new MarkerHelper();
    private List<com.google.android.gms.maps.model.Marker> mUnitMarkers = new ArrayList<>();
    private PreSiteEventHandler mEventHandler;
    private List<DraggableCircle> mCraneMarkers = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.presite, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mEventHandler = new PreSiteEventHandler(mMap, getActivity(), mCraneMarkers, mUnitMarkers);

        mView.findViewById(R.id.go_to_address).setOnClickListener(this);
        mView.findViewById(R.id.edit_site).setOnClickListener(this);

        mMap.setOnMarkerDragListener(mEventHandler);
        mMap.setOnMapLongClickListener(mEventHandler);
        mMap.setOnMarkerClickListener(mEventHandler); // Displays a Toast

        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DISNEYLAND, 7));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.go_to_address:
                zoomToLocation(view);
                break;
            case R.id.edit_site:
                openEditSiteDialog(view);
                break;
            default:
        }
    }

    private void openEditSiteDialog(View view) {
//        EditSiteFragment dialog = new EditSiteFragment();
//        dialog.show(getFragmentManager(), "Edit Job Site");
        takeSnapshot();
    }

    private void zoomToLocation(View view) {
        try {
            hideSoftKeyboard(view);

            String address = ((EditText) mView.findViewById(R.id.edit_address)).getText().toString();
            LatLng newLocation = mLocationHelper.getLocation(address, getActivity());

            clearCurrentMarkers();
            mUnitMarkers.add(createUnit(mMap, newLocation, "Unit 1", "1 ton HVAC", "Trane XAB"));
            mLocationHelper.goToLocation(mMap, newLocation, 18);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void takeSnapshot() {
        if (mMap == null) {
            return;
        }

//        final ImageView snapshotHolder = (ImageView) findViewById(R.id.snapshot_holder);

        final GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                // Callback is called from the main thread, so we can modify the ImageView safely.
//                snapshotHolder.setImageBitmap(snapshot);

                Toast.makeText(getActivity(), "Snapped a pix of Map", Toast.LENGTH_SHORT).show();
            }
        };

//        if (mWaitForMapLoadCheckBox.isChecked()) {
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.snapshot(callback);
            }
        });
//        } else {
//            mMap.snapshot(callback);
//        }
    }


    private void clearCurrentMarkers() {
        mUnitMarkers.clear();
        mCraneMarkers.clear();
        mMap.clear();
    }

    public com.google.android.gms.maps.model.Marker createUnit(GoogleMap map, LatLng location, String title, String... details) {
        com.google.android.gms.maps.model.Marker unit = map.addMarker(new MarkerOptions()
                .position(location)
                .title(title)
//                .snippet(snippet)
                .draggable(true));
        Marker info = mMarkerHelper.createUnitInfo(details);
        unit.setTag(info);

        unit.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        unit.showInfoWindow();
        return unit;
    }

    private void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}