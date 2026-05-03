package com.fa.baiboly.ui.mofonaina;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fa.baiboly.data.MofonainaRepository;
import com.fa.baiboly.data.perikopa.PerikopaService;
import com.fa.baiboly.databinding.ActivityMofonainaBinding;
import com.fa.baiboly.models.MofonainaData;
import com.fa.baiboly.models.Song;
import com.fa.baiboly.ui.fihirana.SongDetailActivity;
import com.fa.baiboly.ui.verses.VersesActivity;

public class MofonainaActivity extends AppCompatActivity {

    private ActivityMofonainaBinding binding;

    private float textSize = 18f;

    private static final String PREFS_NAME = "app_settings";
    private static final String KEY_TEXT_SIZE = "text_size";

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityMofonainaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 🔥 load text size
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        textSize = prefs.getFloat(KEY_TEXT_SIZE, 18f);

        // Keep screen active
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.preferredRefreshRate = 120.0f;
        getWindow().setAttributes(params);

        PerikopaService perikopaService = new PerikopaService(this);

        String title = perikopaService.getTodayLohahevitra();

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setElevation(0);
        }

        fetchMofonainaData();
    }

    private void fetchMofonainaData() {

        binding.loader.setVisibility(View.VISIBLE);
        binding.mainScroll.setVisibility(View.GONE);

        new Thread(() -> {

            try {
                MofonainaRepository repo = new MofonainaRepository(this);
                MofonainaData data = repo.get("https://www.fjkm.mg");

                runOnUiThread(() -> {

                    if (data != null && !isFinishing()) {
                        updateUI(data);
                    } else {
                        binding.loader.setVisibility(View.GONE);
                        Toast.makeText(this, "Erreur chargement", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> binding.loader.setVisibility(View.GONE));
            }

        }).start();
    }

    private void updateUI(MofonainaData data) {

        binding.loader.setVisibility(View.GONE);
        binding.mainScroll.setVisibility(View.VISIBLE);

        binding.textTitle.setText(data.getTitle());
        binding.textDate.setText(data.getDate());
        binding.textReflection.setText(data.getReflection());
        binding.textQuestion.setText(data.getQuestion());

        // 📖 Verse
        if (data.getVerseOfDay() != null) {
            String ref = data.getVerseOfDay().toString();

            binding.textVerseOfDay.setText(ref);
            binding.cardVerseOfDay.setOnClickListener(v -> {
                Intent intent = new Intent(this, VersesActivity.class);
                intent.putExtra("reading", ref);
                startActivity(intent);
            });
        }

        // 📖 Reading
        if (data.getBibleReading1() != null) {
            String ref = data.getBibleReading1().toString();

            binding.textReading.setText(ref);
            binding.textReading.setOnClickListener(v -> {
                Intent intent = new Intent(this, VersesActivity.class);
                intent.putExtra("reading", ref);
                startActivity(intent);
            });
        }

        // 🎵 Songs
        binding.songsContainer.removeAllViews();
        setupSong(data.getSong1());
        setupSong(data.getSong2());

        // 🔥 APPLY TEXT SIZE (IMPORTANT)
        binding.getRoot().post(() -> applyTextSize(binding.getRoot(), textSize));
    }

    private void setupSong(Song song) {

        if (song == null) return;

        TextView tv = new TextView(this);
        tv.setText("🎵 " + song.getCategory().toUpperCase() + " " + song.getNumber());
        tv.setPadding(0, 16, 0, 16);
        tv.setTextSize(textSize); // 🔥 dynamique ici
        tv.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));

        tv.setOnClickListener(v -> {
            SongDetailActivity.open(this, song.getId(), song.getTitle());
        });

        binding.songsContainer.addView(tv);
    }

    // =========================
    // 🔥 TEXT SIZE ENGINE
    // =========================
    private void applyTextSize(View view, float size) {

        if (view instanceof TextView) {
            ((TextView) view).setTextSize(size);
        }

        if (view instanceof ViewGroup) {

            ViewGroup group = (ViewGroup) view;

            for (int i = 0; i < group.getChildCount(); i++) {
                applyTextSize(group.getChildAt(i), size);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}