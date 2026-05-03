package com.fa.employeemanager;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;

import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class StatistiqueActivity extends AppCompatActivity {

    BarChart barChart;
    PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistique);

        barChart = findViewById(R.id.barChart);
        pieChart = findViewById(R.id.pieChart);

        afficherHistogramme();
        afficherCamembert();
    }

    private void afficherHistogramme() {

        ArrayList<BarEntry> entries = new ArrayList<>();

        entries.add(new BarEntry(1, 800));
        entries.add(new BarEntry(2, 2000));
        entries.add(new BarEntry(3, 6000));

        BarDataSet dataSet = new BarDataSet(entries, "Salaires");

        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        BarData data = new BarData(dataSet);

        barChart.setData(data);
        barChart.animateY(1000);
        barChart.invalidate();
    }

    private void afficherCamembert() {

        int mediocre = 1;
        int moyen = 1;
        int grand = 1;

        ArrayList<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(mediocre, "Médiocre"));
        entries.add(new PieEntry(moyen, "Moyen"));
        entries.add(new PieEntry(grand, "Grand"));

        PieDataSet dataSet = new PieDataSet(entries, "Observations");

        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData data = new PieData(dataSet);

        pieChart.setData(data);
        pieChart.animateY(1000);
        pieChart.invalidate();
    }
}