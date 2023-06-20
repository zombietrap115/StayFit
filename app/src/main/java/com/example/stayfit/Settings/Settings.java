package com.example.stayfit.Settings;

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

import com.example.stayfit.MainActivity;
import com.example.stayfit.R;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;

/**
 * Diese Klasse stellt eine Einstellungsaktivität bereit, in der der Benutzer verschiedene
 * Einstellungen vornehmen kann, einschließlich der Festlegung einer Alarmzeit,
 * der Eingabe von Körpergröße, Geschlecht, Alter und Schrittziel.
 */
public class Settings extends AppCompatActivity {

    // UI-Komponenten
    TextView timeEdit;
    EditText heightEdit;
    Button save;
    EditText stepGoalEdit;
    RadioGroup radioGroup;
    RadioButton radioButtonMale;
    RadioButton radioButtonFemale;
    NumberPicker numberPicker;

    // Konstanten für die Schlüssel, die in den SharedPreferences verwendet werden
    private final String KEY = "timetextValue";
    private final String KEY1 = "heighttextValue";
    private final String KEY2 = "genderValue";
    private final String KEY3 = "ageValue";
    private final String KEY4 = "stepGoalValue";

    // Weitere Klassenvariablen
    private MaterialTimePicker picker;
    private SharedPreferences sharedPref;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    /**
     * Wird aufgerufen, wenn die Aktivität erstellt wird.
     *
     * @param savedInstanceState Enthält den Zustand der Aktivität, falls sie zuvor beendet wurde.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        createNotificationChannel();

        // Initialisieren der UI-Komponenten
        timeEdit = findViewById(R.id.editTextTime);
        heightEdit = findViewById(R.id.editTextHeight);
        save = findViewById(R.id.save_Button);
        radioGroup = findViewById(R.id.radioGroup);
        radioButtonMale = findViewById(R.id.radioButtonMale);
        radioButtonFemale = findViewById(R.id.radioButtonFemale);
        numberPicker = findViewById(R.id.number_picker);
        stepGoalEdit = findViewById(R.id.editTextStepGoal);
        calendar = Calendar.getInstance();

        // Zugriff auf die SharedPreferences
        sharedPref = getSharedPreferences("MyApp", MODE_PRIVATE);

        // Setzen der UI-Elemente mit gespeicherten Werten
        timeEdit.setText(getTime());
        heightEdit.setText(getHeight());
        stepGoalEdit.setText(getStepGoal());

        // Setzen des ausgewählten Geschlechts basierend auf den gespeicherten Werten
        String gender = getGender();
        if (gender.equals("Male")) {
            radioButtonMale.setChecked(true);
        } else if (gender.equals("Female")) {
            radioButtonFemale.setChecked(true);
        }

        // Konfigurieren des NumberPickers
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(100);
        numberPicker.setValue(getNumberPickerValue());

        // Zeigen des TimePicker-Dialogs, wenn das Zeitbearbeitungsfeld angeklickt wird
        timeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker();
            }
        });

        // Speichern der Einstellungen, wenn der Speichern-Button angeklickt wird
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String time = timeEdit.getText().toString();
                String height = heightEdit.getText().toString();
                String selectedGender = getSelectedGender();
                String goal = stepGoalEdit.getText().toString();


                // Überprüfen, ob alle Felder ausgefüllt sind, bevor gespeichert wird
                if (!time.isEmpty() && !height.isEmpty() && !selectedGender.isEmpty() && !goal.isEmpty()) {
                    // Werte speichern
                    saveFromTimeText(time);
                    saveFromHeightText(height);
                    saveGender(selectedGender);
                    saveNumberPickerValue(numberPicker.getValue());
                    saveStepGoal(goal);
                    setAlarm();
                    Toast.makeText(Settings.this, "Saved", Toast.LENGTH_SHORT).show();

                    // Zurück zur Hauptaktivität
                    Intent intent = new Intent(Settings.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(Settings.this, "Please fill everything out", Toast.LENGTH_SHORT).show();
                }
            }

            /**
             * Speichert das Schritteziel
             *
             * @param stepGoal Ziel als String
             */
            private void saveStepGoal(String stepGoal) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(KEY4, stepGoal);
                editor.apply();
            }
        });


        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

            }
        });
    }

    /**
     * Storniert den Alarm.
     */
    private void cancelAlarm() {
        Intent intent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        if (alarmManager == null) {
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        }

        alarmManager.cancel(pendingIntent);
    }

    /**
     * Setzt einen wiederkehrenden Alarm.
     */
    private void setAlarm() {
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        // Alarm täglich zur eingestellten Zeit setzen
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    /**
     * Zeigt einen TimePicker-Dialog an, um die Alarmzeit auszuwählen.
     */
    private void showTimePicker() {
        picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Alarm Time")
                .build();

        picker.show(getSupportFragmentManager(), "weightinput");

        picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeEdit.setText(String.format("%02d", (picker.getHour())) + ":" + String.format("%02d", picker.getMinute()));

                // Kalenderobjekt mit der ausgewählten Zeit aktualisieren
                calendar.set(Calendar.HOUR_OF_DAY, picker.getHour());
                calendar.set(Calendar.MINUTE, picker.getMinute());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
            }
        });
    }

    /**
     * Gibt die gespeicherte Zeit als String zurück.
     *
     * @return gespeicherte Zeit
     */
    private String getTime() {
        String savedValue = sharedPref.getString(KEY, "");
        return savedValue;
    }

    /**
     * Gibt das gespeicherte Schrittziel zurück.
     *
     * @return Schrittziel
     */
    private String getStepGoal() {
        return sharedPref.getString(KEY4, "");
    }

    /**
     * Gibt die gespeicherte Höhe zurück.
     *
     * @return gespeicherte Höhe
     */
    public String getHeight() {
        String savedValue = sharedPref.getString(KEY1, "");
        return savedValue;
    }

    /**
     * Speichert die Zeit.
     *
     * @param text Zeit als String
     */
    private void saveFromTimeText(String text) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY, text);
        editor.apply();
    }

    /**
     * Speichert die Höhe.
     *
     * @param text Höhe als String
     */
    private void saveFromHeightText(String text) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY1, text);
        editor.apply();
    }

    /**
     * Speichert den Wert des NumberPickers.
     *
     * @param value Wert des NumberPickers
     */
    private void saveNumberPickerValue(int value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(KEY3, value);
        editor.apply();
    }

    /**
     * Gibt den gespeicherten Wert des NumberPickers zurück.
     *
     * @return gespeicherter Wert des NumberPickers
     */
    public int getNumberPickerValue() {
        int savedValue = sharedPref.getInt(KEY3, 1);
        return savedValue;
    }

    /**
     * Gibt das gespeicherte Geschlecht zurück.
     *
     * @return gespeichertes Geschlecht
     */
    private String getGender() {
        return sharedPref.getString(KEY2, "");
    }

    /**
     * Speichert das Geschlecht.
     *
     * @param gender Geschlecht als String
     */
    private void saveGender(String gender) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY2, gender);
        editor.apply();
    }

    /**
     * Gibt das ausgewählte Geschlecht zurück.
     *
     * @return ausgewähltes Geschlecht
     */
    private String getSelectedGender() {
        int selectedId = radioGroup.getCheckedRadioButtonId();

        if (selectedId == R.id.radioButtonMale) {
            return "Male";
        } else if (selectedId == R.id.radioButtonFemale) {
            return "Female";
        }

        return "";
    }

    /**
     * Erstellt einen Benachrichtigungskanal für die Benachrichtigungen.
     */
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
