package com.fa.baiboly.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.fa.baiboly.data.MofonainaRepository;
import com.fa.baiboly.data.fihirana.FihiranaService;
import com.fa.baiboly.data.history.HistoryService;
import com.fa.baiboly.data.perikopa.PerikopaService;
import com.fa.baiboly.databinding.FragmentHomeBinding;
import com.fa.baiboly.models.History;
import com.fa.baiboly.models.MofonainaData;
import com.fa.baiboly.models.PerikopaDay;
import com.fa.baiboly.models.Song;
import com.fa.baiboly.ui.fihirana.SongDetailActivity;
import com.fa.baiboly.ui.mofonaina.MofonainaActivity;
import com.fa.baiboly.ui.verses.VersesActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FihiranaService fihiranaService;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        loadMofonainaPreview();
        // ✅ INITIALISATION (IMPORTANT)
        fihiranaService = new FihiranaService(requireContext());

        // =========================
        // 📖 DAILY READING
        // =========================
        homeViewModel.loadTodayReading(requireContext());
        binding.textHome.setText(homeViewModel.getCurrentReading());

        binding.cardReading.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), VersesActivity.class);
            intent.putExtra("reading", homeViewModel.getCurrentReading());
            startActivity(intent);
        });


        // =========================
        // 📚 MOFONAINA
        // =========================
        binding.cardMofonaina.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), MofonainaActivity.class));
        });


        // =========================
        // 🌿 PERIKOPA
        // =========================
        loadPerikopa(inflater);

        // =========================
        // 🕘 HISTORY
        // =========================
        loadHistory(inflater);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding != null) {
            loadHistory(getLayoutInflater());
        }
    }

    // =========================
    // 🌿 PERIKOPA
    // =========================
    private void loadPerikopa(LayoutInflater inflater) {

        PerikopaService service = new PerikopaService(requireContext());
        PerikopaDay perikopa = service.getTodayPerikopa();
        binding.lohahevitra.setText(service.getTodayLohahevitra());
        binding.textPerikopaTitle.setText(
                perikopa.getName() != null
                        ? perikopa.getName()
                        : "Perikopa"
        );

        List<String> verses = perikopa.getVerses();
        binding.perikopaContainer.removeAllViews();

        if (verses != null && !verses.isEmpty()) {

            int limit = Math.min(verses.size(), 3);

            for (int i = 0; i < limit; i++) {

                String verse = verses.get(i);

                View verseView = inflater.inflate(
                        android.R.layout.simple_list_item_1,
                        binding.perikopaContainer,
                        false
                );

                android.widget.TextView tv =
                        verseView.findViewById(android.R.id.text1);

                tv.setText(verse);
                tv.setTextSize(16f);
                tv.setPadding(24, 16, 24, 16);

                verseView.setOnClickListener(v -> {
                    Intent intent = new Intent(requireContext(), VersesActivity.class);
                    intent.putExtra("reading", verse);
                    startActivity(intent);
                });

                binding.perikopaContainer.addView(verseView);
            }
        }
    }
    private void loadMofonainaPreview() {

        new Thread(() -> {

            try {

                MofonainaRepository repo =
                        new MofonainaRepository(requireContext());

                MofonainaData data =
                        repo.get("https://www.fjkm.mg");

                requireActivity().runOnUiThread(() -> {

                    if (data == null) return;

                    // =========================
                    // TITLE
                    // =========================
                    binding.textMofonainaTitle.setText(data.getTitle());

                    // =========================
                    // SUBTITLE (date ou intro)
                    // =========================
                    binding.textMofonainaSubTitle.setText(data.getDate());

                    // =========================
                    // RESET CONTAINER
                    // =========================
                    binding.mofonainaDetailsContainer.removeAllViews();

                    LayoutInflater inflater = LayoutInflater.from(requireContext());

                    // =========================
                    // VERSE OF DAY
                    // =========================
                    if (data.getVerseOfDay() != null) {

                        View item = inflater.inflate(
                                android.R.layout.simple_list_item_1,
                                binding.mofonainaDetailsContainer,
                                false
                        );

                        android.widget.TextView tv =
                                item.findViewById(android.R.id.text1);

                        tv.setText(data.getVerseOfDay().toString());
                        tv.setTextSize(16f);
                        tv.setPadding(0, 8, 0, 8);

                        binding.mofonainaDetailsContainer.addView(item);
                        item.setOnClickListener(v -> {
                            Intent intent = new Intent(requireContext(), VersesActivity.class);
                            intent.putExtra("reading", data.getBibleReading1().toString());
                            startActivity(intent);
                        });
                    }

                    //Reading1
                    if (data.getBibleReading1() != null) {
                        View item = inflater.inflate(
                                android.R.layout.simple_list_item_1,
                                binding.mofonainaDetailsContainer,
                                false
                        );

                        android.widget.TextView tv =
                                item.findViewById(android.R.id.text1);

                        tv.setText(data.getBibleReading1().toString());
                        tv.setTextSize(16f);
                        tv.setPadding(0, 8, 0, 8);

                        binding.mofonainaDetailsContainer.addView(item);
                        item.setOnClickListener(v -> {
                            Intent intent = new Intent(requireContext(), VersesActivity.class);
                            intent.putExtra("reading", data.getBibleReading1().toString());
                            startActivity(intent);
                        });
                    }


                    // =========================
                    // SONG 1
                    // =========================
                    if (data.getSong1() != null) {

                        View item = inflater.inflate(
                                android.R.layout.simple_list_item_1,
                                binding.mofonainaDetailsContainer,
                                false
                        );

                        android.widget.TextView tv =
                                item.findViewById(android.R.id.text1);

                        tv.setText(data.getSong1().getCategory().toUpperCase()+ " " + data.getSong1().getNumber());

                        item.setOnClickListener(v -> {
                            SongDetailActivity.open(
                                    requireContext(),
                                    data.getSong1().getId(),
                                    data.getSong1().getTitle()
                            );
                        });

                        binding.mofonainaDetailsContainer.addView(item);
                    }

                    // =========================
                    // SONG 2
                    // =========================
                    if (data.getSong2() != null) {

                        View item = inflater.inflate(
                                android.R.layout.simple_list_item_1,
                                binding.mofonainaDetailsContainer,
                                false
                        );

                        android.widget.TextView tv =
                                item.findViewById(android.R.id.text1);

                        tv.setText(data.getSong2().getCategory().toUpperCase()+ " " + data.getSong1().getNumber());

                        item.setOnClickListener(v -> {
                            SongDetailActivity.open(
                                    requireContext(),
                                    data.getSong2().getId(),
                                    data.getSong2().getTitle()
                            );
                        });

                        binding.mofonainaDetailsContainer.addView(item);
                    }

                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
    }
    // =========================
    // 🕘 HISTORY
    // =========================
    private void loadHistory(LayoutInflater inflater) {

        HistoryService historyService = new HistoryService(requireContext());
        List<History> historyList = historyService.getAllHistory();

        binding.historyContainer.removeAllViews();

        if (historyList != null && !historyList.isEmpty()) {


            for (int i = 0; i < historyList.size() ; i++) {

                History history = historyList.get(i);

                View item = inflater.inflate(
                        android.R.layout.simple_list_item_1,
                        binding.historyContainer,
                        false
                );

                android.widget.TextView tv =
                        item.findViewById(android.R.id.text1);


                tv.setText(history.getReading());
                
                tv.setTextSize(18);
                tv.setPadding(24, 16, 24, 16);

                item.setOnClickListener(v -> {

                    String historyType = history.getType();

                    if (historyType == null) return;

                    switch (historyType) {

                        case "baiboly":
                            Intent intent = new Intent(requireContext(), VersesActivity.class);
                            intent.putExtra("reading", history.getReading());
                            startActivity(intent);
                            break;

                        case "fihirana":
                            Song song = fihiranaService.getSongById(history.getReading());

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
                            break;

                        default:
                            Log.d("HISTORY_TYPE", historyType);
                            break;
                    }
                });

                binding.historyContainer.addView(item);
            }

        } else {
            android.widget.TextView empty = new android.widget.TextView(requireContext());
            empty.setText("Tsy mbola misy novakiana");
            empty.setPadding(24, 16, 24, 16);
            binding.historyContainer.addView(empty);
        }
    }
}