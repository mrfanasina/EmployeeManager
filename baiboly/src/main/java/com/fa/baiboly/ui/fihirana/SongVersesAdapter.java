package com.fa.baiboly.ui.fihirana;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fa.baiboly.R;
import com.fa.baiboly.models.SongVerse;

import java.util.List;

public class SongVersesAdapter extends RecyclerView.Adapter<SongVersesAdapter.VH> {

    private final List<SongVerse> verses;

    // Dynamic text scaling factor (controlled by Activity / user settings)
    private final float textScale;

    /**
     * Constructor with text scaling support
     * @param verses list of song verses
     * @param textScale global scaling factor for text size
     */
    public SongVersesAdapter(List<SongVerse> verses, float textScale) {
        this.verses = verses;
        this.textScale = textScale;
    }

    /**
     * ViewHolder pattern for performance optimization
     * Holds references to UI components for each item
     */
    static class VH extends RecyclerView.ViewHolder {
        TextView tvNumber, tvText;

        public VH(View v) {
            super(v);
            tvNumber = v.findViewById(R.id.verseNumber);
            tvText = v.findViewById(R.id.verseText);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Inflate item layout for each verse row
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_verse, parent, false);

        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {

        SongVerse verse = verses.get(position);

        // -----------------------------
        // APPLY DYNAMIC TEXT SIZE
        // -----------------------------
        // Base sizes (in SP) scaled dynamically
        float baseTextSize = 16f;
        float baseNumberSize = 12f;

        holder.tvText.setTextSize(baseTextSize * textScale);
        holder.tvNumber.setTextSize(baseNumberSize * textScale);

        // -----------------------------
        // DETERMINE VERSE TYPE
        // -----------------------------

        // Check if it's a chorus (fiverenany)
        if (verse.getFiverenany()) {

            // Chorus style
            holder.tvNumber.setText("Fiv:");
            holder.tvNumber.setVisibility(View.VISIBLE);

            // Italic style to differentiate chorus
            holder.tvText.setTypeface(null, Typeface.ITALIC);

        } else if (verse.getAndininy() == 0) {

            // Special case: no verse number
            holder.tvNumber.setVisibility(View.GONE);
            holder.tvText.setTypeface(null, Typeface.NORMAL);

        } else {

            // Normal verse style
            holder.tvNumber.setText(verse.getAndininy() + ".");
            holder.tvNumber.setVisibility(View.VISIBLE);

            holder.tvText.setTypeface(null, Typeface.NORMAL);
        }

        // -----------------------------
        // SET VERSE CONTENT
        // -----------------------------
        holder.tvText.setText(verse.getTononkira());
    }

    @Override
    public int getItemCount() {
        return verses != null ? verses.size() : 0;
    }
}