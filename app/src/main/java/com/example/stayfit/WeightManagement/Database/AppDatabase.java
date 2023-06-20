package com.example.stayfit.WeightManagement.Database;

import androidx.room.ColumnInfo;
import androidx.room.Database;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.RoomDatabase;

/**
 * Definiert die Datenbank für die Gewichtsverwaltungsfunktionen der App.
 * Enthält eine Entität für Gewichtseinträge und eine DAO (Data Access Object)
 * zum Interagieren mit den Gewichtseinträgen.
 */
@Database(entities = {AppDatabase.WeightEntry.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    /**
     * Abstrakte Methode zur Abrufen des Data Access Objects (DAO) für Gewichtseinträge.
     *
     * @return Das WeightEntryDao zum Interagieren mit Gewichtseinträgen.
     */
    public abstract WeightEntryDao weightEntryDao();

    /**
     * Innere Klasse, die die Entität für einen Gewichtseintrag in der Datenbank repräsentiert.
     */
    @Entity
    public static class WeightEntry {

        // Eindeutige ID des Gewichtseintrags (Primärschlüssel)
        @PrimaryKey(autoGenerate = true)
        public int id;

        // Datum des Gewichtseintrags im Format yyyy-MM-dd
        @ColumnInfo(name = "date")
        public String date;

        // Gewicht in Kilogramm
        @ColumnInfo(name = "weight")
        public float weight;

        /**
         * Konstruktor zur Erstellung eines neuen Gewichtseintrags.
         *
         * @param id     Die eindeutige ID des Gewichtseintrags.
         * @param date   Das Datum des Gewichtseintrags.
         * @param weight Das Gewicht in Kilogramm.
         */
        public WeightEntry(int id, String date, float weight) {
            this.id = id;
            this.date = date;
            this.weight = weight;
        }
    }
}

