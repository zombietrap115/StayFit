package com.example.stayfit;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stayfit.WeightManagement.WeightInputActivity;
import com.example.stayfit.databinding.ActivityMainBinding;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;


public class Settings extends AppCompatActivity {

    TextView timeEdit;
    EditText heightEdit;
    Button save;

    private final String KEY = "timetextValue";

    private final String KEY1 = "heighttextValue";

    private final String KEY2 = "genderValue";

    private final String KEY3 = "ageValue";

    RadioGroup radioGroup;
    RadioButton radioButtonMale;
    RadioButton radioButtonFemale;
    NumberPicker numberPicker;

    private MaterialTimePicker picker;

    private SharedPreferences sharedPref;

    private Calendar calendar;

    private AlarmManager alarmManager;

    private PendingIntent pendingIntent;
    

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
        calendar = Calendar.getInstance();

        sharedPref = getSharedPreferences("MyApp", MODE_PRIVATE);

        createNotificationChannel();

        timeEdit.setText(getTime());
        heightEdit.setText(getHeight());

        String gender = getGender();
        if (gender.equals("Male")) {
            radioButtonMale.setChecked(true);
        } else if (gender.equals("Female")) {
            radioButtonFemale.setChecked(true);
        }
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(100);
        numberPicker.setValue(getNumberPickerValue());




        timeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showTimePicker();

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String time = timeEdit.getText().toString();
                String height = heightEdit.getText().toString();
                String selectedGender = getSelectedGender();



                if (!time.isEmpty() && !height.isEmpty() && !selectedGender.isEmpty()) {
                    cancelAlarm();
                    saveFromTimeText(timeEdit.getText().toString());
                    saveFromHeightText(heightEdit.getText().toString());
                    saveGender(getSelectedGender());
                    saveNumberPickerValue(numberPicker.getValue());
                    setAlarm();
                    Toast.makeText(Settings.this, "Saved", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Settings.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(Settings.this, "Please fill everything out", Toast.LENGTH_SHORT).show();
                }
            }
        });

        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // Do something here on the value change like storing the new value
            }
        });


    }

    private void cancelAlarm() {

        Intent intent = new Intent(this,AlarmReceiver.class);

        pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);

        if (alarmManager == null){

            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        }

        alarmManager.cancel(pendingIntent);
    }

    private void setAlarm() {

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this,AlarmReceiver.class);

        pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

    }

    private void showTimePicker() {

        picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Alarm Time")
                .build();

        picker.show(getSupportFragmentManager(),"weightinput");

        picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                timeEdit.setText(
                            String.format("%02d",(picker.getHour()))+":"+String.format("%02d",picker.getMinute()));


                calendar.set(Calendar.HOUR_OF_DAY,picker.getHour());
                calendar.set(Calendar.MINUTE,picker.getMinute());
                calendar.set(Calendar.SECOND,0);
                calendar.set(Calendar.MILLISECOND,0);

                if (calendar.before(Calendar.getInstance())) {
                    calendar.add(Calendar.DATE, 1);
                } //ka


            }
        });

    }


    private String getTime() {
        String savedValue = sharedPref.getString(KEY, "");

        return savedValue;
    }

    public String getHeight() {
        String savedValue = sharedPref.getString(KEY1, "");

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

    private void saveNumberPickerValue(int value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(KEY3, value);
        editor.apply();
    }

    public int getNumberPickerValue() {
        int savedValue = sharedPref.getInt(KEY3, 1);
        return savedValue;
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

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "WeightReminderChannel";
            String description = "Channel For Weight Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("weightinput", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }

    }
}
