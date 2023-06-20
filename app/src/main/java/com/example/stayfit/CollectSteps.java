package com.example.stayfit;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Diese Klasse verwaltet die Erfassung von Schritten durch den Schrittsensor des Geräts.
 * Es ermöglicht dem Benutzer, die Anzahl der Schritte zu speichern und zurückzusetzen,
 * und zeigt den Fortschritt in einer Fortschrittsleiste an.
 */
public class CollectSteps extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor stepSensor;
    private int totalSteps = 0;
    private ProgressBar progressBar;
    private TextView steps;
    private TextView goalTextView;
    private Button saveSteps;
    private Button resetSteps;
    private final String KEY = "stepValue";
    private final String KEY_STEP_GOAL = "stepGoalValue";
    private SharedPreferences sharedPref;

    /**
     * Wird aufgerufen, wenn die Aktivität zum ersten Mal erstellt wird.
     * Hier wird das Layout initialisiert und der Schrittsensor sowie die
     * verschiedenen Schaltflächen und Ansichten konfiguriert.
     * @param savedInstanceState Wenn die Aktivität aus einem vorherigen Zustand wiederhergestellt wird,
     *                           wird dieser Parameter verwendet, um den Zustand wiederherzustellen.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collect_steps);

        // Initialisierung der Komponenten
        sharedPref = getSharedPreferences("MyApp", MODE_PRIVATE);
        saveSteps = findViewById(R.id.collectSteps);
        resetSteps = findViewById(R.id.resetSteps);
        progressBar = findViewById(R.id.progressBar);
        steps = findViewById(R.id.steps);
        goalTextView = findViewById(R.id.goalTextView);

        // Initialisierung des Schrittsensors
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        // Setzen des aktuellen Fortschritts
        progressBar.setProgress(currentProgress());
        steps.setText(Integer.toString(currentProgress())); //Muss in String umgewandelt werden

        // Button-Listener zum Speichern der Schritte
        saveSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentProgress = currentProgress() + totalSteps; // Neu gemachte Schritte werden zum gespeicherten Wert addiert
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(KEY, currentProgress); // Neuen Wert speichern
                editor.apply();
                progressBar.setProgress(currentProgress); // Fortschritt auf ProgressBar anzeigen
                totalSteps = 0; // Variable 0 setzen
                steps.setText(Integer.toString(currentProgress)); // Ausgabe des Fortschritts in TextView
            }
        });

        // Button-Listener zum Zurücksetzen der Schritte
        resetSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setProgress(0);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(KEY, 0);
                editor.apply();
                steps.setText(Integer.toString(currentProgress()));
            }
        });
    }

    /**
     * Gibt den aktuellen Fortschritt zurück, der in den SharedPreferences gespeichert ist.
     *
     * @return Den gespeicherten Fortschrittswert.
     */
    private int currentProgress() {
        return sharedPref.getInt(KEY, 1);
    }

    /**
     * Wirdaufgerufen, wenn die Aktivität in den Vordergrund tritt.
     * Hier wird der Schrittsensor registriert.
     */
    @Override
    protected void onResume() {
        super.onResume();

        // Überprüfen, ob ein Schrittsensor verfügbar ist
        if (stepSensor == null) {
            Toast.makeText(this, "This device has no sensor", Toast.LENGTH_SHORT).show();
        } else {
            mSensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        // Setzen des Schrittziels
        int stepGoal = Integer.parseInt(getStepGoal()); // Schrittziel wird von Settings Activity übernommen
        goalTextView.setText("Goal: " + stepGoal); //Text
        progressBar.setMax(stepGoal); //Anzeige
    }

    /**
     * Wird aufgerufen, wenn die Aktivität in den Hintergrund tritt.
     * Hier wird der Schrittsensor abgemeldet, um Ressourcen zu sparen.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    /**
     * Wird aufgerufen, wenn sich die Sensorwerte ändern.
     * Hier wird die Anzahl der Schritte erhöht und die Ansicht aktualisiert.
     *
     * @param event Das SensorEvent, das die Änderung enthält.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            totalSteps++;
            steps.setText(String.valueOf(currentProgress() + totalSteps)); //Addition von gespeichertem Wert und totalSteps, damit er nicht wieder von 0 beginnt
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    /**
     * Gibt das Schrittziel zurück, das in den SharedPreferences gespeichert ist.
     *
     * @return Das gespeicherte Schrittziel als String.
     */
    private String getStepGoal() {
        return sharedPref.getString(KEY_STEP_GOAL, "8000"); // 8000 ist der Standardwert, falls nichts gespeichert ist.
    }
}



