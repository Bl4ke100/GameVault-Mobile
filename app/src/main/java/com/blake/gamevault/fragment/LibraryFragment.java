package com.blake.gamevault.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blake.gamevault.R;
import com.blake.gamevault.adapter.KeyAdapter;
import com.blake.gamevault.adapter.LibraryAdapter;
import com.blake.gamevault.adapter.ListingAdapter; // Reusing your existing grid adapter
import com.blake.gamevault.databinding.FragmentLibraryBinding; // Make sure this matches your 1st XML name
import com.blake.gamevault.model.Game;
import com.blake.gamevault.model.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class LibraryFragment extends Fragment {

    private FragmentLibraryBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLibraryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        binding.libraryRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        loadLibraryGames();
    }

    private void loadLibraryGames() {
        if (auth.getCurrentUser() == null) return;
        String uid = auth.getCurrentUser().getUid();

        db.collection("users").document(uid).collection("library")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    // 🛑 THE SHIELD
                    if (!isAdded() || binding == null) return;

                    if (querySnapshot.isEmpty()) {
                        binding.libraryRecycler.setVisibility(View.GONE);
                        binding.libraryEmptyState.setVisibility(View.VISIBLE);
                        return;
                    }

                    binding.libraryRecycler.setVisibility(View.VISIBLE);
                    binding.libraryEmptyState.setVisibility(View.GONE);

                    List<String> gameIds = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        gameIds.add(doc.getString("gameId"));
                    }

                    db.collection("games").whereIn("gameId", gameIds).get()
                            .addOnSuccessListener(gameSnapshots -> {

                                // 🛑 THE SHIELD
                                if (!isAdded() || binding == null) return;

                                List<Game> ownedGames = gameSnapshots.toObjects(Game.class);

                                LibraryAdapter adapter = new LibraryAdapter(ownedGames, game -> {
                                    showKeysDialog(game);
                                });
                                binding.libraryRecycler.setAdapter(adapter);
                            });
                });
    }

    private void showKeysDialog(Game game) {
        if (getContext() == null || auth.getCurrentUser() == null) return;
        String uid = auth.getCurrentUser().getUid();

        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_activation_keys);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView titleText = dialog.findViewById(R.id.dialogGameTitle);
        ImageView closeBtn = dialog.findViewById(R.id.dialogClose);
        RecyclerView keysRecycler = dialog.findViewById(R.id.keysRecycler);

        titleText.setText(game.getTitle());
        keysRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        closeBtn.setOnClickListener(v -> dialog.dismiss());

        db.collection("orders")
                .whereEqualTo("userId", uid)
                .whereEqualTo("status", "PAID")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    // 🛑 THE SHIELD (Critical here since Dialogs can leak memory easily)
                    if (!isAdded() || getContext() == null) return;

                    List<KeyAdapter.KeyData> keyDataList = new ArrayList<>();
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
                    int copyCounter = 1;

                    for (Order order : querySnapshot.toObjects(Order.class)) {
                        if (order.getOrderItems() != null) {
                            for (Order.OrderItem item : order.getOrderItems()) {
                                if (item.getGameId().equals(game.getGameId())) {

                                    String platform = "Digital";
                                    if (item.getAttributes() != null) {
                                        for (Order.OrderItem.Attribute attr : item.getAttributes()) {
                                            if ("Platform".equalsIgnoreCase(attr.getName())) {
                                                platform = attr.getValue();
                                                break;
                                            }
                                        }
                                    }

                                    String formattedDate = sdf.format(new Date(order.getOrderDate()));

                                    for (int i = 0; i < item.getQty(); i++) {
                                        String fakeKey = UUID.randomUUID().toString().substring(0, 14).toUpperCase().replace("-", "");
                                        fakeKey = fakeKey.substring(0,4) + "-" + fakeKey.substring(4,8) + "-" + fakeKey.substring(8,12);

                                        keyDataList.add(new KeyAdapter.KeyData(platform, formattedDate, fakeKey, copyCounter));
                                        copyCounter++;
                                    }
                                }
                            }
                        }
                    }

                    KeyAdapter adapter = new KeyAdapter(keyDataList);
                    keysRecycler.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    if (isAdded() && getContext() != null) {
                        Toast.makeText(getContext(), "Failed to load keys.", Toast.LENGTH_SHORT).show();
                    }
                });

        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}