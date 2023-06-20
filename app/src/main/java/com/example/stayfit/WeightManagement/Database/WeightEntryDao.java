package com.example.stayfit.WeightManagement.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import androidx.lifecycle.LiveData;

import com.example.stayfit.WeightManagement.Database.AppDatabase;

/**
 * Data Access Object (DAO) Interface für die Interaktion mit Gewichtseinträgen in der Datenbank.
 * Definiert Methoden zum Abrufen, Einfügen und Löschen von Gewichtseinträgen.
 */
@Dao
public interface WeightEntryDao {

    /**
     * Gibt eine Liste aller Gewichtseinträge in der Datenbank zurück.
     *
     * @return LiveData-Objekt, das eine Liste aller Gewichtseinträge enthält.
     */
    @Query("SELECT * FROM WeightEntry")
    LiveData<List<AppDatabase.WeightEntry>> getAll();

    /**
     * Fügt mehrere Gewichtseinträge in die Datenbank ein.
     *
     * @param weightEntries Ein Array von Gewichtseinträgen, die eingefügt werden sollen.
     */
    @Insert
    void insertAll(AppDatabase.WeightEntry... weightEntries);

    /**
     * Löscht einen bestimmten Gewichtseintrag aus der Datenbank.
     *
     * @param weightEntry Der Gewichtseintrag, der gelöscht werden soll.
     */
    @Delete
    void delete(AppDatabase.WeightEntry weightEntry);

    /**
     * Gibt den neuesten Gewichtseintrag in der Datenbank zurück, basierend auf der ID.
     *
     * @return LiveData-Objekt, das den neuesten Gewichtseintrag enthält.
     */
    @Query("SELECT * FROM WeightEntry ORDER BY id DESC LIMIT 1")
    LiveData<AppDatabase.WeightEntry> getLatestEntry();

}




