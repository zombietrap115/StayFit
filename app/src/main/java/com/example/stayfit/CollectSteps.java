package com.example.stayfit;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stayfit.R;

public class CollectSteps extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager = null;
    private Sensor stepSensor;
    private int totalSteps = 0;
    private ProgressBar progressBar;
    private TextView steps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collect_steps);

        progressBar = findViewById(R.id.progressBar);
        steps = findViewById(R.id.steps);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        stepSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
    }

    protected void onResume(){
        super.onResume();

        if(stepSensor == null){
            Toast.makeText(this, "This device has no sensor", Toast.LENGTH_SHORT).show();
        }
        else {
            mSensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    protected void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR){
            totalSteps++;
            steps.setText(String.valueOf(totalSteps));
            progressBar.setProgress(totalSteps);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}

