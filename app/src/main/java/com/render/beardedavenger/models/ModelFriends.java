package com.render.beardedavenger.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sati on 07/03/2015.
 */
public class ModelFriends {


    @SerializedName("username")
    private String userName;
    @SerializedName("level")
    private int nivel;
    @SerializedName("teamId")
    private int team;
    @SerializedName("experience")
    private int exp;
    @SerializedName("nextLevel")
    private int maxExp;
    @SerializedName("avatar")
    private String urlImage;
    private int progressInitial;
    private int progressInitialPecentaje;


    public ModelFriends(String userName, int nivel, int team, int exp, int maxExp, String urlImage) {
        this.userName = userName;
        this.nivel = nivel;
        this.team = team;
        this.exp = exp;
        this.maxExp = maxExp;
        this.urlImage = urlImage;
        this.progressInitial = 0;
        this.progressInitialPecentaje = 0;
    }

    public void setProgressInitialPecentaje(int progressInitialPecentaje) {

        int newProgressInitial = (int) ((maxExp / 100.0) * progressInitialPecentaje);
        if (newProgressInitial <= exp) {
            this.progressInitialPecentaje = progressInitialPecentaje;
//            this.progressInitialPecentaje = (int) (100 * (newProgressInitial / (maxExp * 1.0)));;;

            progressInitial = newProgressInitial;
        }

    }

    public int getProgressInitial() {
        return progressInitial;
    }


    public int getProgressInitialPecentaje() {
        return progressInitialPecentaje;
    }

    public String getUserName() {
        return userName;
    }

    public int getNivel() {
        return nivel;
    }

    public int getTeam() {
        return team;
    }

    public int getExp() {
        return exp;
    }

    public int getMaxExp() {
        return maxExp;
    }

    public String getUrlImage() {
        return urlImage;
    }
}
