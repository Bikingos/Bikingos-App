package com.render.beardedavenger.model;


import com.mapbox.mapboxsdk.geometry.LatLng;

/**
 * Created by Silmood on 08/03/2015.
 * 7
 * A simple POJO for the power point in map
 */
public class PowerPoint {

    String team;
    int health;
    com.mapbox.mapboxsdk.geometry.LatLng coordinates;

    public String getTeam() {
        return this.team;
    }

    public void setTeam(String value) {
        this.team = value;
    }

    public int getHealth() {
        return this.health;
    }

    public void setHealth(int value) {
        this.health = value;
    }

    public LatLng getLatLng() {
        return this.coordinates;
    }

    public void setLatLng(LatLng value) {
        this.coordinates = value;
    }
}
