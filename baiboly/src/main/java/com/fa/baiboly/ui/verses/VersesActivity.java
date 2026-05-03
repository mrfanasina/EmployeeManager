package com.fa.baiboly.ui.verses;

import android.graphics.Typeface;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.SuperscriptSpan;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.fa.baiboly.R;
import com.fa.baiboly.data.bible.BibleService;
import com.fa.baiboly.data.history.HistoryService;
import com.fa.baiboly.databinding.ActivityVersesBinding;
import com.fa.baiboly.models.Verse;

import java.util.List;

public class VersesActivity extends AppCompatActivity {

    private ActivityVersesBinding binding;
    private BibleService bibleService;
    private HistoryService historyService;

    // Global text scale factor (can be controlled by user settings later)
    private float textScale = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVersesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.preferredRefreshRate = 120.0f;
        getWindow().setAttributes(params);

        // Load dynamic text size (future: SharedPreferences / settings screen)
        loadTextScale();

        bibleService = new BibleService(this);
        String reading = getIntent().getStringExtra("reading");

        historyService = new HistoryService(this);
        historyService.addHistory(reading, "baiboly");
        if (reading == null) reading = "Baiboly";

        setupToolbar(reading);
        loadAndDisplayVerses(reading);
    }

    private void setupToolbar(String title) {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(title);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    // Load user preferred text size
    // For now it's hardcoded, but easily replaceable with SharedPreferences
    private void loadTextScale() {
        textScale = 1.0f; // Example: 1.0 = normal, 1.2 = bigger, 0.9 = smaller
    }

    private void loadAndDisplayVerses(String reading) {
        List<Verse> verseList = bibleService.getVerseObjectsFromReading(reading);
        SpannableStringBuilder builder = new SpannableStringBuilder();

        int primaryColor = ContextCompat.getColor(this, R.color.teal_200);
        int titleColor = ContextCompat.getColor(this, R.color.title_gray);

        for (Verse v : verseList) {

            // 1. ADD TITLE (If it exists)
            if (v.getTitle() != null && !v.getTitle().trim().isEmpty()) {

                // Add newline before title if it's not the first verse
                if (builder.length() > 0) {
                    builder.append("\n\n");
                }

                int titleStart = builder.length();
                String titleStr = v.getTitle();
                builder.append(titleStr).append("\n\n");

                // FIX: titleEnd is exactly the end of the title string text
                int titleEnd = titleStart + titleStr.length();

                // Center alignment for title
                builder.setSpan(
                        new android.text.style.AlignmentSpan.Standard(android.text.Layout.Alignment.ALIGN_CENTER),
                        titleStart, titleEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );

                // Typography styling for title
                builder.setSpan(new android.text.style.TypefaceSpan("sans-serif-medium"),
                        titleStart, titleEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                builder.setSpan(new StyleSpan(Typeface.ITALIC),
                        titleStart, titleEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                // Dynamic size applied here
                builder.setSpan(new RelativeSizeSpan(1.1f * textScale),
                        titleStart, titleEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                builder.setSpan(new ForegroundColorSpan(titleColor),
                        titleStart, titleEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            // 2. ADD VERSE NUMBER
            String numberStr = v.getNumber() + " ";
            int numStart = builder.length();
            builder.append(numberStr);
            int numEnd = numStart + numberStr.length();

            // Verse number styling (superscript small number)
            builder.setSpan(new ForegroundColorSpan(primaryColor),
                    numStart, numEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Dynamic size applied here
            builder.setSpan(new RelativeSizeSpan(0.7f * textScale),
                    numStart, numEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            builder.setSpan(new StyleSpan(Typeface.BOLD),
                    numStart, numEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            builder.setSpan(new SuperscriptSpan(),
                    numStart, numEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            // 3. ADD VERSE TEXT
            String rawText = v.getText()
                    .replace("<n>", "<br>")
                    .replace("</n>", "") + " ";

            CharSequence styledVerse;

            // HTML parsing depending on Android version
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                styledVerse = Html.fromHtml(rawText, Html.FROM_HTML_MODE_LEGACY);
            } else {
                styledVerse = Html.fromHtml(rawText);
            }

            builder.append(styledVerse);
        }

        // Apply full styled text to TextView
        binding.bibleTextFull.setText(builder);

        // Enable text justification (better reading experience)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.bibleTextFull.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }
    }
}