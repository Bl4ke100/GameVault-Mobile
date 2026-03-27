package com.blake.gamevault.fragment;

import android.content.LocusId;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blake.gamevault.R;
import com.blake.gamevault.databinding.FragmentCartBinding;
import com.blake.gamevault.databinding.FragmentCheckoutBinding;
import com.blake.gamevault.listener.FireStoreCallback;
import com.blake.gamevault.model.CartItem;
import com.blake.gamevault.model.Game;
import com.blake.gamevault.model.Order;
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

public class CheckoutFragment extends Fragment {

    private FragmentCheckoutBinding binding;

    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCheckoutBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getCartItems(cartItems -> {

            ArrayList<String> gameIds = new ArrayList<>();
            for (CartItem cartItem : cartItems) {
                gameIds.add(cartItem.getGameId());
            }

            getGamesById(gameIds, data -> {
                double subTotal = 0;
                int itemCount = 0; // 1. Initialize to 0

                for (CartItem cartItem : cartItems) {
                    // 2. Use getGameId() to match the map key!
                    Game game = data.get(cartItem.getGameId());

                    if (game != null) {
                        subTotal += game.getPrice() * cartItem.getQty();
                        // 3. Use += to add them all together
                        itemCount += cartItem.getQty();
                    }
                }

                binding.checkoutItemCount.setText(String.valueOf(itemCount));
                binding.checkoutTotalAmount.setText(String.format(Locale.US, "LKR %,.2f", subTotal));
            });

        });


        String uid = firebaseAuth.getCurrentUser().getUid();


        binding.btnConfirmOrder.setOnClickListener(v -> {

            db.collection("users").document(uid).collection("cart")
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot qds) {
                            List<CartItem> cartItems;
                            if (!qds.isEmpty()) {
                                cartItems = qds.toObjects(CartItem.class);

                                Order order = new Order();
                                order.setOrderId(String.valueOf(System.currentTimeMillis()));
                                order.setUserId(uid);


                                String name = binding.checkoutInputFullName.getText().toString();
                                String email = binding.checkoutInputEmail.getText().toString();
                                String phone = binding.checkoutInputPhone.getText().toString();
                                String addressLine1 = binding.checkoutInputAddressLine1.getText().toString();
                                String addressLine2 = binding.checkoutInputAddressLine2.getText().toString();
                                String city = binding.checkoutInputCity.getText().toString();
                                String postalCode = binding.checkoutInputPostalCode.getText().toString();

                                Order.Address billingAddress = Order.Address.builder()
                                        .fullName(name)
                                        .email(email)
                                        .phoneNumber(phone)
                                        .addressLine1(addressLine1)
                                        .addressLine2(addressLine2)
                                        .city(city)
                                        .postalCode(postalCode).build();

                                order.setBillingAddress(billingAddress);

                                List<Order.OrderItem> orderItems = new ArrayList<>();


                                for (CartItem cartItem : cartItems) {

                                    List<Order.OrderItem.Attribute> attributes = new ArrayList<>();
                                    for (CartItem.Attribute at : cartItem.getAttributes()) {
                                        Order.OrderItem.Attribute attribute = Order.OrderItem.Attribute.builder()
                                                .name(at.getName())
                                                .value(at.getValue())
                                                .build();

                                        attributes.add(attribute);
                                    }


                                    Order.OrderItem orderItem = Order.OrderItem.builder()
                                            .gameId(cartItem.getGameId())
                                            .unitPrice(0)
                                            .qty(cartItem.getQty())
                                            .attributes(attributes)
                                            .build();

                                    orderItems.add(orderItem);
                                }

                                order.setOrderItems(orderItems);

                                db.collection("orders")
                                        .document().set(order)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(getContext(), "Order Placed Successfully!", Toast.LENGTH_SHORT).show();

                                        });
                            }

                        }
                    });

        });
    }

    private void getCartItems(FireStoreCallback<List<CartItem>> callback) {

        String uid = firebaseAuth.getCurrentUser().getUid();

        db.collection("users").document(uid).collection("cart")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot qds) {
                        if (!qds.isEmpty()) {

                            List<CartItem> cartItems = qds.toObjects(CartItem.class);
                            callback.onCallback(cartItems);

                        }
                    }
                });
    }

    private void getGamesById(List<String> gameIds, FireStoreCallback<Map<String, Game>> callback) {

        Map<String, Game> games = new HashMap();

        if (gameIds == null || gameIds.isEmpty()) {
            callback.onCallback(games);
            return;
        }


        db.collection("games")
                .whereIn("gameId", gameIds)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot qds) {

                        qds.getDocuments().forEach(ds -> {
                            Game game = ds.toObject(Game.class);
                            if (game != null) {
                                games.put(game.getGameId(), game);
                            }
                        });

                        callback.onCallback(games);

                    }
                });

    }

//    private void getSubTotal() {
//
//        List<CartItem> cartItems = getCartItems();
//
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        List<String> gameIds = new ArrayList<>();
//        cartItems.forEach(cartItem -> {
//            gameIds.add(cartItem.getGameId());
//        });
//
//        Map<String, Game> games = getGamesById(gameIds);
//
//        double subTotal = 0;
//
//        for (CartItem cartItem : cartItems) {
//            Game game = games.get(cartItem.getGameId());
//            if (game != null) {
//                subTotal += game.getPrice() * cartItem.getQty();
//            }
//        }
//
//
//
//        int totalQty = 0;
//        for (CartItem item : cartItems) {
//            totalQty += item.getQty();
//        }
//
//        return subTotal;
//    }
}