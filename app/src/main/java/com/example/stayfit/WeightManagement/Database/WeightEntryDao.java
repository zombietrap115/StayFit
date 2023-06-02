package com.example.stayfit.WeightManagement.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import androidx.lifecycle.LiveData;

import com.example.stayfit.WeightManagement.Database.AppDatabase;

@Dao
public interface WeightEntryDao {
    @Query("SELECT * FROM WeightEntry")
    LiveData<List<AppDatabase.WeightEntry>> getAll();

    @Insert
    void insertAll(AppDatabase.WeightEntry... weightEntries);

    @Delete
    void delete(AppDatabase.WeightEntry weightEntry);

    @Query("SELECT * FROM WeightEntry ORDER BY id DESC LIMIT 1")
    LiveData<AppDatabase.WeightEntry> getLatestEntry();

}



