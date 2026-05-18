package com.example.examen_test;

import androidx.annotation.NonNull;

public class InterventieAuto {
    private String modelAuto;
    private float pret;
    private int durataOre;
    private boolean pieseIncluse;
    private String numeMecanic;

    public InterventieAuto() {
    }

    public InterventieAuto(String modelAuto, float pret, int durataOre, boolean pieseIncluse, String numeMecanic) {
        this.modelAuto = modelAuto;
        this.pret = pret;
        this.durataOre = durataOre;
        this.pieseIncluse = pieseIncluse;
        this.numeMecanic = numeMecanic;
    }

    public String getModelAuto() {
        return modelAuto;
    }

    public void setModelAuto(String modelAuto) {
        this.modelAuto = modelAuto;
    }

    public float getPret() {
        return pret;
    }

    public void setPret(float pret) {
        this.pret = pret;
    }

    public int getDurataOre() {
        return durataOre;
    }

    public void setDurataOre(int durataOre) {
        this.durataOre = durataOre;
    }

    public boolean isPieseIncluse() {
        return pieseIncluse;
    }

    public void setPieseIncluse(boolean pieseIncluse) {
        this.pieseIncluse = pieseIncluse;
    }

    public String getNumeMecanic() {
        return numeMecanic;
    }

    public void setNumeMecanic(String numeMecanic) {
        this.numeMecanic = numeMecanic;
    }

    @NonNull
    @Override
    public String toString() {
        return "InterventieAuto{" +
                "modelAuto='" + modelAuto + '\'' +
                ", pret=" + pret +
                ", durataOre=" + durataOre +
                ", pieseIncluse=" + pieseIncluse +
                ", numeMecanic='" + numeMecanic + '\'' +
                '}';
    }
}
