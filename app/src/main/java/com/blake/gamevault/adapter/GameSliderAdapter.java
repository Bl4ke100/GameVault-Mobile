package com.blake.gamevault.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blake.gamevault.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class GameSliderAdapter extends RecyclerView.Adapter<GameSliderAdapter.GameSliderViewHolder> {

    private List<String> images;

    public GameSliderAdapter(List<String> images) {
        this.images = images;
    }

    @NonNull
    @Override
    public GameSliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.game_image_slider, parent, false);
        return new GameSliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameSliderViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext())
                .load(images.get(position))
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public static class GameSliderViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public GameSliderViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.game_image_slider_item);        }
    }
}
