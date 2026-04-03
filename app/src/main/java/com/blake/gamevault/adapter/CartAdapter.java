package com.blake.gamevault.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blake.gamevault.R;
import com.blake.gamevault.model.CartItem;
import com.blake.gamevault.model.Game;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<CartItem> cartItems;
    private OnQtyChangeListener qtyChangeListener;
    private OnRemoveListener removeListener;

    public CartAdapter(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public void setOnQtyChangeListener(OnQtyChangeListener listener) {
        this.qtyChangeListener = listener;
    }

    public void setOnRemoveListener(OnRemoveListener listener) {
        this.removeListener = listener;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("games")
                .whereEqualTo("gameId", cartItem.getGameId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot qds) {
                        if (!qds.isEmpty()) {
                            int currentposition = holder.getAbsoluteAdapterPosition();
                            if (currentposition == RecyclerView.NO_POSITION) return;

                            Game game = qds.getDocuments().get(0).toObject(Game.class);
                            if (game == null) return;

                            holder.gameTitle.setText(game.getTitle());
                            holder.gameUnitPrice.setText(String.format(Locale.US, "LKR %,.2f / unit", game.getPrice()));
                            holder.gameQty.setText(String.valueOf(cartItem.getQty()));
                            holder.gamePrice.setText(String.format(Locale.US, "LKR %,.2f", game.getPrice() * cartItem.getQty()));

                            String selectedPlatform = "PC"; // Default fallback
                            if (cartItem.getAttributes() != null) {
                                for (CartItem.Attribute attr : cartItem.getAttributes()) {
                                    if (attr.getName() != null && attr.getName().equalsIgnoreCase("Platform")) {
                                        selectedPlatform = attr.getValue();
                                        break;
                                    }
                                }
                            }
                            holder.gamePlatform.setText(selectedPlatform);

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
                                        Log.e("CartAdapter", "Failed to load poster for: " + game.getTitle());
                                    });

                            holder.btnPlus.setOnClickListener(v -> {
                                if (cartItem.getQty() < game.getStock()) {
                                    cartItem.setQty(cartItem.getQty() + 1);
                                    notifyItemChanged(currentposition);
                                    if (qtyChangeListener != null) {
                                        qtyChangeListener.onChanged(cartItem);
                                    }
                                }
                            });

                            holder.btnMinus.setOnClickListener(v -> {
                                if (cartItem.getQty() > 1) {
                                    cartItem.setQty(cartItem.getQty() - 1);
                                    notifyItemChanged(currentposition);
                                    if (qtyChangeListener != null) {
                                        qtyChangeListener.onChanged(cartItem);
                                    }
                                }
                            });

                            holder.btnRemove.setOnClickListener(v -> {
                                if (removeListener != null) {
                                    removeListener.onRemove(currentposition);
                                }
                            });
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView gameImage;
        TextView gameTitle;
        TextView gamePrice;
        TextView gameUnitPrice;
        TextView gamePlatform;
        TextView gameQty;
        ImageButton btnPlus;
        ImageButton btnMinus;
        ImageButton btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gameImage = itemView.findViewById(R.id.cartItemImage);
            gameTitle = itemView.findViewById(R.id.cartItemTitle);
            gamePlatform = itemView.findViewById(R.id.cartItemPlatform);
            gameUnitPrice = itemView.findViewById(R.id.cartItemUnitPrice);
            gameQty = itemView.findViewById(R.id.cartItemQuantity);
            gamePrice = itemView.findViewById(R.id.cartItemSubtotal);
            btnPlus = itemView.findViewById(R.id.cartQtyIncrease);
            btnMinus = itemView.findViewById(R.id.cartQtyDecrease);
            btnRemove = itemView.findViewById(R.id.cartItemRemove);
        }
    }

    public interface OnQtyChangeListener {
        void onChanged(CartItem cartItem);
    }

    public interface OnRemoveListener {
        void onRemove(int position);
    }
}