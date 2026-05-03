package com.fa.baiboly.ui.fihirana;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fa.baiboly.data.fihirana.FihiranaService;
import com.fa.baiboly.databinding.ActivitySongListBinding;
import com.fa.baiboly.models.Song;

import java.util.ArrayList;
import java.util.List;

public class SongListActivity extends AppCompatActivity {

    private ActivitySongListBinding binding;
    private FihiranaService service;
    private SongAdapter adapter;
    private List<Song> allSongs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySongListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Performance & Refresh Rate
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.preferredRefreshRate = 120.0f;
        getWindow().setAttributes(params);

        // Toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        service = new FihiranaService(this);
        String category = getIntent().getStringExtra("category");
        if (category == null) category = "Tous";
        setTitle(category.toUpperCase());

        setupRecyclerView();
        loadSongs(category);
        setupSearchFilter();
    }

    private void setupRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void loadSongs(String category) {
        allSongs = service.getSongsByCategory(category);

        // Initial setup of adapter with full list
        adapter = new SongAdapter(new ArrayList<>(allSongs), song -> {
            SongDetailActivity.open(this, song.getId(), song.getTitle());
        });

        binding.recyclerView.setAdapter(adapter);
    }

    private void setupSearchFilter() {
        binding.etSearchNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filter(String query) {
        List<Song> filteredList = new ArrayList<>();

        if (query.isEmpty()) {
            filteredList.addAll(allSongs);
        } else {
            for (Song song : allSongs) {
                // Assuming your Song model has getId() or a number field
                // We check if the song ID/Number contains the query
                String songNumber = String.valueOf(song.getId());
                if (songNumber.contains(query)) {
                    filteredList.add(song);
                }
            }
        }

        // Update the adapter with the new list
        // Note: Ensure your SongAdapter has a method to update data
        adapter.updateList(filteredList);
    }
}