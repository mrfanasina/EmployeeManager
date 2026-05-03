package com.fa.baiboly.ui.fihirana;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.fa.baiboly.data.fihirana.FihiranaService;
import com.fa.baiboly.databinding.FragmentNotificationsBinding;
import com.fa.baiboly.models.Song;

import java.util.List;

public class FihiranaFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private FihiranaService service;
    private String currentInput = ""; // Pour stocker le numéro saisi

    private String selectedCategory = "";
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        service = new FihiranaService(requireContext());

        setupNumPad();
        setupRecyclerView();
        loadCategories();
        setupSpinner();
    }

    private void setupNumPad() {
        // Listener pour les touches 0-9
        View.OnClickListener numberListener = v -> {
            String val = (String) v.getTag();
            if (currentInput.length() < 3) { // Max 999
                currentInput += val;
                updateDisplay();
            }
        };

        // On parcourt le GridLayout pour assigner le listener aux boutons numériques
        for (int i = 0; i < binding.numPad.getChildCount(); i++) {
            View child = binding.numPad.getChildAt(i);
            if (child instanceof Button) {
                child.setOnClickListener(numberListener);
            }
        }

        // Action Effacer
        binding.btnBackspace.setOnClickListener(v -> {
            if (!currentInput.isEmpty()) {
                currentInput = currentInput.substring(0, currentInput.length() - 1);
                updateDisplay();
            }
        });

        // Action Valider
        binding.btnValidate.setOnClickListener(v -> {

            if (currentInput.isEmpty()) return;

            if (selectedCategory == null || selectedCategory.isEmpty()) {
                Toast.makeText(requireContext(), "Sélectionne une catégorie", Toast.LENGTH_SHORT).show();
                return;
            }

            int number = Integer.parseInt(currentInput);

            Song song = service.getSongByNumberAndCategory(number, selectedCategory);

            if (song != null) {

                SongDetailActivity.open(
                        requireContext(),
                        song.getId(),
                        song.getTitle()
                );

            } else {
                Toast.makeText(requireContext(),
                        "Chant introuvable",
                        Toast.LENGTH_SHORT).show();
            }

            currentInput = "";
            updateDisplay();
        });
    }
    private void setupSpinner() {
        List<String> categories = service.getCategories();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.categorySpinner.setAdapter(adapter);
        binding.categorySpinner.setSelection(2);
        binding.categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = categories.get(position);
                int count = service.getSongCountByCategory(selectedCategory);
                binding.tvSongCount.setText( " Hira misy :" + count );
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    private void updateDisplay() {
        binding.tvSongNumber.setText(currentInput.isEmpty() ? "---" : currentInput);
    }

    private void setupRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        binding.recyclerCategories.setLayoutManager(layoutManager);
        binding.recyclerCategories.setHasFixedSize(true);
    }

    private void loadCategories() {
        List<String> categories = service.getCategories();

        CategoryAdapter adapter = new CategoryAdapter(categories, category -> {
            Intent intent = new Intent(requireContext(), SongListActivity.class);
            intent.putExtra("category", category);
            startActivity(intent);
        });

        binding.recyclerCategories.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}