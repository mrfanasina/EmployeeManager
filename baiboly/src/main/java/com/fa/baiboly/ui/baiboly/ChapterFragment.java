package com.fa.baiboly.ui.baiboly;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.fa.baiboly.databinding.FragmentChapterBinding;
import com.fa.baiboly.models.Book;
import com.fa.baiboly.models.Chapter;
import com.fa.baiboly.data.bible.BibleService;
import com.fa.baiboly.ui.verses.VersesActivity;

import java.util.ArrayList;
import java.util.List;

public class ChapterFragment extends Fragment {

    private FragmentChapterBinding binding;

    private BibleService bibleService;
    private Book selectedBook;

    private Chapter selectedChapter = null;
    private int selectedStartVerse = -1;
    private int selectedEndVerse = -1;

    public static ChapterFragment newInstance(Book book) {
        ChapterFragment fragment = new ChapterFragment();
        Bundle args = new Bundle();
        args.putSerializable("book", book);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentChapterBinding.inflate(inflater, container, false);

        bibleService = new BibleService(requireContext());

        if (getArguments() != null) {
            selectedBook = (Book) getArguments().getSerializable("book");
        }

        loadChapters();

        return binding.getRoot();
    }

    // =========================
    // 📖 Charger chapitres
    // =========================
    private void loadChapters() {

        if (selectedBook == null) return;

        List<Chapter> chapters = bibleService.getChapters(selectedBook.getId());

        ChapterAdapter adapter = new ChapterAdapter(chapters, chapter -> {

            selectedChapter = chapter;
            selectedStartVerse = -1;
            selectedEndVerse = -1;

            binding.btnRead.setVisibility(View.GONE);

            showVerseSelector(chapter);
        });

        binding.recyclerChapters.setLayoutManager(
                new GridLayoutManager(getContext(), 4)
        );

        binding.recyclerChapters.setAdapter(adapter);

        binding.tvBookTitle.setText(selectedBook.getLongName());
    }
    // =========================
    // 🎯 Sélection versets
    // =========================
    private void showVerseSelector(Chapter chapter) {

        View view = LayoutInflater.from(getContext())
                .inflate(com.fa.baiboly.R.layout.dialog_verse_selector, null);

        androidx.recyclerview.widget.RecyclerView recyclerStart =
                view.findViewById(com.fa.baiboly.R.id.recyclerStart);

        androidx.recyclerview.widget.RecyclerView recyclerEnd =
                view.findViewById(com.fa.baiboly.R.id.recyclerEnd);

        int maxVerse = chapter.getVerseCount();

        selectedStartVerse = -1;
        selectedEndVerse = -1;

        recyclerStart.setLayoutManager(
                new GridLayoutManager(getContext(), 5)
        );

        recyclerEnd.setLayoutManager(
                new GridLayoutManager(getContext(), 5)
        );

        VerseNumberAdapter startAdapter = new VerseNumberAdapter(maxVerse, verse -> {
            selectedStartVerse = verse;
            updateReadButton();
        });

        VerseNumberAdapter endAdapter = new VerseNumberAdapter(maxVerse, verse -> {
            selectedEndVerse = verse;
            updateReadButton();

            if (selectedStartVerse != -1 && selectedEndVerse != -1) {
                openReading();
            }
        });

        recyclerStart.setAdapter(startAdapter);
        recyclerEnd.setAdapter(endAdapter);

        new AlertDialog.Builder(getContext())
                .setTitle("Choisir versets")
                .setView(view)
                .setNegativeButton("Fermer", null)
                .show();
    }

    // =========================
    // 🔘 Bouton lecture
    // =========================
    private void updateReadButton() {

        if (selectedChapter == null) return;

        String text;

        if (selectedStartVerse == -1) {
            text = selectedBook.getShortName() + " "
                    + selectedChapter.getChapterNumber();
        } else {
            text = selectedBook.getShortName() + " "
                    + selectedChapter.getChapterNumber() + ":"
                    + selectedStartVerse;

            if (selectedEndVerse != selectedStartVerse) {
                text += "-" + selectedEndVerse;
            }
        }

        binding.btnRead.setText(text);
        binding.btnRead.setVisibility(View.VISIBLE);

        binding.btnRead.setOnClickListener(v -> openReading());
    }

    // =========================
    // 📖 Ouvrir lecture
    // =========================
    private void openReading() {

        if (selectedChapter == null) return;

        String reading;

        if (selectedStartVerse == -1) {
            reading = selectedBook.getShortName() + " "
                    + selectedChapter.getChapterNumber();
        } else {
            reading = selectedBook.getShortName() + " "
                    + selectedChapter.getChapterNumber() + ":"
                    + selectedStartVerse;

            if (selectedEndVerse != selectedStartVerse) {
                reading += "-" + selectedEndVerse;
            }
        }

        Intent intent = new Intent(getContext(), VersesActivity.class);
        intent.putExtra("reading", reading);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}