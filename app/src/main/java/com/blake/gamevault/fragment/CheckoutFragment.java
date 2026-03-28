package com.blake.gamevault.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.LocusId;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.StatusResponse;

public class CheckoutFragment extends Fragment {

    private FragmentCheckoutBinding binding;

    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private double subTotal;
    private boolean paymentActive = false;


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
                subTotal = 0;
                int itemCount = 0; // 1. Initialize to 0

                for (CartItem cartItem : cartItems) {
                    Game game = data.get(cartItem.getGameId());

                    if (game != null) {
                        subTotal += game.getPrice() * cartItem.getQty();
                        itemCount += cartItem.getQty();
                    }
                }

                binding.checkoutItemCount.setText(String.valueOf(itemCount));
                binding.checkoutTotalAmount.setText(String.format(Locale.US, "LKR %,.2f", subTotal));
                paymentActive = true;
            });

        });


        String uid = firebaseAuth.getCurrentUser().getUid();

        binding.btnConfirmOrder.setOnClickListener(v -> {

            if (validateInputs() && paymentActive) {

                InitRequest req = new InitRequest();
                req.setSandBox(true);

                req.setMerchantId("1230668");
                req.setMerchantSecret("ODg0MTAxMTEyNzI4MzQwMDE1MzkyMjk5NDQzNTg4NjMwMjAx");
                req.setCurrency("LKR");
                req.setAmount(subTotal);
                req.setOrderId("GVOD 001");
                req.setItemsDescription(" ");

                req.getCustomer().setFirstName(binding.checkoutInputFullName.getText().toString());
                req.getCustomer().setLastName(binding.checkoutInputFullName.getText().toString());
                req.getCustomer().setEmail(binding.checkoutInputEmail.getText().toString());
                req.getCustomer().setPhone(binding.checkoutInputPhone.getText().toString());
                req.getCustomer().getAddress().setAddress(binding.checkoutInputAddressLine1.getText().toString());
                req.getCustomer().getAddress().setCity(binding.checkoutInputCity.getText().toString());
                req.getCustomer().getAddress().setCountry("Sri Lanka");

                //req.setNotifyUrl("https://gamevault.requestcatcher.com/");

                Intent intent = new Intent(getActivity(), PHMainActivity.class);
                intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);

                payhereLauncher.launch(intent);
            }


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

    private final ActivityResultLauncher<Intent> payhereLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {

                    Intent data = result.getData();
                    if (data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
                        PHResponse<StatusResponse> response =
                                (PHResponse<StatusResponse>) data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);

                        if (response != null && response.isSuccess()) {
                            StatusResponse statusResponse = response.getData();

                            saveOrder(statusResponse);
                            Log.i("PayHere", "Payment Successful");
                            Toast.makeText(getContext(), "Payment Successful!", Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    Toast.makeText(getContext(), "Payment Failed!", Toast.LENGTH_SHORT).show();
                }
            });

    private void saveOrder(StatusResponse statusResponse) {
        getCartItems(cartItems -> {

            String uid = firebaseAuth.getCurrentUser().getUid();

            Order order = new Order();
            order.setOrderId(String.valueOf(System.currentTimeMillis()));
            order.setUserId(uid);
            order.setTotalAmount(subTotal);
            order.setStatus("PAID");
            order.setOrderDate(Timestamp.now().toDate().getTime());


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

            ArrayList<String> gameIds = new ArrayList<>();
            for (CartItem cartItem : cartItems) {
                gameIds.add(cartItem.getGameId());
            }

            List<Order.OrderItem> orderItems = new ArrayList<>();

            getGamesById(gameIds, data -> {
                for (CartItem cartItem : cartItems) {

                    Game game = data.get(cartItem.getGameId());
                    if (game != null) {

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
                                .unitPrice(game.getPrice())
                                .qty(cartItem.getQty())
                                .attributes(attributes)
                                .build();

                        orderItems.add(orderItem);

                        order.setOrderItems(orderItems);

                        db.collection("orders")
                                .document().set(order)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Order Placed Successfully!", Toast.LENGTH_SHORT).show();

                                    db.collection("users").document(uid)
                                            .collection("cart").
                                            get()
                                            .addOnSuccessListener(qsd -> {
                                                qsd.getDocuments().forEach(ds -> {
                                                    ds.getReference().delete();
                                                });
                                            });

                                    getParentFragmentManager().beginTransaction()
                                            .replace(R.id.fragmentContainer, new ShopFragment())
                                            .commit();


                                });
                    }

                }
            });

        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        String name = binding.checkoutInputFullName.getText().toString().trim();
        String email = binding.checkoutInputEmail.getText().toString().trim();
        String phone = binding.checkoutInputPhone.getText().toString().trim();
        String address = binding.checkoutInputAddressLine1.getText().toString().trim();
        String city = binding.checkoutInputCity.getText().toString().trim();
        String postalCode = binding.checkoutInputPostalCode.getText().toString().trim();

        if (name.isEmpty()) {
            binding.checkoutInputFullName.setError("Full Name is required");
            isValid = false;
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.checkoutInputEmail.setError("Valid Email is required");
            isValid = false;
        }
        if (phone.isEmpty() || phone.length() < 10) {
            binding.checkoutInputPhone.setError("Valid Phone Number is required");
            isValid = false;
        }
        if (address.isEmpty()) {
            binding.checkoutInputAddressLine1.setError("Address is required");
            isValid = false;
        }
        if (city.isEmpty()) {
            binding.checkoutInputCity.setError("City is required");
            isValid = false;
        }
        if (postalCode.isEmpty()) {
            binding.checkoutInputPostalCode.setError("Postal Code is required");
            isValid = false;
        }

        return isValid;
    }

}