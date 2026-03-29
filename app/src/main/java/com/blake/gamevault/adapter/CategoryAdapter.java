package com.blake.gamevault.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blake.gamevault.R;
import com.blake.gamevault.model.Category;
import com.blake.gamevault.util.CardFlipAnimator;
import com.bumptech.glide.Glide;
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

        // 1. Clear old image so recycled views don't show the wrong category
        Glide.with(holder.itemView).clear(holder.catImage);
        holder.catImage.setImageResource(R.drawable.placeholder_game); // Use a category placeholder if you have one!

        String imagePath = category.getImageUrl();
        if (imagePath == null || imagePath.isEmpty()) {
            imagePath = "default.png"; // Fallback just in case
        }

        // 2. CACHING TRICK: Did we already fetch this URL?
        if (imagePath.startsWith("http")) {
            // Yes! Load instantly from memory.
            Glide.with(holder.itemView)
                    .load(imagePath)
                    .placeholder(R.drawable.placeholder_game)
                    .centerCrop()
                    .into(holder.catImage);
        } else {
            // No. Fetch from Firebase Storage.
            // Note: I removed the "/" you had before imagePath, getReference handles it better without it!
            storage.getReference(imagePath)
                    .getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        String realUrl = uri.toString();

                        // Save it back to the object to skip the network call next time
                        category.setImageUrl(realUrl);

                        // THE SHIELD: holder.itemView ties Glide to the View's lifecycle
                        Glide.with(holder.itemView)
                                .load(realUrl)
                                .placeholder(R.drawable.placeholder_game)
                                .centerCrop()
                                .into(holder.catImage);
                    })
                    .addOnFailureListener(e -> {
                        android.util.Log.e("CategoryAdapter", "Failed to load image for: " + category.getName());
                    });
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
