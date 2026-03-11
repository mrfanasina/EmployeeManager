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
import androidx.fragment.app.Fragment;

import com.fa.employeemanager.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SharedPreferences preferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        preferences = requireContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE);

        // Charger IP/Port sauvegardé
        binding.editIp.setText(preferences.getString("server_ip", ""));
        binding.editPort.setText(preferences.getString("server_port", ""));

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        binding.btnSaveSettings.setOnClickListener(v -> {

            String ip = binding.editIp.getText().toString().trim();
            String port = binding.editPort.getText().toString().trim();

            if (TextUtils.isEmpty(ip) || TextUtils.isEmpty(port)) {
                Toast.makeText(getContext(), "Veuillez remplir IP et port", Toast.LENGTH_SHORT).show();
                return;
            }

            // Sauvegarder
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("server_ip", ip);
            editor.putString("server_port", port);
            editor.apply();

            Toast.makeText(getContext(), "Paramètres sauvegardés", Toast.LENGTH_SHORT).show();

            // Ici tu peux lancer une requête vers ton serveur
            testServerConnection(ip, port);
        });
    }

    private void testServerConnection(String ip, String port) {
        // Pour exemple, juste afficher un toast
        // Dans une vraie app, tu utiliserais Retrofit / OkHttp
        Toast.makeText(getContext(), "Connexion test à " + ip + ":" + port, Toast.LENGTH_SHORT).show();

        // Exemple pseudo-code pour récupération JSON
        // new Thread(() -> {
        //    OkHttpClient client = new OkHttpClient();
        //    Request request = new Request.Builder()
        //         .url("http://" + ip + ":" + port + "/employes")
        //         .build();
        //    Response response = client.newCall(request).execute();
        // }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
