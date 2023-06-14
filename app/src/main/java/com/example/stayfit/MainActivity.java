package com.example.stayfit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.stayfit.WeightManagement.Database.AppDatabase;
import com.example.stayfit.WeightManagement.Database.WeightEntryAdapter;
import com.example.stayfit.WeightManagement.Database.WeightEntryItem;
import com.example.stayfit.WeightManagement.WeightHistory;
import com.example.stayfit.WeightManagement.WeightInputActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    AppDatabase db;

    private WeightEntryAdapter adapter;

    private List<WeightEntryItem> weightEntryItems = new ArrayList<>();

    Button buttonweight;

    Button buttonSettings;

    Button weightHistory;

    TextView latestWeightView;

    TextView caloriesBurned;

    private SharedPreferences sharedPref;

    TextView bmiText;

    Button collectSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // SharedPreferences-Objekt abrufen
        sharedPref = getSharedPreferences("MyApp", MODE_PRIVATE);

        // Überprüfen, ob es das erste Mal ist, dass die App gestartet wird
        boolean isFirstRun = sharedPref.getBoolean("IsFirstRun", true);

        if (isFirstRun) {
            // Wenn es das erste Mal ist, starten Sie die bestimmte Activity

            // SharedPreferences-Editor zum Ändern der SharedPreferences-Daten abrufen
            SharedPreferences.Editor editor = sharedPref.edit();

            // "IsFirstRun" auf false setzen
            editor.putBoolean("IsFirstRun", false);

            // Änderungen speichern
            editor.apply();

            // Starten Sie die bestimmte Activity
            startActivity(new Intent(MainActivity.this, Settings.class));

            // Beenden Sie die aktuelle Activity
            finish();
        }

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "database-name").build();

        buttonweight = findViewById(R.id.button_weight);

        buttonSettings = findViewById(R.id.button_einstellungen);

        caloriesBurned = findViewById(R.id.caloriesTextView);

        bmiText = findViewById(R.id.textViewBMI);

        collectSteps = findViewById(R.id.collectSteps);


        buttonweight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WeightInputActivity.class);
                startActivity(intent);
            }
        });

        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Settings.class);
                startActivity(intent);
            }
        });

        collectSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CollectSteps.class);
                startActivity(intent);
            }
        });

        weightHistory = findViewById(R.id.weightHistory);

        weightHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WeightHistory.class);
                startActivity(intent);
            }
        });


    }



    @Override
    protected void onResume() {
        super.onResume();


        latestWeightView = findViewById(R.id.weightTextView);

        db.weightEntryDao().getLatestEntry().observe(this, latestEntry -> {
            if (latestEntry != null) {
                latestWeightView.setText(String.valueOf("Latest weight: " + latestEntry.weight));

                String heightString = sharedPref.getString("heighttextValue", "");
                double height = heightString.isEmpty() ? 0 : Integer.parseInt(heightString);
                String genderString = sharedPref.getString("genderValue", "");
                int age = sharedPref.getInt("ageValue",1);

                if(genderString=="Female") {
                    double calories = ((655.1 + (9.6 * latestEntry.weight) + (1.8 * height) - (4.7 * age)));//Frau
                    caloriesBurned.setText(String.valueOf("Calories burned at rest: "+String.format("%.0f", calories) + " kcals"));
                } else {
                    double calories = ((66 + (13.7 * latestEntry.weight) + (5 * height) - (6.8 * age)));//Mann
                    caloriesBurned.setText(String.valueOf("Calories burned at rest: " + String.format("%.0f", calories) + " kcals"));
                }

                double heightM = height/100;

                double bmi = latestEntry.weight/ Math.pow(heightM,2.0);
                bmiText.setText(String.valueOf("BMI: " + String.format("%.1f", bmi)));

                //Körpergewicht (in kg) geteilt durch Körpergröße (in m) zum Quadrat.

            }
        });
        //655,1 + (9,6 x Körpergewicht in kg) + (1,8 x Körpergröße in cm) – (4,7 x Alter in Jahren). (Frau)
        //66 + (13.7 x Gewicht in Kilogramm) + (5 x Körpergröße in cm) – (6.8 x Alter in Jahren) (Mann)



    }
    }

