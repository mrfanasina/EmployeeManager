package com.fa.employeemanager;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.fa.employeemanager.adapter.EmployeAdapter;
import com.fa.employeemanager.databinding.FragmentFirstBinding;
import com.fa.employeemanager.model.Employe;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private EmployeAdapter adapter;
    private EmployeViewModel viewModel;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    private void showState(String title, String message, int iconRes) {
        binding.stateContainer.setVisibility(View.VISIBLE);
        binding.txtStateTitle.setText(title);
        binding.txtStateMessage.setText(message);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(requireActivity())
                .get(EmployeViewModel.class);

        adapter = new EmployeAdapter();

        binding.recyclerEmployees.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerEmployees.setAdapter(adapter);

        // ✅ CONFIGURATION DU SWIPE REFRESH
        binding.swipeRefresh.setOnRefreshListener(() -> {
            viewModel.reload();
        });

        binding.swipeRefresh.setColorSchemeResources(
                R.color.blue,
                R.color.emp_grand,
                R.color.blue2,
                R.color.delete_red,
                R.color.emp_moyen
        );
        Activity activity = getActivity();
        binding.btnSettings.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.settingsFragment);
        });
        // Observer le LiveData
        viewModel.getEmployes().observe(getViewLifecycleOwner(), state -> {

            binding.progressBar.setVisibility(View.GONE);
            binding.recyclerEmployees.setVisibility(View.GONE);
            binding.stateContainer.setVisibility(View.GONE);

            // ✅ IMPORTANT : arrêter le refresh
            binding.swipeRefresh.setRefreshing(false);

            switch (state.status) {

                case LOADING:
                    if (!binding.swipeRefresh.isRefreshing()) {
                        binding.progressBar.setVisibility(View.VISIBLE);
                    }
                    break;

                case SUCCESS:
                    binding.recyclerEmployees.setVisibility(View.VISIBLE);
                    adapter.setEmployes(state.data);
                    updateStatistics(state.data);
                    break;

                case ERROR:
                    showState(
                            "Erreur",
                            state.message,
                            android.R.drawable.stat_notify_error
                    );
                    break;

                case EMPTY:
                    showState(
                            "Aucune donnée",
                            "Aucun employé trouvé",
                            android.R.drawable.ic_menu_info_details
                    );
                    break;
            }
        });
    }

    private void updateStatistics(List<Employe> list) {
        if (list == null || list.isEmpty()) {
            binding.txtTotalSalaire.setText("0 €");
            binding.txtMinSalaire.setText("0 €");
            binding.txtMaxSalaire.setText("0 €");
            return;
        }

        double total = 0;
        double min = list.get(0).getSalaire();
        double max = list.get(0).getSalaire();

        for (Employe e : list) {
            double s = e.getSalaire();
            total += s;
            if (s < min) min = s;
            if (s > max) max = s;
        }

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator(' ');
        DecimalFormat df = new DecimalFormat("#,##0", symbols);

        binding.txtTotalSalaire.setText(df.format(total) + " €");
        binding.txtMinSalaire.setText(df.format(min) + " €");
        binding.txtMaxSalaire.setText(df.format(max) + " €");
    }
    @Override
    public void onResume() {
        super.onResume();

        if (viewModel != null) {
            viewModel.reload(); // 🔥 recharge automatiquement
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}