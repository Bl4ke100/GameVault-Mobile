package com.blake.gamevault.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blake.gamevault.R;
import com.blake.gamevault.model.Game;
import com.blake.gamevault.model.Order;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private OnOrderClickListener listener;
    private FirebaseFirestore db;

    public OrderAdapter(List<Order> orderList, OnOrderClickListener listener) {
        this.orderList = orderList;
        this.listener = listener;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_purchase_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        Context context = holder.itemView.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        holder.orderIdText.setText("#" + order.getOrderId());
        holder.orderTotalText.setText(String.format(Locale.US, "LKR %,.2f", order.getTotalAmount()));

        holder.orderStatusBadge.setText(order.getStatus());
        if ("CANCELLED".equalsIgnoreCase(order.getStatus())) {
            holder.orderStatusBadge.setBackgroundColor(Color.parseColor("#D32F2F")); // Red for cancelled
        } else {
            holder.orderStatusBadge.setBackgroundResource(R.drawable.bg_stock_badge);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        holder.orderDateText.setText(sdf.format(new Date(order.getOrderDate())));

        holder.orderItemsContainer.removeAllViews();

        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            List<String> gameIds = new ArrayList<>();
            for (Order.OrderItem item : order.getOrderItems()) {
                gameIds.add(item.getGameId());
            }

            db.collection("games").whereIn("gameId", gameIds).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        Map<String, Game> gameMap = new HashMap<>();
                        for (Game game : queryDocumentSnapshots.toObjects(Game.class)) {
                            gameMap.put(game.getGameId(), game);
                        }

                        for (Order.OrderItem item : order.getOrderItems()) {
                            View rowView = inflater.inflate(R.layout.item_order_game_row, holder.orderItemsContainer, false);

                            ImageView img = rowView.findViewById(R.id.rowGameImage);
                            TextView title = rowView.findViewById(R.id.rowGameTitle);
                            TextView platformText = rowView.findViewById(R.id.rowGamePlatform);
                            TextView price = rowView.findViewById(R.id.rowGamePrice);
                            TextView qty = rowView.findViewById(R.id.rowGameQty);

                            price.setText(String.format(Locale.US, "LKR %,.2f", item.getUnitPrice()));
                            qty.setText("Qty: " + item.getQty());

                            // Find Platform Attribute
                            String platform = "Digital Download";
                            if (item.getAttributes() != null) {
                                for (Order.OrderItem.Attribute attr : item.getAttributes()) {
                                    if ("Platform".equalsIgnoreCase(attr.getName())) {
                                        platform = attr.getValue();
                                        break;
                                    }
                                }
                            }
                            platformText.setText(platform);

                            Game game = gameMap.get(item.getGameId());
                            if (game != null) {
                                title.setText(game.getTitle());

                                String storagePath = "images/game-images/" + game.getGameId() + "/poster.png";

                                com.google.firebase.storage.FirebaseStorage.getInstance().getReference(storagePath)
                                        .getDownloadUrl()
                                        .addOnSuccessListener(uri -> {
                                            Glide.with(context)
                                                    .load(uri)
                                                    .centerCrop()
                                                    .into(img);
                                        })
                                        .addOnFailureListener(e -> {
                                            android.util.Log.e("OrderAdapter", "Failed to load poster for: " + game.getTitle());
                                        });
                            }

                            holder.orderItemsContainer.addView(rowView);
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdText, orderStatusBadge, orderDateText, orderTotalText;
        LinearLayout orderItemsContainer;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdText = itemView.findViewById(R.id.orderIdText);
            orderStatusBadge = itemView.findViewById(R.id.orderStatusBadge);
            orderDateText = itemView.findViewById(R.id.orderDateText);
            orderTotalText = itemView.findViewById(R.id.orderTotalText);
            orderItemsContainer = itemView.findViewById(R.id.orderItemsContainer);
        }
    }

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }
}