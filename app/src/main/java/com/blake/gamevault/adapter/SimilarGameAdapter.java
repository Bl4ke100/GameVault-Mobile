package com.blake.gamevault.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blake.gamevault.R;
import com.blake.gamevault.model.Game;
import com.blake.gamevault.util.CardFlipAnimator;
import com.bumptech.glide.Glide;

import java.util.List;

public class SimilarGameAdapter extends RecyclerView.Adapter<SimilarGameAdapter.ViewHolder> {

    private List<Game> games;
    private OnGameClickListener listener;

    public SimilarGameAdapter(List<Game> games, OnGameClickListener listener) {

        this.games = games;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SimilarGameAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_game_recycler, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SimilarGameAdapter.ViewHolder holder, int position) {
        Game game = games.get(position);
        holder.gameTitle.setText(game.getTitle());
        holder.gamePrice.setText("LKR " + game.getPrice()+"0");
        Glide.with(holder.itemView.getContext())
                .load(game.getPosterUrl())
                .into(holder.gameImage);

        CardFlipAnimator.attach(holder.itemView, () -> {
            if (listener != null) {
                listener.onGameClick(game);
            }
        });
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView gameImage;
        TextView gameTitle;
        TextView gamePrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gameImage = itemView.findViewById(R.id.similarGameImage);
            gameTitle = itemView.findViewById(R.id.similarGameName);
            gamePrice = itemView.findViewById(R.id.similarGamePrice);
        }
    }

    public interface OnGameClickListener{
        void onGameClick(Game game);
    }
}
