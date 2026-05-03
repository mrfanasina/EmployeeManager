package com.fa.employeemanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.fa.employeemanager.api.ApiClient;
import com.fa.employeemanager.api.ApiConfig;
import com.fa.employeemanager.databinding.FragmentSettingsBinding;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
    private SharedPreferences preferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        preferences = requireContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) requireActivity()).setFabVisibility(false);
        // 1. Charger les valeurs sauvegardées (IP, Port, APIs)
        loadSavedSettings();

        // 2. Gestion du clic sur le bouton Sauvegarder
        binding.btnSaveSettings.setOnClickListener(v -> {
            saveAllSettings();
        });

        // 3. Gestion du changement de Thème (Instantané)
        binding.themeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int mode;
            if (checkedId == R.id.radioLight) {
                mode = AppCompatDelegate.MODE_NIGHT_NO;
            } else if (checkedId == R.id.radioDark) {
                mode = AppCompatDelegate.MODE_NIGHT_YES;
            } else {
                mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
            }

            // Appliquer et sauvegarder le thème
            AppCompatDelegate.setDefaultNightMode(mode);
            preferences.edit().putInt("theme_mode", mode).apply();
        });
    }

    private void loadSavedSettings() {
        binding.editIp.setText(preferences.getString("server_ip", ""));
        binding.editPort.setText(preferences.getString("server_port", ""));
        binding.editReadApi.setText(preferences.getString("api_read", ""));

        // Charger l'état du thème
        int savedMode = preferences.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        if (savedMode == AppCompatDelegate.MODE_NIGHT_NO) {
            binding.radioLight.setChecked(true);
        } else if (savedMode == AppCompatDelegate.MODE_NIGHT_YES) {
            binding.radioDark.setChecked(true);
        } else {
            binding.radioSystem.setChecked(true);
        }
    }

    private void saveAllSettings() {
        String ip = binding.editIp.getText().toString().trim();
        String port = binding.editPort.getText().toString().trim();

        if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(port)) {
            Toast.makeText(getContext(), "L'IP et le Port sont obligatoires", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sauvegarde de tous les champs
        preferences.edit()
                .putString("server_ip", ip)
                .putString("server_port", port)
                .putString("api_read", binding.editReadApi.getText().toString().trim())
                .apply();
        String read = binding.editReadApi.getText().toString().trim();

        ApiConfig.init(ip, port, read);

        ApiClient.reset();
        Toast.makeText(getContext(), "Paramètres enregistrés", Toast.LENGTH_SHORT).show();

        // Lancer le test de connexion
        testServerConnection(ip, port);
    }

    private void testServerConnection(String ip, String port) {
        new Thread(() -> {
            OkHttpClient client = new OkHttpClient();
            String url = "http://" + ip + ":" + port + "/";

            Request request = new Request.Builder().url(url).build();
            try {
                Response response = client.newCall(request).execute();
                boolean isSuccessful = response.isSuccessful();

                // Retour au thread principal pour afficher le Toast
                requireActivity().runOnUiThread(() -> {
                    if (isSuccessful) {
                        Toast.makeText(getContext(), "Connexion réussie !", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Serveur trouvé mais erreur: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Échec de connexion au serveur", Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }
    private void saveAllSettingsSilently() {
        String ip = binding.editIp.getText().toString().trim();
        String port = binding.editPort.getText().toString().trim();

        if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(port)) {
            return; // ❌ ne rien faire si vide
        }

        preferences.edit()
                .putString("server_ip", ip)
                .putString("server_port", port)
                .putString("api_read", binding.editReadApi.getText().toString().trim())
                .apply();

        String read = binding.editReadApi.getText().toString().trim();

        // 🔥 Appliquer direct la config
        ApiConfig.init(ip, port, read);
        ApiClient.reset();
    }
    @Override
    public void onPause() {
        super.onPause();

        // 🔥 Sauvegarde automatique
        saveAllSettingsSilently();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) requireActivity()).setFabVisibility(true);
        binding = null;
    }
}