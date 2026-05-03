package com.fa.employeemanager;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.fa.employeemanager.databinding.FragmentStatistiqueBinding;
import com.fa.employeemanager.model.Employe;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class StatistiqueFragment extends Fragment {

    private FragmentStatistiqueBinding binding;
    private EmployeViewModel viewModel;

    public StatistiqueFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentStatistiqueBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(EmployeViewModel.class);

        // cacher FAB
        ((MainActivity) requireActivity()).setFabVisibility(false);

        viewModel.getEmployes().observe(getViewLifecycleOwner(), state -> {

            switch (state.status) {

                case SUCCESS:
                    if (state.data != null) {
                        afficherHistogramme(state.data);
                        afficherCamembert(state.data);
                    }
                    break;

                case EMPTY:
                case ERROR:
                case LOADING:
                    break;
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (menu.findItem(R.id.histogramme) != null) {
            menu.findItem(R.id.histogramme).setVisible(false);
        }
    }

    private void afficherHistogramme(List<Employe> employes) {

        ArrayList<BarEntry> entries = new ArrayList<>();

        int index = 0;
        for (Employe e : employes) {
            entries.add(new BarEntry(index++, (float) e.getSalaire()));
        }

        BarDataSet dataSet = new BarDataSet(entries, getString(R.string.salaires));
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        BarData data = new BarData(dataSet);

        binding.barChart.setData(data);

        int textColor = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textColor = requireContext().getColor(R.color.chart_label);
        }

        binding.barChart.getXAxis().setTextColor(textColor);
        binding.barChart.getAxisLeft().setTextColor(textColor);
        binding.barChart.getAxisRight().setTextColor(textColor);
        binding.barChart.getLegend().setTextColor(textColor);
        binding.barChart.getDescription().setTextColor(textColor);

        binding.barChart.animateY(1000);
        binding.barChart.invalidate();
    }

    private void afficherCamembert(List<Employe> employes) {

        int faible = 0;
        int moyen = 0;
        int eleve = 0;

        for (Employe e : employes) {
            float salaire = (float) e.getSalaire();

            if (salaire < 1000) {
                faible++;
            } else if (salaire < 3000) {
                moyen++;
            } else {
                eleve++;
            }
        }

        ArrayList<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(faible, getString(R.string.faible)));
        entries.add(new PieEntry(moyen, getString(R.string.moyen)));
        entries.add(new PieEntry(eleve, getString(R.string.eleve)));

        PieDataSet dataSet = new PieDataSet(entries, getString(R.string.repartition_salaire));

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.BLUE);
        colors.add(Color.GREEN);

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);

        binding.pieChart.setData(data);

        int textColor = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textColor = requireContext().getColor(R.color.chart_label);
        }

        binding.pieChart.setEntryLabelColor(textColor);
        binding.pieChart.getLegend().setTextColor(textColor);
        binding.pieChart.getDescription().setTextColor(textColor);

        binding.pieChart.animateY(1000);
        binding.pieChart.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ((MainActivity) requireActivity()).setFabVisibility(true);

        binding = null;
    }
}