package com.render.beardedavenger.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.PathOverlay;
import com.mapbox.mapboxsdk.overlay.UserLocationOverlay;
import com.mapbox.mapboxsdk.views.MapView;
import com.render.beardedavenger.R;

import java.util.ArrayList;

public class HomeFragment extends Fragment
        implements GoogleApiClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, View.OnClickListener {

    final String LOG_TAG = HomeFragment.class.getSimpleName();

    private boolean PLAY_MODE = false;

    private Context CONTEXT;
    private static final long INTERVAL_REFRESH_LOCATION = 15000;
    private static final float MIN_METERS = 14;

    private MapView mMap;
    private Location currentLocation;
    private ArrayList<LatLng> routePoints;
    private PathOverlay route;

    private GoogleApiClient mApiClient; // With this client we're gonna listen to location changes
    private FloatingActionButton mPlayButton;
    private FloatingActionButton mMenuButton;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        CONTEXT = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        routePoints = new ArrayList<>();
        mApiClient = new GoogleApiClient.Builder(CONTEXT)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mMap = (MapView) rootView.findViewById(R.id.game_map);
        mPlayButton = (FloatingActionButton) rootView.findViewById(R.id.btn_play);
        mPlayButton.setOnClickListener(this);

        setUpMap();
        setUpButtons();
        setUpUserLocation();

        return rootView;
    }



    @Override
    public void onResume() {
        super.onResume();
        mApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mApiClient.disconnect();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_play:
                PLAY_MODE = !PLAY_MODE;
                break;
        }
    }

    /**
     * Methods for the LocationListener interface.
     * {@link com.google.android.gms.location.LocationListener}
     */

    @Override
    public void onLocationChanged(Location location) {

        if(currentLocation == null || currentLocation.distanceTo(location)>= MIN_METERS){
            currentLocation = location;
            LatLng currentPoint = new LatLng(location.getLatitude(), location.getLongitude());

            if(PLAY_MODE){
                route.addPoint(currentPoint);
                routePoints.add(currentPoint);

                mMap.clear();
                mMap.getOverlays().add(route);
            }
        }

    }

    /**
     * Methods for the ConnectionCallbacks interface.
     * {@link GoogleApiClient.ConnectionCallbacks}
     */

    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(INTERVAL_REFRESH_LOCATION);

        LocationServices.FusedLocationApi.requestLocationUpdates(mApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     * Methods for the ConnectionFailedCallbacks interface.
     * {@link com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener}
     */

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /**
     * Setting the mapbox tracking mode
     */
    private void setUpUserLocation() {
        mMap.setUserLocationEnabled(true);
        mMap.setUserLocationTrackingMode(UserLocationOverlay.TrackingMode.FOLLOW);
        mMap.setUserLocationRequiredZoom(10);
    }

    /**
     * Initialize everything that maps will need
     */
    private void setUpMap() {
        route = new PathOverlay(Color.RED, 5);
    }

    private void setUpButtons() {
    }

}
