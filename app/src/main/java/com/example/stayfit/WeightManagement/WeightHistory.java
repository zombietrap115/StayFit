package com.example.stayfit.WeightManagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.graphics.Color;
import android.os.Bundle;

import com.example.stayfit.R;
import com.example.stayfit.WeightManagement.Database.AppDatabase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Diese Klasse repräsentiert die Activity zur Anzeige der Gewichtshistorie des Benutzers in einem Liniendiagramm.
 */
public class WeightHistory extends AppCompatActivity {

    AppDatabase db;
    LineChart lineChart;
    ArrayList<Entry> lineEntries;

    /**
     * Wird aufgerufen, wenn die Activity erstellt wird.
     *
     * @param savedInstanceState Eine Referenz zum Bundle-Objekt.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_history);

        // Datenbankinstanz erstellen
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "database-name").build();

        // Liniendiagramm aus der Layout-Ressource abrufen
        lineChart = findViewById(R.id.line_chart);

        // Die Einträge für das Diagramm abrufen
        getEntries();

    }

    /**
     * Ruft die Gewichtseinträge aus der Datenbank ab und fügt sie dem Liniendiagramm hinzu.
     */
    private void getEntries() {
        lineEntries = new ArrayList<>();

        // Durchaufen der Gewichtseinträge in der Datenbank
        db.weightEntryDao().getAll().observe(this, weightEntries -> {
            lineEntries.clear();
            for (AppDatabase.WeightEntry entry : weightEntries) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    // Datum von String in Date-Objekt konvertieren
                    Date date = dateFormat.parse(entry.date);
                    float dateValue = date.getTime();
                    // Gewichtseintrag zum Diagramm hinzufügen
                    lineEntries.add(new Entry(dateValue, entry.weight));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            // Diagramm konfigurieren
            setupLineChart();
        });
    }

    /**
     * Konfiguriert das Liniendiagramm mit den abgerufenen Gewichtseinträgen.
     */
    private void setupLineChart() {
        // Ein LineDataSet wird erstellt, um die Gewichtseinträge im Diagramm darzustellen.
        // "Weight" ist das Label für die Datenlinie.
        LineDataSet lineDataSet = new LineDataSet(lineEntries, "Weight");

        // Farbe der Linie im Diagramm auf Blau setzen.
        lineDataSet.setColor(Color.BLUE);

        // Farbe der Kreise an den Datenpunkten auf Blau setzen.
        lineDataSet.setCircleColor(Color.BLUE);

        // Linienbreite auf 2 setzen.
        lineDataSet.setLineWidth(2f);

        // Radius der Kreise an den Datenpunkten auf 4 setzen.
        lineDataSet.setCircleRadius(4f);

        // Deaktiviert das Anzeigen von Werten an den Datenpunkten.
        lineDataSet.setDrawValues(false);

        // Setzt den Modus der Linie im Diagramm auf linear.
        lineDataSet.setMode(LineDataSet.Mode.LINEAR);

        // Erstellt LineData mit dem zuvor konfigurierten DataSet.
        LineData lineData = new LineData(lineDataSet);

        // Fügt die LineData dem Liniendiagramm hinzu.
        lineChart.setData(lineData);

        // Deaktiviert die Diagrammbeschreibung.
        lineChart.getDescription().setEnabled(false);

        // Deaktiviert die Legende des Diagramms.
        lineChart.getLegend().setEnabled(false);

        // Setzt die Position der X-Achse auf die untere Seite des Diagramms.
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        // Setzt einen benutzerdefinierten WertFormatter für die X-Achse,
        // der die Millisekunden in ein lesbares Datum umwandelt.
        lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM");

            @Override
            public String getFormattedValue(float value) {
                // Konvertiere den X-Wert (Zeitstempel) in ein lesbares Datum.
                long millis = (long) value;
                return dateFormat.format(new Date(millis));
            }
        });

        // Deaktiviert die rechte Y-Achse des Diagramms.
        lineChart.getAxisRight().setEnabled(false);

        // Fügt eine Y-Achsen-Animation hinzu, wenn das Diagramm angezeigt wird.
        lineChart.animateY(500);

        // Zeichnet das Diagramm, damit alle Änderungen sichtbar sind.
        lineChart.invalidate();
    }

}
