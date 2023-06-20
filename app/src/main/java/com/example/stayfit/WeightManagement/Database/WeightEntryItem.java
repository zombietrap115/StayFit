package com.example.stayfit.WeightManagement.Database;

/**
 * Repräsentiert einen einzelnen Gewichtseintrag, der Informationen
 * wie ID, Datum, Gewicht, Gewicht mit Einheit und Auswahlinformationen enthält.
 */
public class WeightEntryItem {

    public int id;
    public String date;
    public float weight;
    public String weightWithUnit; // Gewicht zusammen mit der Einheit (Kg)
    public boolean selected; // Flag, das angibt, ob das Element ausgewählt ist oder nicht

    /**
     * Konstruktor zur Erstellung eines neuen Gewichtseintrags.
     *
     * @param id     Die eindeutige ID des Gewichtseintrags.
     * @param date   Das Datum des Gewichtseintrags.
     * @param weight Das Gewicht in Kilogramm.
     */
    public WeightEntryItem(int id, String date, float weight) {
        this.id = id;
        this.date = date;
        this.weight = weight;
        this.weightWithUnit = weight + " Kg"; // Erstellt einen String mit dem Gewicht und der Einheit
        this.selected = false;  // Setzt das Auswahlattribut standardmäßig auf false.

    }
}

