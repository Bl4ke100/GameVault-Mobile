package com.blake.gamevault.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blake.gamevault.R;
import com.blake.gamevault.adapter.OrderAdapter;
import com.blake.gamevault.databinding.FragmentOrdersBinding;
import com.blake.gamevault.model.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class OrdersFragment extends Fragment {

    private FragmentOrdersBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOrdersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        binding.purchaseHistoryRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        loadPurchaseHistory();
    }

    private void loadPurchaseHistory() {
        if (firebaseAuth.getCurrentUser() == null) return;

        String uid = firebaseAuth.getCurrentUser().getUid();

        // Fetch orders for this user, newest first
        db.collection("orders")
                .whereEqualTo("userId", uid)
                .orderBy("orderDate", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        binding.purchaseHistoryRecycler.setVisibility(View.GONE);
                        binding.emptyState.setVisibility(View.VISIBLE);
                        return;
                    }

                    binding.purchaseHistoryRecycler.setVisibility(View.VISIBLE);
                    binding.emptyState.setVisibility(View.GONE);

                    List<Order> orders = querySnapshot.toObjects(Order.class);

                    OrderAdapter adapter = new OrderAdapter(orders, order -> {
                        // Handle View Details click
                        Toast.makeText(getContext(), "Clicked Order: " + order.getOrderId(), Toast.LENGTH_SHORT).show();

                        // You can navigate to an OrderDetailFragment here later
                    });

                    binding.purchaseHistoryRecycler.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load history", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}