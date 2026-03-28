package com.blake.gamevault.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blake.gamevault.R;
import com.blake.gamevault.activity.LoginActivity;
import com.blake.gamevault.activity.MainActivity;
import com.blake.gamevault.adapter.GameSliderAdapter;
import com.blake.gamevault.adapter.SimilarGameAdapter;
import com.blake.gamevault.databinding.FragmentGameDetailBinding;
import com.blake.gamevault.model.CartItem;
import com.blake.gamevault.model.Game;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GameDetailFragment extends Fragment {

    private FragmentGameDetailBinding binding;
    private String gameId;
    private String catId;
    private int qty = 1;
    private double availableQty;
    private Map<String, ChipGroup> attributeGroups = new HashMap<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            gameId = getArguments().getString("gameId");
            catId = getArguments().getString("catId");
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGameDetailBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().findViewById(R.id.bottomNav).setVisibility(View.GONE);
        getActivity().findViewById(R.id.toolBar).setVisibility(View.GONE);

        binding.btnBack.setOnClickListener(v -> {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });

        getActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().popBackStack();
                getActivity().findViewById(R.id.bottomNav).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.toolBar).setVisibility(View.VISIBLE);
            }
        });


        //load game details

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("games")
                .whereEqualTo("gameId", gameId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot qds) {
                        if (!qds.isEmpty()) {
                            Game game = qds.getDocuments().get(0).toObject(Game.class);

                            GameSliderAdapter adapter = new GameSliderAdapter(game.getImages());
                            binding.gameImageSlider.setAdapter(adapter);
                            binding.gameName.setText(game.getTitle());
                            binding.gamePrice.setText(String.valueOf("LKR " + game.getPrice() + "0"));
                            binding.detailReleaseYear.setText(String.valueOf(game.getReleasedYear()));
                            binding.detailDescription.setText(game.getDescription());
                            binding.detailStockCount.setText(game.getStock() + " Keys Available");
                            binding.detailRating.setRating(game.getRating());
                            binding.dotsIndicator.attachTo(binding.gameImageSlider);

                            availableQty = game.getStock();

                            if (game.getAttributes() != null) {
                                game.getAttributes().forEach(attribute -> {
                                    renderAttribute(attribute, (ViewGroup) binding.detailPlatformContainer);
                                });

                            }


                            FirebaseFirestore.getInstance().collection("developers")
                                    .whereEqualTo("developerId", game.getDeveloperId())
                                    .get()
                                    .addOnSuccessListener(querySnapshot -> {
                                        Log.d("TAG", "developerId from game: " + game.getDeveloperId());
                                        Log.d("TAG", "query result size: " + querySnapshot.size());
                                        if (!querySnapshot.isEmpty()) {
                                            String developerName = querySnapshot.getDocuments().get(0).getString("name");
                                            Log.d("TAG", "developer name: " + developerName);
                                            binding.detailDeveloper.setText(developerName);
                                        }
                                    })
                                    .addOnFailureListener(e -> Log.e("TAG", "Error: " + e.getMessage()));

                            FirebaseFirestore.getInstance().collection("categories")
                                    .whereEqualTo("catId", game.getCategoryId())
                                    .get()
                                    .addOnSuccessListener(querySnapshot -> {
                                        if (!querySnapshot.isEmpty()) {
                                            String categoryName = querySnapshot.getDocuments().get(0).getString("name");
                                            binding.detailGenre.setText(categoryName);
                                        }
                                    });

                        }

                    }
                });

        binding.btnDecrease.setOnClickListener(v -> {
            if (qty > 1) {
                qty--;
                binding.detailQuantity.setText(String.valueOf(qty));
            }

        });

        binding.btnIncrease.setOnClickListener(v -> {
            if (qty < availableQty) {
                qty++;
                binding.detailQuantity.setText(String.valueOf(qty));
            }
        });

        loadSimilarProducts();

        binding.btnAddToCart.setOnClickListener(v -> {

            if (!validateSelections()) {
                return;
            }

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

            if (firebaseAuth.getCurrentUser() == null){
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            } else {
                List<CartItem.Attribute> attributes = getFinalSelection();
                CartItem cartItem = new CartItem(gameId, qty, attributes);
                String uid = firebaseAuth.getCurrentUser().getUid();

                CollectionReference cartRef = db.collection("users").document(uid).collection("cart");

                cartRef.whereEqualTo("gameId", gameId).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    boolean matchFound = false;

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        CartItem existingItem = doc.toObject(CartItem.class);

                        if (existingItem != null && existingItem.getAttributes().equals(attributes)) {

                            // 3. Exact match found! Add to the existing quantity.
                            int newQty = existingItem.getQty() + qty;
                            doc.getReference().update("qty", newQty)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(getContext(), "Cart Quantity Updated!", Toast.LENGTH_SHORT).show();
                                    });
                            matchFound = true;
                            break;
                        }
                    }

                    if (!matchFound) {
                        cartRef.document().set(cartItem)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(getContext(), "Game Added To Cart!", Toast.LENGTH_SHORT).show();
                                    Log.i("Added to cart", cartItem.toString());
                                });
                    }
                });
            }
        });
    }

    private void loadSimilarProducts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("games")
                .whereEqualTo("categoryId", catId) // Assuming catId is available
                .get()
                .addOnSuccessListener(qds -> {
                    if (qds != null && !qds.isEmpty()) {
                        List<Game> games = new ArrayList<>();

                        for (DocumentSnapshot doc : qds.getDocuments()) {
                            Game game = doc.toObject(Game.class);
                            if (game != null && !game.getGameId().equals(gameId)) {
                                games.add(game);
                            }
                        }

                        if (!games.isEmpty()) {
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                                    LinearLayoutManager.HORIZONTAL, false);
                            binding.detailSimilarProductsSection.recyclerSimilarGamesView.setLayoutManager(layoutManager);

                            SimilarGameAdapter adapter = new SimilarGameAdapter(games, game -> {
                                Bundle bundle = new Bundle();
                                bundle.putString("gameId", game.getGameId());
                                bundle.putString("catId", game.getCategoryId());

                                GameDetailFragment gameDetailFragment = new GameDetailFragment();
                                gameDetailFragment.setArguments(bundle);

                                getParentFragmentManager().beginTransaction()
                                        .replace(R.id.fragmentContainer, gameDetailFragment)
                                        .addToBackStack(null)
                                        .commit();
                            });

                            binding.detailSimilarProductsSection.similarProductsSectionTitle.setText("You Might Also Like");
                            binding.detailSimilarProductsSection.recyclerSimilarGamesView.setAdapter(adapter);
                        }
                    }
                });
    }

    private boolean validateSelections() {
        for (Map.Entry<String, ChipGroup> entry : attributeGroups.entrySet()) {
            if (entry.getValue().getCheckedChipId() == View.NO_ID) {
                Toast.makeText(getContext(), "Please select a " + entry.getKey(), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void renderAttribute(Game.Attribute attribute, ViewGroup container) {

        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        rowParams.bottomMargin = 8;
        row.setLayoutParams(rowParams);

        TextView label = new TextView(getContext());
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        labelParams.gravity = Gravity.CENTER_VERTICAL;
        label.setLayoutParams(labelParams);
        label.setText(attribute.getName().toUpperCase());
        label.setTextColor(resolveThemeColor(com.google.android.material.R.attr.colorOnSurface));
        label.setTextSize(13);
        label.setLetterSpacing(0.12f);
        row.addView(label);

        View spacer = new View(getContext());
        LinearLayout.LayoutParams spacerParams = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
        );
        spacer.setLayoutParams(spacerParams);
        row.addView(spacer);

        ChipGroup group = new ChipGroup(getContext());


        group.setSelectionRequired(true);
        group.setSingleSelection(true);

        LinearLayout.LayoutParams groupParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        groupParams.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        group.setLayoutParams(groupParams);

        attribute.getValues().forEach(value -> {
            Chip chip = new Chip(getContext());
            chip.setCheckable(true);
            chip.setText(value);
            group.addView(chip);
        });

        row.addView(group);
        container.addView(row);

        attributeGroups.put(attribute.getName(), group);
    }

    private List<CartItem.Attribute> getFinalSelection() {

        StringBuilder result = new StringBuilder("Selected: \n");

        List<CartItem.Attribute> attributes = new ArrayList<>();

        for (Map.Entry<String, ChipGroup> entry : attributeGroups.entrySet()) {
            String attributeName = entry.getKey();
            ChipGroup chipGroup = entry.getValue();

            int checkedChipId = chipGroup.getCheckedChipId();

            if (checkedChipId != -1) {
                Chip chip = getView().findViewById(checkedChipId);
                String value = chip.getText().toString();

                attributes.add(new CartItem.Attribute(attributeName, value));

                result.append(attributeName).append(": ").append(value);
            }
        }

        Log.i("Final Result", result.toString());
        return attributes;
    }

    private int resolveThemeColor(int attr) {
        android.util.TypedValue typedValue = new android.util.TypedValue();
        requireContext().getTheme().resolveAttribute(attr, typedValue, true);
        return typedValue.data;
    }
}