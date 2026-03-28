package com.blake.gamevault.adapter;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
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

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ViewHolder> {

    private List<Game> games;
    private OnGameClickListener listener;

    public ListingAdapter(List<Game> games, OnGameClickListener listener) {
        this.games = games;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ListingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.game_listing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListingAdapter.ViewHolder holder, int position) {
        Game game = games.get(position);
        holder.gameTitle.setText(game.getTitle());
        holder.gamePrice.setText("LKR " + game.getPrice() + "0");

        // 1. ADD THIS LINE: Clear the old image so it doesn't "ghost" into the new item
        Glide.with(holder.itemView.getContext()).clear(holder.gameImage);

        // 2. Optional: Set a placeholder so it doesn't look empty
        holder.gameImage.setImageResource(R.drawable.placeholder_game);

        String storagePath = "images/game-images/" + game.getGameId() + "/poster.png";

        com.google.firebase.storage.FirebaseStorage.getInstance().getReference(storagePath)
                .getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Glide.with(holder.itemView.getContext())
                            .load(uri)
                            .centerCrop()
                            .into(holder.gameImage);
                })
                .addOnFailureListener(e -> {
                    // If the image doesn't exist, keep the placeholder
                    Log.e("StorageError", "No image for: " + game.getGameId());
                });

        CardFlipAnimator.attach(holder.itemView, () -> {
            Log.d("ClickTest", "Card clicked for: " + game.getTitle());
            if (listener != null) {
                listener.onGameClick(game);
            }
        });
    }


    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.startParallax();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.stopParallax();
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements SensorEventListener {

        ImageView gameImage;
        TextView gameTitle;
        TextView gamePrice;

        SensorManager sensorManager;
        Sensor gravitySensor;

        // Settings to fix the "too much" feeling
        private float intensity = 4f; // Lowered from 12f to 4f for subtle movement
        private float smoothing = 0.15f; // Values 0.0 to 1.0 (Lower is smoother/slower)

        private float currentX = 0;
        private float currentY = 0;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gameImage = itemView.findViewById(R.id.gameImage);
            gameTitle = itemView.findViewById(R.id.gameName);
            gamePrice = itemView.findViewById(R.id.gamePrice);

            sensorManager = (SensorManager) itemView.getContext().getSystemService(Context.SENSOR_SERVICE);
            gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        }

        public void startParallax() {
            if (gravitySensor != null) {
                sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_GAME);
            }
        }

        public void stopParallax() {
            sensorManager.unregisterListener(this);
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            // Target positions based on tilt
            float targetX = event.values[0] * intensity;
            float targetY = event.values[1] * intensity;

            // LERP (Linear Interpolation) for buttery smooth gliding
            currentX = currentX + (targetX - currentX) * smoothing;
            currentY = currentY + (targetY - currentY) * smoothing;

            gameImage.setTranslationX(currentX);
            gameImage.setTranslationY(currentY);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    }

    public interface OnGameClickListener {
        void onGameClick(Game game);
    }
}