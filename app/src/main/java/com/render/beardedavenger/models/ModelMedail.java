package com.render.beardedavenger.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sati on 07/03/2015.
 */
public class ModelMedail {


    @SerializedName("title")
    private String titleMedail;

    @SerializedName("stat")
    private int progress;

    @SerializedName("needed")
    private int maxProgres;

    @SerializedName("unlocked")
    private boolean unlocked;

    @SerializedName("type")
    private int type;

    @SerializedName("unit")
    private String description;

    private int progressInitial;
    private int progressInitialPecentaje;

    public ModelMedail(String titleMedail, int progress, int maxProgres, boolean unlocked, int type, String description) {
        this.titleMedail = titleMedail;
        this.progress = progress;
        this.maxProgres = maxProgres;
        this.unlocked = unlocked;
        this.type = type;
        this.description = description;
        this.progressInitial = 0;
        this.progressInitialPecentaje = 0;
    }


    public void setProgressInitialPecentaje(int progressInitialPecentaje) {

        if (!unlocked) {
            int newProgressInitial = (int) ((progress / 100.0) * progressInitialPecentaje);

            if (newProgressInitial <= progress) {
                this.progressInitialPecentaje = (int) (100 * (newProgressInitial / (maxProgres * 1.0)));;
                progressInitial = newProgressInitial;
            }
        }
        else {
            this.progressInitialPecentaje = progressInitialPecentaje;
        }
    }

    public int getProgressInitial() {
        return progressInitial;
    }


    public int getProgressInitialPecentaje() {
        return progressInitialPecentaje;
    }

    public String getTitleMedail() {
        return titleMedail;
    }

    public int getProgress() {
        return progress;
    }

    public int getMaxProgres() {
        return maxProgres;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public int getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }
}
