package com.fa.baiboly.ui.baiboly;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.fa.baiboly.R;
import com.fa.baiboly.databinding.FragmentBaibolyBinding;
import com.fa.baiboly.models.Book;
import com.fa.baiboly.data.bible.BibleService;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BaibolyFragment extends Fragment {

    private FragmentBaibolyBinding binding;
    private BibleService bibleService;

    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentBaibolyBinding.inflate(inflater, container, false);

        bibleService = new BibleService(requireContext());

        // ✅ Afficher loader immédiatement
        binding.progressBar.setVisibility(View.VISIBLE);

        // ❌ Ne PAS charger ici directement
        loadBooks();

        return binding.getRoot();
    }

    private void loadBooks() {

        executor.execute(() -> {

            // 📚 Chargement en background
            List<Book> oldTestament = bibleService.getBooks("AT", "mg");
            List<Book> newTestament = bibleService.getBooks("NT", "mg");

            mainHandler.post(() -> {

                if (binding == null) return;

                // 🎯 Adapters
                BookAdapter oldAdapter = new BookAdapter(oldTestament, this::onBookSelected);
                BookAdapter newAdapter = new BookAdapter(newTestament, this::onBookSelected);

                // 📱 UI OLD TESTAMENT
                binding.recyclerOld.setLayoutManager(new GridLayoutManager(requireContext(), 1));
                binding.recyclerOld.setAdapter(oldAdapter);
                binding.recyclerOld.setHasFixedSize(true);

                // 📱 UI NEW TESTAMENT
                binding.recyclerNew.setLayoutManager(new GridLayoutManager(requireContext(), 1));
                binding.recyclerNew.setAdapter(newAdapter);
                binding.recyclerNew.setHasFixedSize(true);

                // ✅ Cacher loader
                binding.progressBar.setVisibility(View.GONE);
            });
        });
    }

    // =========================
    // 📖 Navigation vers chapitres
    // =========================
    private void onBookSelected(Book book) {

        Bundle bundle = new Bundle();
        bundle.putSerializable("book", book);

        NavController navController =
                Navigation.findNavController(requireView());

        navController.navigate(R.id.navigation_chapter, bundle);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}