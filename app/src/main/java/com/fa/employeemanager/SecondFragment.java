package com.fa.employeemanager;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.fa.employeemanager.api.ApiClient;
import com.fa.employeemanager.api.ApiConfig;
import com.fa.employeemanager.api.ApiService;
import com.fa.employeemanager.databinding.FragmentSecondBinding;
import com.fa.employeemanager.model.Employe;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 🔥 Cache le FAB
        ((MainActivity) requireActivity()).setFabVisibility(false);

        NavController navController = Navigation.findNavController(view);

        binding.btnSave.setOnClickListener(v -> {

            String nom = binding.editNom.getText().toString();
            String salaire = binding.editSalaire.getText().toString();

            if (TextUtils.isEmpty(nom) || TextUtils.isEmpty(salaire)) {
                Toast.makeText(getContext(),
                        "Remplir tous les champs",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            int salaireValue;
            try {
                salaireValue = Integer.parseInt(salaire);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(),
                        "Salaire invalide",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            ApiService apiService = ApiClient.getClient().create(ApiService.class);

            Employe newEmploye = new Employe(0, nom, salaireValue);

            // 🔥 Désactiver bouton (évite double clic)
            binding.btnSave.setEnabled(false);

            apiService.addEmploye(ApiConfig.BASE_URL, newEmploye)
                    .enqueue(new Callback<Employe>() {

                        @Override
                        public void onResponse(Call<Employe> call, Response<Employe> response) {

                            binding.btnSave.setEnabled(true);

                            if (response.isSuccessful()) {

                                Toast.makeText(getContext(),
                                        "Employé ajouté",
                                        Toast.LENGTH_SHORT).show();

                                // ✅ OPTION 1 : revenir simplement
                                navController.popBackStack();

                                // ✅ OPTION 2 (si navigation spécifique)
                                // navController.navigate(R.id.action_SecondFragment_to_FirstFragment);
                            } else {
                                Toast.makeText(getContext(),
                                        "Erreur serveur",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Employe> call, Throwable t) {

                            binding.btnSave.setEnabled(true);

                            Toast.makeText(getContext(),
                                    "Erreur: " + t.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (menu.findItem(R.id.histogramme) != null) {
            menu.findItem(R.id.histogramme).setVisible(false);
        }

        menu.clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // 🔥 Réaffiche le FAB
        ((MainActivity) requireActivity()).setFabVisibility(true);

        binding = null;
    }
}