package com.example.stayfit;

import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;


public class Settings extends AppCompatActivity {

    EditText timeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        timeText = findViewById(R.id.editTextTime);


    }
}
