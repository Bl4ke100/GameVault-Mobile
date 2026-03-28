package com.blake.gamevault.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blake.gamevault.R;
import com.blake.gamevault.model.Game;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.LibraryViewHolder> {

    private List<Game> gameList;
    private OnKeyClickListener listener;

    public LibraryAdapter(List<Game> gameList, OnKeyClickListener listener) {
        this.gameList = gameList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LibraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Make sure to name your new XML file "item_library_card" or change this reference!
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_library_game, parent, false);
        return new LibraryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryViewHolder holder, int position) {
        Game game = gameList.get(position);
        Context context = holder.itemView.getContext();

        holder.gameTitle.setText(game.getTitle());

        Glide.with(context)
                .load(game.getPosterUrl())
                .into(holder.gameImage);

        // Attach the click listener ONLY to the "View Keys" button
        holder.btnRevealKey.setOnClickListener(v -> {
            if (listener != null) {
                listener.onKeyClick(game);
            }
        });
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

    public static class LibraryViewHolder extends RecyclerView.ViewHolder {
        ImageView gameImage;
        TextView gameTitle;
        MaterialButton btnRevealKey;

        public LibraryViewHolder(@NonNull View itemView) {
            super(itemView);
            gameImage = itemView.findViewById(R.id.libraryGameImage);
            gameTitle = itemView.findViewById(R.id.libraryGameTitle);
            btnRevealKey = itemView.findViewById(R.id.btnRevealKey);
        }
    }

    public interface OnKeyClickListener {
        void onKeyClick(Game game);
    }
}