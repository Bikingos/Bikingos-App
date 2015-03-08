package com.render.beardedavenger.models;

/**
 * Created by sati on 07/03/2015.
 */
public class ModelMedail {

    private String titleMedail;
    private int progress;
    private int maxProgres;
    private boolean unlocked;
    private int type;
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

        int newProgressInitial = (int) ((maxProgres / 100.0)*progressInitialPecentaje);

        if (newProgressInitial<=progress) {
            this.progressInitialPecentaje = progressInitialPecentaje;
            progressInitial = newProgressInitial;
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
