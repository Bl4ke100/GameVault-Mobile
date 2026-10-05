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
    private List<com.google.firebase.storage.StorageReference> bannerRefs;
    public BannerAdapter(List<com.google.firebase.storage.StorageReference> bannerRefs) {
        this.bannerRefs = bannerRefs;
    }
    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        com.google.firebase.storage.StorageReference storageRef = bannerRefs.get(position);
        com.blake.gamevault.GlideApp.with(holder.itemView).clear(holder.imageView);
        com.blake.gamevault.GlideApp.with(holder.itemView)
                .load(storageRef)
                .diskCacheStrategy(DiskCacheStrategy.ALL) 
                .placeholder(com.blake.gamevault.util.ShimmerUtils.getShimmerDrawable())
                .error(R.drawable.placeholder_game)
                .centerCrop()
                .into(holder.imageView);
    }
    @Override
    public int getItemCount() {
        return bannerRefs.size();
    }
    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.bannerImage);
        }
    }
}