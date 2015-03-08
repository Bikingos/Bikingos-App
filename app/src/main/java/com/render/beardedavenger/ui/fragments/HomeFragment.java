package com.render.beardedavenger.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.render.beardedavenger.ui.FriendsActivity;
import com.render.beardedavenger.ui.PerfilActivity;
import com.render.beardedavenger.util.CirclePicture;
import com.render.beardedavenger.util.Constants;
import com.squareup.picasso.Picasso;

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
    private FloatingActionButton mFriendsButton;
    private FloatingActionButton mStopPlayButton;
//    private CardView mUserCard;


    private ImageView imageViewUser;
    private TextView textViewUserName;

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
        mFriendsButton = (FloatingActionButton) rootView.findViewById(R.id.btn_friends);
        mStopPlayButton = (FloatingActionButton)rootView.findViewById(R.id.btn_stop);
//        mUserCard = (CardView) rootView.findViewById(R.id.user_card);
        rootView.findViewById(R.id.containerPerfilHome).setOnClickListener(this);

        imageViewUser = (ImageView) rootView.findViewById(R.id.img_user);

        textViewUserName = (TextView) rootView.findViewById(R.id.txt_username);

        mFriendsButton.setOnClickListener(this);
        mPlayButton.setOnClickListener(this);
        mStopPlayButton.setOnClickListener(this);
//        mUserCard.setOnClickListener(this);

        mStopPlayButton.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                hideGameInterface();
                mStopPlayButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });


        setUpMap();
        setUpUserLocation();

        obtainUserInfo();

        return rootView;
    }

    private void obtainUserInfo () {

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFERENCE_USER, Context.MODE_PRIVATE);

        String urlPicturePerfil = "https://graph.facebook.com/" + sharedPreferences.getString(Constants.USER_ID,"") + "/picture?width=200&height=200";
        Picasso.with(getActivity()).load(urlPicturePerfil).placeholder(R.drawable.ic_person_white_48dp).transform(new CirclePicture()).into(imageViewUser);

        textViewUserName.setText(sharedPreferences.getString(Constants.USER_NAME,""));


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
                PLAY_MODE = true;
                initPlayModeIfIsAvailable();
                break;

            case R.id.btn_stop:
                PLAY_MODE = false;
                initPlayModeIfIsAvailable();
                break;

            case R.id.containerPerfilHome:
                launchProfileActivity();
               break;

            case R.id.btn_friends:
                Intent intent = new Intent(getActivity(), FriendsActivity.class);
                startActivity(intent);
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

    private void launchProfileActivity() {
        Intent profileIntent = new Intent(getActivity(), PerfilActivity.class);
        startActivity(profileIntent);
    }

    /**
     * Setting the mapbox tracking mode
     */
    private void setUpUserLocation() {
        mMap.setUserLocationEnabled(true);
        mMap.setUserLocationTrackingMode(UserLocationOverlay.TrackingMode.FOLLOW);
        mMap.setUserLocationRequiredZoom(14);
    }

    /**
     * Initialize everything that maps will need
     */
    private void setUpMap() {
        route = new PathOverlay(Color.RED, 5);
    }

    private void initPlayModeIfIsAvailable() {
        if(PLAY_MODE){
            hideOptionButtons();
            showGameInterface();
        }
        else {
            showOptionButtons();
            hideGameInterface();
        }
    }

    private void showOptionButtons() {
        mFriendsButton.animate()
                .translationX(0)
                .setInterpolator(new AnticipateOvershootInterpolator(0.03f));

        mPlayButton.animate()
                .translationX(0)
                .setInterpolator(new AnticipateOvershootInterpolator(0.03f));

    }

    private void hideOptionButtons() {
        mFriendsButton.animate()
                .translationX(-mFriendsButton.getMeasuredWidth())
                .setInterpolator(new AnticipateOvershootInterpolator(0.03f));

        mPlayButton.animate()
                .translationX(mPlayButton.getMeasuredWidth())
                .setInterpolator(new AnticipateOvershootInterpolator(0.03f));
    }

    private void showGameInterface() {
        mStopPlayButton.setVisibility(View.VISIBLE);

        mStopPlayButton.animate()
                .translationY(0)
                .setInterpolator(new AnticipateOvershootInterpolator(0.03f));

    }

    private void hideGameInterface() {
        mStopPlayButton.animate()
                .translationY(mStopPlayButton.getWidth())
                .setInterpolator(new AnticipateOvershootInterpolator(0.03f));

    }

}
