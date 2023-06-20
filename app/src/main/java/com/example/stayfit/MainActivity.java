package com.example.stayfit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.stayfit.Settings.Settings;
import com.example.stayfit.WeightManagement.Database.AppDatabase;
import com.example.stayfit.WeightManagement.WeightHistory;
import com.example.stayfit.WeightManagement.WeightInputActivity;

/**
 * Diese Klasse verwaltet die Hauptoberfläche der App und ermöglicht den Zugriff
 * auf verschiedene Funktionen wie Gewichtseingabe, Einstellungen und Schrittzählung.
 */
public class MainActivity extends AppCompatActivity {

    private AppDatabase db;
    private Button buttonweight;
    private Button buttonSettings;
    private Button weightHistory;
    private TextView latestWeightView;
    private TextView caloriesBurned;
    private SharedPreferences sharedPref;
    private TextView bmiText;
    private Button collectSteps;

    /**
     * Wird aufgerufen, wenn die Aktivität zum ersten Mal erstellt wird.
     * Hier wird das Layout initialisiert und die Datenbank sowie die
     * verschiedenen Schaltflächen und Ansichten konfiguriert.
     * @param savedInstanceState Wenn die Aktivität aus einem vorherigen Zustand wiederhergestellt wird,
     *                           wird dieser Parameter verwendet, um den Zustand wiederherzustellen.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Abrufen des SharedPreferences-Objekts, um den Zustand der App zu speichern
        sharedPref = getSharedPreferences("MyApp", MODE_PRIVATE);

        // Überprüfen, ob die App zum ersten Mal gestartet wird
        boolean isFirstRun = sharedPref.getBoolean("IsFirstRun", true);
        if (isFirstRun) {
            // Initialisierung für den ersten Start der App
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("IsFirstRun", false);
            editor.apply();
            startActivity(new Intent(MainActivity.this, Settings.class));
            finish();
        }

        // Datenbankinitialisierung
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "database-name").build();

        // Initialisierung der UI-Elemente
        buttonweight = findViewById(R.id.button_weight);
        buttonSettings = findViewById(R.id.button_einstellungen);
        caloriesBurned = findViewById(R.id.caloriesTextView);
        bmiText = findViewById(R.id.textViewBMI);
        collectSteps = findViewById(R.id.collectSteps);
        weightHistory = findViewById(R.id.weightHistory);
        latestWeightView = findViewById(R.id.weightTextView);

        // Button-Listener für die Gewichtseingabe
        buttonweight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WeightInputActivity.class);
                startActivity(intent);
            }
        });

        // Button-Listener für die Einstellungen
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Settings.class);
                startActivity(intent);
            }
        });

        // Button-Listener für Schrittzähler
        collectSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CollectSteps.class);
                startActivity(intent);
            }
        });

        // Button-Listener für Gewichtshistorie
        weightHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WeightHistory.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Wird aufgerufen, wenn die Aktivität in den Vordergrund einer Aufgabenstapel tritt.
     * Hier wird das Gewicht des Benutzers aktualisiert und die Anzeige für verbrannte Kalorien berechnet.
     */
    @Override
    protected void onResume() {
        super.onResume();

        // Abrufen des neuesten Gewichtseintrags aus der Datenbank
        db.weightEntryDao().getLatestEntry().observe(this, latestEntry -> {
            //Überprüft ob der letzte Eintrag nicht null ist
            if (latestEntry != null) {
                // Anzeige des letzten Gewichts
                latestWeightView.setText(String.valueOf("Latest weight: " + latestEntry.weight));

                // Abrufen von Höhe, Geschlecht und Alter aus den SharedPreferences
                String heightString = sharedPref.getString("heighttextValue", "");
                double height = heightString.isEmpty() ? 0 : Integer.parseInt(heightString);
                String genderString = sharedPref.getString("genderValue", "");
                int age = sharedPref.getInt("ageValue", 1);

                // Berechnung der verbrannten Kalorien basierend auf Geschlecht, Gewicht, Größe und Alter
                double calories;
                if ("Female".equals(genderString)) {
                    // Berechnung für Frauen
                    calories = (655.1 + (9.6 * latestEntry.weight) + (1.8 * height) - (4.7 * age));
                } else {
                    // Berechnung für Männer
                    calories = (66 + (13.7 * latestEntry.weight) + (5 * height) - (6.8 * age));
                }
                // Anzeige der verbrannten Kalorien ohne Kommastellen
                caloriesBurned.setText(String.format("Calories burned at rest: %.0f kcals", calories));

                // Berechnung des BMI (Body Mass Index) und Ausgabe mit einer Kommastelle
                double heightInMeters = height / 100;
                double bmi = latestEntry.weight / Math.pow(heightInMeters, 2.0);
                bmiText.setText(String.format("BMI: %.1f", bmi));
            }
        });
    }
}



