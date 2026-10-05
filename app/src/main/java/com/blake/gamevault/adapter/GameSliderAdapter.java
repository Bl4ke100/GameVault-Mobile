package com.blake.gamevault.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.blake.gamevault.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;
import java.util.List;
public class GameSliderAdapter extends RecyclerView.Adapter<GameSliderAdapter.GameSliderViewHolder> {
    private List<String> images;
    private String gameId;
    public GameSliderAdapter(List<String> images, String gameId) {
        this.images = images;
        this.gameId = gameId;
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
        String fileName = images.get(position);
        com.blake.gamevault.GlideApp.with(holder.itemView).clear(holder.imageView);
        holder.imageView.setImageResource(R.drawable.placeholder_game);
        if (fileName != null && fileName.startsWith("http")) {
            com.blake.gamevault.GlideApp.with(holder.itemView)
                    .load(fileName)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(com.blake.gamevault.util.ShimmerUtils.getShimmerDrawable())
                    .error(R.drawable.close)
                    .transition(com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(holder.imageView);
        } else {
            String storagePath = "images/game-images/" + gameId + "/" + fileName;
            com.google.firebase.storage.StorageReference storageRef = FirebaseStorage.getInstance().getReference(storagePath);
            com.blake.gamevault.GlideApp.with(holder.itemView)
                    .load(storageRef)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(com.blake.gamevault.util.ShimmerUtils.getShimmerDrawable())
                    .error(R.drawable.close)
                    .centerCrop()
                    .into(holder.imageView);
        }
    }
    @Override
    public int getItemCount() {
        return images.size();
    }
    public static class GameSliderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public GameSliderViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.game_image_slider_item);
        }
    }
}