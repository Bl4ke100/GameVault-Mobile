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

        Glide.with(holder.itemView.getContext()).clear(holder.gameImage);
        holder.gameImage.setImageResource(R.drawable.placeholder_game);
        holder.resetPosition();

        String posterName = game.getPosterUrl();
        if (posterName == null || posterName.isEmpty()) {
            posterName = "poster.png";
        }

        if (posterName.startsWith("http")) {
            Glide.with(holder.itemView.getContext())
                    .load(posterName)
                    .centerCrop()
                    .into(holder.gameImage);

            holder.gameImage.setScaleX(1.03f);
            holder.gameImage.setScaleY(1.03f);

        } else {
            String storagePath = "images/game-images/" + game.getGameId() + "/" + posterName;

            com.google.firebase.storage.FirebaseStorage.getInstance().getReference(storagePath)
                    .getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        String realUrl = uri.toString();

                        game.setPosterUrl(realUrl);

                        Glide.with(holder.itemView.getContext())
                                .load(realUrl)
                                .centerCrop()
                                .into(holder.gameImage);

                        holder.gameImage.setScaleX(1.03f);
                        holder.gameImage.setScaleY(1.03f);
                    })
                    .addOnFailureListener(e -> Log.e("StorageError", "No image for: " + game.getGameId()));
        }

        CardFlipAnimator.attach(holder.itemView, () -> {
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

        private SensorManager sensorManager;
        private Sensor gravitySensor;

        private float intensity = 1f;
        private float smoothing = 0.1f;
        private float curX = 0, curY = 0;

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

        public void resetPosition() {
            curX = 0;
            curY = 0;
            gameImage.setTranslationX(0);
            gameImage.setTranslationY(0);
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            float targetX = event.values[0] * intensity;
            float targetY = event.values[1] * intensity;

            curX += (targetX - curX) * smoothing;
            curY += (targetY - curY) * smoothing;

            gameImage.setTranslationX(curX);
            gameImage.setTranslationY(curY);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }

    public interface OnGameClickListener {
        void onGameClick(Game game);
    }
}