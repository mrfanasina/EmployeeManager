package com.fa.baiboly.ui.baiboly;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.fa.baiboly.R;

public class VerseNumberAdapter extends RecyclerView.Adapter<VerseNumberAdapter.ViewHolder> {

    private final int count;
    private final OnVerseClick listener;
    private int selected = -1;

    public interface OnVerseClick {
        void onClick(int verse);
    }

    public VerseNumberAdapter(int count, OnVerseClick listener) {
        this.count = count;
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;

        ViewHolder(View v) {
            super(v);
            text = v.findViewById(R.id.txtChapter);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chapter, parent, false); // reuse ton item
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int verse = position + 1;

        holder.text.setText(String.valueOf(verse));

        // highlight sélection
        if (selected == verse) {
            holder.text.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.text.setBackgroundColor(Color.TRANSPARENT);
        }

        holder.itemView.setOnClickListener(v -> {
            selected = verse;
            notifyDataSetChanged();
            listener.onClick(verse);
        });
    }

    @Override
    public int getItemCount() {
        return count;
    }
}