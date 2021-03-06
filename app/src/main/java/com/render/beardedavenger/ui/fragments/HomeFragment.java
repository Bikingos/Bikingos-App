package com.render.beardedavenger.ui.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.overlay.PathOverlay;
import com.mapbox.mapboxsdk.overlay.UserLocationOverlay;
import com.mapbox.mapboxsdk.views.MapView;
import com.render.beardedavenger.R;
import com.render.beardedavenger.async.VolleySingleton;
import com.render.beardedavenger.model.Base;
import com.render.beardedavenger.model.PowerPoint;
import com.render.beardedavenger.ui.FriendsActivity;
import com.render.beardedavenger.ui.PerfilActivity;
import com.render.beardedavenger.util.CirclePicture;
import com.render.beardedavenger.util.Constants;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.fabric.sdk.android.services.concurrency.AsyncTask;

public class HomeFragment extends Fragment
        implements GoogleApiClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, View.OnClickListener {

    private static final int SERVER_TIME_OUT = 5000;
    final String LOG_TAG = HomeFragment.class.getSimpleName();

    private boolean PLAY_MODE = false;

    private Context CONTEXT;
    private static final long INTERVAL_REFRESH_LOCATION = 15000;
    private static final float MIN_METERS = 14;

    private MapView mMap;
    private Location currentLocation;
    private ArrayList<Location> routePoints;
    private ArrayList<Marker> bases;
    private PathOverlay route;

    private GoogleApiClient mApiClient; // With this client we're gonna listen to location changes
    private FloatingActionButton mPlayButton;
    private FloatingActionButton mFriendsButton;
    private FloatingActionButton mStopPlayButton;
    private ImageButton userLocationButton;

    private ProgressDialog requestProgress;
    private CardView statCard;
    private CardView mUserCard;

    private ImageView imageViewUser;
    private TextView textViewUserName;
    private TextView kilometersTraveled;
    private TextView textViewLevel;
    private TextView textViewExp;
    private RoundCornerProgressBar roundCornerProgressBar;
    private int auxProgress;
    private int progressMax;
    private int progresExp;
    private Chronometer userChronometer;

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
        bases = new ArrayList<>();
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
        mStopPlayButton = (FloatingActionButton) rootView.findViewById(R.id.btn_stop);
        mUserCard = (CardView) rootView.findViewById(R.id.user_card);
        statCard = (CardView) rootView.findViewById(R.id.stat_card);
        imageViewUser = (ImageView) rootView.findViewById(R.id.img_user);
        userLocationButton = (ImageButton) rootView.findViewById(R.id.btn_user_location);
        userChronometer = (Chronometer) rootView.findViewById(R.id.chronometer);
        kilometersTraveled = (TextView) rootView.findViewById(R.id.txt_distance);

        textViewUserName = (TextView) rootView.findViewById(R.id.txt_username);
        textViewLevel = (TextView) rootView.findViewById(R.id.txt_lvl);
        textViewExp = (TextView) rootView.findViewById(R.id.txt_exp);
        roundCornerProgressBar = (RoundCornerProgressBar) rootView.findViewById(R.id.roundCornerProgressBar);


        rootView.findViewById(R.id.containerPerfilHome).setOnClickListener(this);

        mFriendsButton.setOnClickListener(this);
        mPlayButton.setOnClickListener(this);
        mStopPlayButton.setOnClickListener(this);
        userLocationButton.setOnClickListener(this);
        mUserCard.setOnClickListener(this);

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

    @Override
    public void onResume() {
        super.onResume();
        mApiClient.connect();
        getPowerPointsFromServer();
    }

    @Override
    public void onStop() {
        super.onStop();
        mApiClient.disconnect();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play:
                PLAY_MODE = true;
                initPlayModeIfIsAvailable();
                break;

            case R.id.btn_stop:
                PLAY_MODE = false;
                initPlayModeIfIsAvailable();

                try {
                    sendRoute(routePoints);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                clearData();

                break;

            case R.id.containerPerfilHome:
               launchProfileActivity();
               break;

            case R.id.btn_friends:
                Intent intent = new Intent(getActivity(), FriendsActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_user_location:
                mMap.setCenter(mMap.getUserLocation());
                break;

        }
    }


    /**
     * Methods for the LocationListener interface.
     * {@link com.google.android.gms.location.LocationListener}
     */

    @Override
    public void onLocationChanged(Location location) {

        if (currentLocation == null || currentLocation.distanceTo(location) >= MIN_METERS) {
            currentLocation = location;
            LatLng currentPoint = new LatLng(location.getLatitude(), location.getLongitude());
            //mMap.scrollTo(currentPoint.getLatitude(),currentPoint.getLongitude());

            if (PLAY_MODE) {
                route.addPoint(currentPoint);
                routePoints.add(location);
                kilometersTraveled.setText(getRouteInKilometers(routePoints) + "KM");

                mMap.getOverlays().remove(route);
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
     * Request Methods with volley
     */

    private void getPowerPointsFromServer() {

        StringRequest getPowerPoints = new StringRequest(Request.Method.GET,
                Constants.POWER_POINTS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            mMap.clear();
                            ArrayList<PowerPoint> powerPoints = parsePowerPoints(new JSONObject(response));
                            drawPointsInMap(powerPoints);
                            requestProgress.dismiss();

                            if(bases.isEmpty())
                                getBasesFromServer();
                            else {
                                drawMarkersSaved();
//                                new ProgressDataPerfil().execute();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            requestProgress.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                requestProgress.dismiss();

                Toast.makeText(getActivity(), R.string.request_update_failed, Toast.LENGTH_SHORT).show();
            }
        });

        VolleySingleton.getInstance(getActivity())
                .addToRequestQueue(getPowerPoints);

        setUpProgressDialog(R.string.power_points_request);
    }

    private void getBasesFromServer (){

        StringRequest getBases = new StringRequest(Request.Method.GET,
                Constants.GET_BASES_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i(LOG_TAG, response);
                            ArrayList<Base> bases = parseBases(new JSONArray(response));
                            drawBasesInMap(bases);
                            requestProgress.dismiss();
                            new ProgressDataPerfil().execute();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            requestProgress.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                requestProgress.dismiss();
            }
        });

        getBases.setRetryPolicy(new DefaultRetryPolicy(
                SERVER_TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS
        ));

        VolleySingleton.getInstance(getActivity())
                .addToRequestQueue(getBases);

        setUpProgressDialog(R.string.bases_request);
    }

    private void sendRoute(ArrayList<Location> route) throws JSONException {

        //Check if the route really has points
        if(route.isEmpty()){
            getPowerPointsFromServer();
            return;
        }

        final JSONObject jsonRoute = buildRoute(route);

        JsonObjectRequest postRoute = new JsonObjectRequest(Request.Method.POST
                , Constants.POST_ROUTE_URL, jsonRoute, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Todo: something with the response
                requestProgress.dismiss();
                getPowerPointsFromServer();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                requestProgress.dismiss();
                Toast.makeText(getActivity(), R.string.request_update_failed, Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
        postRoute.setRetryPolicy(new DefaultRetryPolicy(
                SERVER_TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS
        ));

        VolleySingleton.getInstance(getActivity())
                .addToRequestQueue(postRoute);

        setUpProgressDialog(R.string.post_route);
    }

    private void obtainUserInfo() {

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.PREFERENCE_USER, Context.MODE_PRIVATE);
        String urlPicturePerfil = "https://graph.facebook.com/" + sharedPreferences.getString(Constants.USER_ID, "") + "/picture?width=200&height=200";
        Picasso.with(getActivity()).load(urlPicturePerfil).placeholder(R.drawable.ic_person_white_48dp).transform(new CirclePicture()).into(imageViewUser);
        textViewUserName.setText(sharedPreferences.getString(Constants.USER_NAME, ""));

        textViewLevel.setText("Nivel " + sharedPreferences.getInt(Constants.USER_LEVEL, 1));
        textViewExp.setText("0/" + sharedPreferences.getInt(Constants.USER_MAX_EXPERENCE, 100) + " Exp");
        roundCornerProgressBar.setProgress(0);

        if (sharedPreferences.getString(Constants.USER_TEAM, "green").equals("green")) {
            roundCornerProgressBar.setProgressColor(getResources().getColor(R.color.accent_green));
        } else {
            roundCornerProgressBar.setProgressColor(getResources().getColor(R.color.accent_red));
        }

        progresExp = sharedPreferences.getInt(Constants.USER_EXPERENCE, 50);
        progressMax = sharedPreferences.getInt(Constants.USER_MAX_EXPERENCE, 50);

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
        mMap.goToUserLocation(true);
    }

    /**
     * Initialize everything that maps will need
     */
    private void setUpMap() {
        route = new PathOverlay(Color.RED, 5);
        mMap.setZoom(14);
        mMap.setMinZoomLevel(14);
        mMap.setMaxZoomLevel(18);
        mMap.goToUserLocation(true);

        userChronometer.stop();
    }

    private void setUpMapInGameMode() {
        mMap.clear();
        drawMarkersSaved();
        mMap.setZoom(17);
        mMap.setMinZoomLevel(17);
        mMap.setMaxZoomLevel(18);
        mMap.goToUserLocation(true);

        userChronometer.setBase(SystemClock.elapsedRealtime());
        userChronometer.start();
    }

    private void drawPointsInMap(ArrayList<PowerPoint> powerPoints) {
        for (PowerPoint powerPoint : powerPoints) {
            Marker pointMarker = new Marker(mMap, "", "", powerPoint.getLatLng());

            if (powerPoint.getTeam().equals(Constants.RED_TEAM)) {
                pointMarker.setMarker(getResources().getDrawable(R.drawable.ic_marker_red));
            } else if (powerPoint.getTeam().equals(Constants.GREEN_TEAM)) {
                pointMarker.setMarker(getResources().getDrawable(R.drawable.ic_marker_green));
            }

            mMap.addMarker(pointMarker);
        }
    }

    private void drawBasesInMap(ArrayList<Base> bases) {
        for (Base currentBase : bases) {
            Marker pointMarker = new Marker(mMap, "", "", currentBase.getCoordinates());

            if(currentBase.getStatus().equals(Constants.SPECIAL_BASE)){
                pointMarker.setMarker(getResources().getDrawable(R.drawable.ic_marker_yellow));
            }
            this.bases.add(pointMarker);
            mMap.addMarker(pointMarker);
        }

    }

    private void drawMarkersSaved (){
        for (Marker base: bases) {
            mMap.addMarker(base);
        }
    }

    private void clearData() {
        routePoints.clear();
        route.clearPath();
    }

    private void initPlayModeIfIsAvailable() {
        if (PLAY_MODE) {
            hideOptionButtons();
            showGameInterface();
            setUpMapInGameMode();

        } else {
            showOptionButtons();
            hideGameInterface();
            setUpMap();
        }
    }

    private void showOptionButtons() {

        mUserCard.animate()
                .translationY(0)
                .setInterpolator(new AnticipateOvershootInterpolator(0.03f));

        mFriendsButton.animate()
                .translationX(0)
                .setInterpolator(new AnticipateOvershootInterpolator(0.03f));

        mPlayButton.animate()
                .translationX(0)
                .setInterpolator(new AnticipateOvershootInterpolator(0.03f));

    }

    private void hideOptionButtons() {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mUserCard.getLayoutParams();
        int bottomMargin = lp.topMargin;

        mUserCard.animate()
                .translationY(-mFriendsButton.getHeight() - bottomMargin)
                .setInterpolator(new AnticipateOvershootInterpolator(0.03f));

        mFriendsButton.animate()
                .translationX(-mFriendsButton.getMeasuredWidth())
                .setInterpolator(new AnticipateOvershootInterpolator(0.03f));

        mPlayButton.animate()
                .translationX(mPlayButton.getMeasuredWidth())
                .setInterpolator(new AnticipateOvershootInterpolator(0.03f));
    }

    private void showGameInterface() {
        mStopPlayButton.setVisibility(View.VISIBLE);
        statCard.setVisibility(View.VISIBLE);

        mStopPlayButton.animate()
                .translationY(0)
                .setInterpolator(new AnticipateOvershootInterpolator(0.03f));

        statCard.animate()
                .translationY(0)
                .setInterpolator(new AnticipateOvershootInterpolator(0.03f));

    }

    private void hideGameInterface() {
        mStopPlayButton.animate()
                .translationY(mStopPlayButton.getWidth())
                .setInterpolator(new AnticipateOvershootInterpolator(0.03f));

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) statCard.getLayoutParams();
        int bottomMargin = lp.topMargin;

        statCard.animate()
                .translationY(-statCard.getHeight() - bottomMargin)
                .setInterpolator(new AnticipateOvershootInterpolator(0.03f));

    }

    /**
     * @param label id string resource
     */
    private void setUpProgressDialog(int label) {
        if (requestProgress == null)
            requestProgress = new ProgressDialog(getActivity());

        requestProgress.setIndeterminate(true);
        requestProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        requestProgress.setMessage(getString(label));
        requestProgress.onStart();
        requestProgress.show();
    }

    private ArrayList<PowerPoint> parsePowerPoints(JSONObject jsonObject) throws JSONException {
        ArrayList<PowerPoint> pointsFound = new ArrayList<>();
        JSONArray data = jsonObject.getJSONArray(Constants.DATA);

        for (int i = 0; i < data.length(); i++) {
            JSONObject currentPoint = data.getJSONObject(i);
            PowerPoint powerPoint = new PowerPoint();

            powerPoint.setTeam(currentPoint.getString(Constants.TEAM));
            powerPoint.setHealth(currentPoint.getInt(Constants.HEALTH));

            JSONObject coordinates = currentPoint.getJSONObject(Constants.COORDINATES);
            double lat = coordinates.getDouble(Constants.LATITUDE);
            double lon = coordinates.getDouble(Constants.LONGITUDE);

            LatLng pointLocation = new LatLng(lat, lon);
            powerPoint.setLatLng(pointLocation);

            pointsFound.add(powerPoint);
        }

        return pointsFound;
    }

    private ArrayList<Base> parseBases(JSONArray jsonArray) throws JSONException {
        ArrayList<Base> bases = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            Base currentBase = new Base();
            String status = jsonArray.getJSONObject(i)
                    .getString(Constants.STATUS);
            JSONObject coordinates = jsonArray.getJSONObject(i)
                    .getJSONObject(Constants.COORDINATES);
            LatLng baseLatLng = new LatLng(coordinates.getDouble(Constants.LATITUDE),
                    coordinates.getDouble(Constants.LONGITUDE));

            currentBase.setCoordinates(baseLatLng);
            currentBase.setStatus(status);

            bases.add(currentBase);
        }

        return bases;
    }

    private JSONObject buildRoute(ArrayList<Location> route) throws JSONException {
        //TODO: Change hardcoding
        JSONObject dataObject = new JSONObject();
        dataObject.put(Constants.USERNAME, "bob");
        dataObject.put(Constants.TEAM, "red");
        dataObject.put(Constants.KILOMETERS_TRAVELED, getRouteInKilometers(route));

        JSONArray points = new JSONArray();
        for (Location point : route) {
            JSONObject currentPoint = new JSONObject();

            JSONObject coordinates = new JSONObject();
            coordinates.put(Constants.LATITUDE, point.getLatitude());
            coordinates.put(Constants.LONGITUDE, point.getLongitude());

            currentPoint.put(Constants.COORDINATES, coordinates);
            points.put(currentPoint);
        }

        dataObject.put(Constants.POINTS, points);
        return dataObject;
    }

    public double getRouteInKilometers(ArrayList<Location> routePoints) {
        int routeInKilometers = 0;
        Location previousLocation = routePoints.get(0);

        for (int i = 1; i < routePoints.size(); i++) {
            routeInKilometers += previousLocation.distanceTo(routePoints.get(i)) ;
            previousLocation = routePoints.get(i);
        }

        return routeInKilometers/1000;
    }

    private class ProgressDataPerfil extends AsyncTask<Void, Integer, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            auxProgress = 0;
        }

        @Override
        protected Void doInBackground(Void... voids) {


            while (auxProgress < 100) {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                auxProgress++;

                publishProgress(auxProgress);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);


            int newProgress = (int) (values[0] * (progresExp / 100.0));

            if (newProgress <= progresExp) {
                int progress = (int) (100 * (newProgress / (progressMax * 1.0)));
                roundCornerProgressBar.setProgress(progress);

                textViewExp.setText(newProgress + "/"+progressMax+" Exp");
            }

        }
    }














}
