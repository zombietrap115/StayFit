package com.example.stayfit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stayfit.WeightManagement.WeightInputActivity;


public class Settings extends AppCompatActivity {

    EditText timeEdit;
    EditText heightEdit;
    Button save;

    private final String KEY = "timetextValue";

    private final String KEY1 = "heighttextValue";

    private final String KEY2 = "genderValue";

    RadioGroup radioGroup;
    RadioButton radioButtonMale;
    RadioButton radioButtonFemale;
    NumberPicker numberPicker;

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);


        timeEdit = findViewById(R.id.editTextTime);
        heightEdit = findViewById(R.id.editTextHeight);
        save = findViewById(R.id.save_Button);
        radioGroup = findViewById(R.id.radioGroup);
        radioButtonMale = findViewById(R.id.radioButtonMale);
        radioButtonFemale = findViewById(R.id.radioButtonFemale);
        numberPicker = findViewById(R.id.number_picker);

        sharedPref = getSharedPreferences("MyApp", MODE_PRIVATE);


        timeEdit.setText(getTime());
        heightEdit.setText(getHeight());

        String gender = getGender();
        if(gender.equals("Male")) {
            radioButtonMale.setChecked(true);
        } else if(gender.equals("Female")) {
            radioButtonFemale.setChecked(true);
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String time = timeEdit.getText().toString();
                String height = heightEdit.getText().toString();
                String selectedGender = getSelectedGender();


                if(!time.isEmpty()&&!height.isEmpty()&&!selectedGender.isEmpty()) {
                    saveFromTimeText(timeEdit.getText().toString());
                    saveFromHeightText(heightEdit.getText().toString());
                    saveGender(getSelectedGender());
                    Toast.makeText(Settings.this, "Saved", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Settings.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(Settings.this, "Please fill everything out", Toast.LENGTH_SHORT).show();
                }
            }
        });

        numberPicker.setMinValue(1); // specify the minimum number user can select

// Specify the maximum value/number of NumberPicker
        numberPicker.setMaxValue(100); // specify the maximum number user can select

// Set a value change listener for NumberPicker
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                // Do something here on the value change like storing the new value
            }
        });


    }

    private String getTime() {
        String savedValue = sharedPref.getString(KEY, ""); //the 2 argument return default value

        return savedValue;
    }

    public String getHeight() {
        String savedValue = sharedPref.getString(KEY1, ""); //the 2 argument return default value

        return savedValue;
    }

    private void saveFromTimeText(String text) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY, text);
        editor.apply();
    }

    private void saveFromHeightText(String text) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY1, text);
        editor.apply();
    }

    private String getGender() {
        return sharedPref.getString(KEY2, "");
    }

    private void saveGender(String gender) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY2, gender);
        editor.apply();
    }


    private String getSelectedGender() {
        int selectedId = radioGroup.getCheckedRadioButtonId();

        if (selectedId == R.id.radioButtonMale) {
            return "Male";
        } else if (selectedId == R.id.radioButtonFemale) {
            return "Female";
        }

        return "";
    }



}
