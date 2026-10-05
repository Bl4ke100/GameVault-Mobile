package com.blake.gamevault.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.blake.gamevault.R;
import com.blake.gamevault.model.Category;
import com.blake.gamevault.util.CardFlipAnimator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;
import java.util.List;
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private List<Category> categories;
    private OnCategoryClickListener listener;
    private FirebaseStorage storage;
    public CategoryAdapter(List<Category> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
        storage = FirebaseStorage.getInstance();
    }
    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.catName.setText(category.getName());
        com.blake.gamevault.GlideApp.with(holder.itemView).clear(holder.catImage);
        holder.catImage.setImageResource(R.drawable.placeholder_game);
        String imagePath = category.getImageUrl();
        if (imagePath == null || imagePath.isEmpty()) {
            imagePath = "default.webp";
        }
        if (imagePath.startsWith("http")) {
            com.blake.gamevault.GlideApp.with(holder.itemView)
                    .load(imagePath)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(com.blake.gamevault.util.ShimmerUtils.getShimmerDrawable())
                    .centerCrop()
                    .into(holder.catImage);
        } else {
            com.google.firebase.storage.StorageReference storageRef = storage.getReference(imagePath);
            com.blake.gamevault.GlideApp.with(holder.itemView)
                    .load(storageRef)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(com.blake.gamevault.util.ShimmerUtils.getShimmerDrawable())
                    .centerCrop()
                    .into(holder.catImage);
        }
        CardFlipAnimator.attach(holder.itemView, () -> {
            if (listener != null) {
                listener.onCategoryClick(category);
            }
        });
    }
    @Override
    public int getItemCount() {
        return categories.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView catImage;
        TextView catName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            catImage = itemView.findViewById(R.id.catImage);
            catName = itemView.findViewById(R.id.catName);
        }
    }
    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }
}