package com.example.stayfit.WeightManagement.Database;

import androidx.room.ColumnInfo;
import androidx.room.Database;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.RoomDatabase;

@Database(entities = {AppDatabase.WeightEntry.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract WeightEntryDao weightEntryDao();

    @Entity
    public static class WeightEntry {
        @PrimaryKey(autoGenerate = true)
        public int id;

        @ColumnInfo(name = "date")
        public String date;

        @ColumnInfo(name = "weight")
        public float weight;

        public WeightEntry(int id,String date, float weight) {
            this.id = id;
            this.date = date;
            this.weight = weight;
        }
    }
}
