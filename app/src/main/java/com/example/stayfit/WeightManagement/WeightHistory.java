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

public class WeightHistory extends AppCompatActivity {

    AppDatabase db;
    LineChart lineChart;

    ArrayList<Entry> lineEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_history);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "database-name").build();

        lineChart = findViewById(R.id.line_chart);
        getEntries();

    }

    private void getEntries() {
        lineEntries = new ArrayList<>();
        db.weightEntryDao().getAll().observe(this, weightEntries -> {
            lineEntries.clear();
            for (AppDatabase.WeightEntry entry : weightEntries) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date = dateFormat.parse(entry.date);
                    float dateValue = date.getTime();
                    lineEntries.add(new Entry(dateValue, entry.weight));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            setupLineChart();
        });
    }

    private void setupLineChart() {
        LineDataSet lineDataSet = new LineDataSet(lineEntries, "Weight");
        lineDataSet.setColor(Color.BLUE);
        lineDataSet.setCircleColor(Color.BLUE);
        lineDataSet.setLineWidth(2f);
        lineDataSet.setCircleRadius(4f);
        lineDataSet.setDrawValues(false);
        lineDataSet.setMode(LineDataSet.Mode.LINEAR);

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM");

            @Override
            public String getFormattedValue(float value) {
                long millis = (long) value;
                return dateFormat.format(new Date(millis));
            }
        });
        lineChart.getAxisRight().setEnabled(false);
        lineChart.animateY(500);
        lineChart.invalidate();
    }

}
