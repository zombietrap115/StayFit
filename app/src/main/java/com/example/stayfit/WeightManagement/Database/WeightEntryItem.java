package com.example.stayfit.WeightManagement.Database;

public class WeightEntryItem {
    public int id;
    public String date;
    public float weight;
    public String weightWithUnit;
    public boolean selected; // Neue Eigenschaft für die Auswahl


    public WeightEntryItem(int id, String date, float weight) {
        this.id = id;
        this.date = date;
        this.weight = weight;
        this.weightWithUnit = weight + " Kg";
        this.selected = false; // Standardmäßig nicht ausgewählt
    }
}
