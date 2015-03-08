package com.render.beardedavenger.model;


import com.mapbox.mapboxsdk.geometry.LatLng;

public class Base {
    String status;
    LatLng coordinates;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(LatLng coordinates) {
        this.coordinates = coordinates;
    }
}
