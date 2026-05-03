package com.fa.baiboly.ui.fihirana;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fa.baiboly.R;
import com.fa.baiboly.data.fihirana.FihiranaService;
import com.fa.baiboly.data.history.HistoryService;
import com.fa.baiboly.databinding.ActivitySongDetailBinding;
import com.fa.baiboly.models.SongVerse;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.List;

public class SongDetailActivity extends AppCompatActivity {

    private ActivitySongDetailBinding binding;
    private FihiranaService service;
    private HistoryService historyService;

    // Global text scaling factor (can be linked to user settings later)
    private float textScale = 1.0f;

    /**
     * Static helper method to open this Activity
     * Provides a clean way to pass required data (songId and title)
     */
    public static void open(Context context, String songId, String title) {
        Intent intent = new Intent(context, SongDetailActivity.class);
        intent.putExtra("songId", songId);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySongDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Force high refresh rate for smoother scrolling experience
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.preferredRefreshRate = 120.0f;
        getWindow().setAttributes(params);

        // Keep screen ON while reading songs (prevents sleep interruption)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Load dynamic text scale (future: SharedPreferences or settings screen)
        loadTextScale();

        service = new FihiranaService(this);

        String songId = getIntent().getStringExtra("songId");

        // Save reading history
        historyService = new HistoryService(this);
        historyService.addHistory(songId, "fihirana");

        // Setup toolbar with formatted title
        setupToolbar(songId != null ? songId.toUpperCase().replace("_", " ") : "Chant");

        // Load song content
        loadSong(songId);
    }

    /**
     * Load user preferred text size
     * Currently hardcoded, but designed for future persistence (SharedPreferences)
     */
    private void loadTextScale() {
        textScale = 1.0f; // Example: 1.2f = larger text, 0.9f = smaller text
    }

    /**
     * Configure toolbar (title + back navigation)
     */
    private void setupToolbar(String title) {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    /**
     * Load song verses and bind them to RecyclerView
     */
    private void loadSong(String songId) {
        List<SongVerse> verses = service.getSongVerses(songId);

        // Configure RecyclerView
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Optimization: improves performance when size is ثابت
        binding.recyclerView.setHasFixedSize(true);

        String title = getIntent().getStringExtra("title");
        CollapsingToolbarLayout collapsing =
                findViewById(R.id.collapsingToolbar);

        collapsing.setTitle(title);

        // IMPORTANT: pass textScale to adapter so it can apply dynamic sizing
        SongVersesAdapter adapter = new SongVersesAdapter(verses, textScale);

        binding.recyclerView.setAdapter(adapter);
    }
}