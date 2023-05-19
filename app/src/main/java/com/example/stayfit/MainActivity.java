package com.example.stayfit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.stayfit.R;
import com.example.stayfit.WeightInputActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonweight = findViewById(R.id.button_weight);
        buttonweight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hier starten Sie die andere Activity
                Intent intent = new Intent(MainActivity.this, WeightInputActivity.class);
                startActivity(intent);
            }
        });
    }


}