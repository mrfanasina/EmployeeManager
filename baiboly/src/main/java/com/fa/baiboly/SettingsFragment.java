package com.fa.baiboly;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.fa.baiboly.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SharedPreferences preferences;

    private static final String PREFS_NAME = "app_settings";
    private static final String KEY_THEME = "theme_mode";
    private static final String KEY_TEXT_SIZE = "text_size";

    private float tempTextSize = 18f;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        preferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        return binding.getRoot();
    }

    // ❌ DO NOT TOUCH ACTIVITY HERE
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbar.setNavigationOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp()
        );

        loadSavedSettings();
        setupThemeChanger();
        setupTextSizeSeekBar();

        binding.btnSaveSettings.setOnClickListener(v -> {
            saveSettings();
            Toast.makeText(getContext(), "Paramètres enregistrés", Toast.LENGTH_SHORT).show();
        });
    }

    // ✅ SAFE PLACE (activity is ready)
    @Override
    public void onResume() {
        super.onResume();

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setBottomNavVisibility(false);
        }

        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().hide();
            }
        }
    }

    // =========================
    // 🎨 THEME
    // =========================
    private void setupThemeChanger() {

        binding.themeGroup.setOnCheckedChangeListener((group, checkedId) -> {

            int mode;

            if (checkedId == R.id.radioLight) {
                mode = AppCompatDelegate.MODE_NIGHT_NO;
            } else if (checkedId == R.id.radioDark) {
                mode = AppCompatDelegate.MODE_NIGHT_YES;
            } else {
                mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
            }

            preferences.edit().putInt(KEY_THEME, mode).apply();
            AppCompatDelegate.setDefaultNightMode(mode);
        });
    }

    // =========================
    // 🔠 TEXT SIZE
    // =========================
    private void setupTextSizeSeekBar() {

        SeekBar seekBar = binding.textSizeSeek;

        tempTextSize = preferences.getFloat(KEY_TEXT_SIZE, 18f);
        seekBar.setProgress((int) tempTextSize);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) tempTextSize = progress;
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    // =========================
    // 💾 SAVE
    // =========================
    private void saveSettings() {

        preferences.edit()
                .putFloat(KEY_TEXT_SIZE, tempTextSize)
                .apply();

        applyTextSizeGlobally(tempTextSize);
    }

    // =========================
    // 🔄 LOAD
    // =========================
    private void loadSavedSettings() {

        int savedMode = preferences.getInt(KEY_THEME,
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        if (savedMode == AppCompatDelegate.MODE_NIGHT_NO) {
            binding.radioLight.setChecked(true);
        } else if (savedMode == AppCompatDelegate.MODE_NIGHT_YES) {
            binding.radioDark.setChecked(true);
        } else {
            binding.radioSystem.setChecked(true);
        }

        tempTextSize = preferences.getFloat(KEY_TEXT_SIZE, 18f);
        binding.textSizeSeek.setProgress((int) tempTextSize);
    }

    // =========================
    // 🔤 APPLY GLOBAL TEXT SIZE
    // =========================
    private void applyTextSizeGlobally(float size) {

        if (getActivity() == null) return;

        View root = getActivity().findViewById(android.R.id.content);

        if (root != null) {
            setTextSizeRecursive(root, size);
        }
    }

    private void setTextSizeRecursive(View view, float size) {

        if (view instanceof com.google.android.material.bottomnavigation.BottomNavigationView) {
            return;
        }

        if (view instanceof android.widget.TextView) {
            ((android.widget.TextView) view).setTextSize(size);
        }

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                setTextSizeRecursive(group.getChildAt(i), size);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // ✅ SAFE call
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setBottomNavVisibility(true);
        }

        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().show();
            }
        }

        binding = null;
    }
}