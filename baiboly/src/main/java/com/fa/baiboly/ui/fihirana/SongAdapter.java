
package com.fa.baiboly.ui.fihirana;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fa.baiboly.R;
import com.fa.baiboly.models.Song;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.VH> {

    public interface OnClick {
        void onClick(Song song);
    }

    private final List<Song> list;
    private final OnClick listener;

    public SongAdapter(List<Song> list, OnClick listener) {
        this.list = list;
        this.listener = listener;
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvNumber, tvTitle, tvPreview;

        public VH(View v) {
            super(v);
            // On lie les IDs du fichier item_song.xml
            tvNumber = v.findViewById(R.id.songNumber);
            tvTitle = v.findViewById(R.id.songTitle);
            tvPreview = v.findViewById(R.id.songPreview);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Utilisation du nouveau layout personnalisé
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Song song = list.get(position);

        // Affichage du numéro dans le cercle
        holder.tvNumber.setText(String.valueOf(song.getNumber()));

        // Titre principal
        holder.tvTitle.setText(song.getTitle());

        // Sous-titre (aperçu). Si tu n'as pas de champ "preview",
        // tu peux mettre une catégorie ou une info supplémentaire.
        if (holder.tvPreview != null) {
            holder.tvPreview.setText(song.getCategory() + " " + song.getNumber());
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(song);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    // Inside your SongAdapter.java
    public void updateList(List<Song> newList) {
        this.list.clear();
        this.list.addAll(newList);
        notifyDataSetChanged();
    }
}