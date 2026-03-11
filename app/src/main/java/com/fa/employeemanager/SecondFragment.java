package com.fa.employeemanager;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.fa.employeemanager.databinding.FragmentSecondBinding;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;

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

        binding.btnSave.setOnClickListener(v -> {

            String nom = binding.editNom.getText().toString();
            String salaire = binding.editSalaire.getText().toString();

            if (TextUtils.isEmpty(nom) || TextUtils.isEmpty(salaire)) {
                Toast.makeText(getContext(),
                        "Remplir tous les champs",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(getContext(),
                    "Employé ajouté",
                    Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}