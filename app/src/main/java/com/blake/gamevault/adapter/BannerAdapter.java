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
import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private List<String> bannerUrls;

    public BannerAdapter(List<String> bannerUrls) {
        this.bannerUrls = bannerUrls;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        String imageUrl = bannerUrls.get(position);

        Glide.with(holder.itemView).clear(holder.imageView);

        Glide.with(holder.itemView)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Caches the banner instantly
                .placeholder(R.drawable.placeholder_game)
                .error(R.drawable.placeholder_game)
                .transition(com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade(300))
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return bannerUrls.size();
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.bannerImage);
        }
    }
}