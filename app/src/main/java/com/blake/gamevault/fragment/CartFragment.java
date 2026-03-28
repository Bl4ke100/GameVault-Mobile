package com.blake.gamevault.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blake.gamevault.R;
import com.blake.gamevault.adapter.CartAdapter;
import com.blake.gamevault.databinding.FragmentCartBinding;
import com.blake.gamevault.databinding.FragmentShopBinding;
import com.blake.gamevault.model.CartItem;
import com.blake.gamevault.model.Game;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class CartFragment extends Fragment {

    private FragmentCartBinding binding;

    private List<CartItem> cartItems;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCartBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {

            String uid = firebaseAuth.getCurrentUser().getUid();

            db.collection("users").document(uid).collection("cart")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot qds) {

                            // Handle completely empty cart on first load
                            if (qds.isEmpty()) {
                                cartItems = new ArrayList<>();
                                updateEmptyState();
                                return;
                            }

                            cartItems = new ArrayList<>();

                            for (DocumentSnapshot ds : qds.getDocuments()) {
                                CartItem cartItem = ds.toObject(CartItem.class);
                                if (cartItem != null) {
                                    String documentId = ds.getId();
                                    cartItem.setDocumentId(documentId);

                                    cartItems.add(cartItem);
                                }
                            }

                            updateTotal();

                            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                            binding.cartRecyclerView.setLayoutManager(layoutManager);

                            CartAdapter adapter = new CartAdapter(cartItems);

                            adapter.setOnQtyChangeListener(cartItem -> {
                                String documentId = cartItem.getDocumentId();
                                db.collection("users").document(uid)
                                        .collection("cart").document(documentId)
                                        .update("qty", cartItem.getQty())
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(getContext(), "Item Quantity Changed!", Toast.LENGTH_SHORT).show();
                                        });

                                updateTotal();
                            });

                            adapter.setOnRemoveListener(position -> {
                                String documentId = cartItems.get(position).getDocumentId();
                                db.collection("users").document(uid)
                                        .collection("cart").document(documentId)
                                        .delete().addOnSuccessListener(aVoid -> {
                                            cartItems.remove(position);
                                            adapter.notifyItemRemoved(position);
                                            adapter.notifyItemRangeChanged(position, cartItems.size());
                                            updateTotal();
                                            updateEmptyState(); // Update UI when an item is removed
                                            Toast.makeText(getContext(), "Item Removed From Cart!", Toast.LENGTH_SHORT).show();
                                        });

                            });

                            binding.cartRecyclerView.setAdapter(adapter);
                            updateEmptyState(); // Update UI after setting adapter
                        }
                    });
        }

        binding.btnProceedToCheckout.setOnClickListener(v ->{
            CheckoutFragment checkoutFragment = new CheckoutFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, checkoutFragment)
                    .addToBackStack(null)
                    .commit();
        });

        binding.btnBrowseGames.setOnClickListener(v -> {
            // Replace ShopFragment with whatever your home/games fragment is called
            ShopFragment shopFragment = new ShopFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, shopFragment)
                    .commit();
        });

    }

    // Drop this helper method anywhere inside your CartFragment class
    private void updateEmptyState() {
        if (cartItems == null || cartItems.isEmpty()) {
            binding.cartRecyclerView.setVisibility(View.GONE);
            binding.bottomSummaryLayout.setVisibility(View.GONE);
            binding.emptyCartView.setVisibility(View.VISIBLE); // Updated ID
        } else {
            binding.cartRecyclerView.setVisibility(View.VISIBLE);
            binding.bottomSummaryLayout.setVisibility(View.VISIBLE);
            binding.emptyCartView.setVisibility(View.GONE); // Updated ID
        }
    }


    private void updateTotal() {
        if (cartItems == null || cartItems.isEmpty()) {
            binding.cartTotalAmount.setText(String.format(Locale.US, "LKR %,.2f", 0.00));
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        List<String> gameIds = new ArrayList<>();
        cartItems.forEach(cartItem -> {
            gameIds.add(cartItem.getGameId());
        });

        db.collection("games")
                .whereIn("gameId", gameIds)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot qds) {

                        Map<String, Game> gameMap = new HashMap<>();

                        qds.getDocuments().forEach(ds -> {
                            Game game = ds.toObject(Game.class);
                            if (game != null) {
                                gameMap.put(game.getGameId(), game);
                            }
                        });

                        double total = 0;

                        for (CartItem cartItem : cartItems) {
                            Game game = gameMap.get(cartItem.getGameId());
                            if (game != null) {
                                total += game.getPrice() * cartItem.getQty();
                            }
                        }

                        binding.cartTotalAmount.setText(String.format(Locale.US, "LKR %,.2f", total));

                        int totalQty = 0;
                        for (CartItem item : cartItems) {
                            totalQty += item.getQty();
                        }
                        binding.cartItemCount.setText(String.valueOf(totalQty));

                    }
                });
    }


}