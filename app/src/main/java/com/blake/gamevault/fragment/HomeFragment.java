package com.blake.gamevault.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.blake.gamevault.R;
import com.blake.gamevault.adapter.CategoryAdapter;
import com.blake.gamevault.adapter.GameSliderAdapter;
import com.blake.gamevault.databinding.FragmentHomeBinding;
import com.blake.gamevault.model.Category;
import com.blake.gamevault.model.Game;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        com.google.firebase.storage.FirebaseStorage storage = com.google.firebase.storage.FirebaseStorage.getInstance();

        // 1. Fetch Top Banners
        storage.getReference().child("images/banners").listAll().addOnSuccessListener(listResult -> {
            java.util.List<String> urls = new java.util.ArrayList<>();
            int total = listResult.getItems().size();
            if (total == 0) return;
            for (com.google.firebase.storage.StorageReference item : listResult.getItems()) {
                item.getDownloadUrl().addOnSuccessListener(uri -> {
                    urls.add(uri.toString());
                    if (urls.size() == total) {
                        setupSlider(binding.homeBannerSlider, binding.homeBannerDots, urls);
                    }
                });
            }
        });

        // 2. Fetch New Arrivals Banners
        storage.getReference().child("images/new-arrivals").listAll().addOnSuccessListener(listResult -> {
            java.util.List<String> urls = new java.util.ArrayList<>();
            int total = listResult.getItems().size();
            if (total == 0) return;
            for (com.google.firebase.storage.StorageReference item : listResult.getItems()) {
                item.getDownloadUrl().addOnSuccessListener(uri -> {
                    urls.add(uri.toString());
                    if (urls.size() == total) {
                        setupSlider(binding.newArrivalsBannerSlider, binding.newArrivalsBannerDots, urls);
                    }
                });
            }
        });

        // 3. Load the Game Cards & Categories
        loadFeaturedGames();
        loadCategories();
        loadNewArrivals(); // <-- ADDED THIS BACK IN!
    }



    // ===== BANNER SLIDER =====
    private void loadBannerSlider() {
        db.collection("games")
                .whereEqualTo("featured", true)
                .limit(5)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) return;

                    List<String> bannerUrls = new ArrayList<>();
                    for (Game game : querySnapshot.toObjects(Game.class)) {
                        if (game.getPosterUrl() != null && !game.getPosterUrl().isEmpty()) {
                            bannerUrls.add(game.getPosterUrl());
                        }
                    }

                    GameSliderAdapter sliderAdapter = new GameSliderAdapter(bannerUrls);
                    binding.homeBannerSlider.setAdapter(sliderAdapter);

                    setupBannerDots(bannerUrls.size());

                    binding.homeBannerSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            updateDots(position, bannerUrls.size());
                        }
                    });
                });
    }

    private void setupBannerDots(int count) {
        binding.homeBannerDots.removeAllViews();
        for (int i = 0; i < count; i++) {
            View dot = new View(getContext());
            int size = dpToPx(8);
            int margin = dpToPx(4);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins(margin, 0, margin, 0);
            dot.setLayoutParams(params);
            dot.setBackgroundResource(i == 0 ? R.drawable.dot_active : R.drawable.dot_inactive);
            binding.homeBannerDots.addView(dot);
        }
    }

    private void updateDots(int selectedIndex, int count) {
        for (int i = 0; i < count; i++) {
            View dot = binding.homeBannerDots.getChildAt(i);
            if (dot != null) {
                dot.setBackgroundResource(i == selectedIndex ? R.drawable.dot_active : R.drawable.dot_inactive);
            }
        }
    }

    // ===== FEATURED GAMES =====
    private void loadFeaturedGames() {
        db.collection("games")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) return;

                    List<Game> allGames = querySnapshot.toObjects(Game.class);

                    // Shuffle the entire list to randomize the order
                    java.util.Collections.shuffle(allGames);

                    // Safely grab up to 10 games (prevents crashing if you have less than 10)
                    List<Game> randomGames = allGames.subList(0, Math.min(allGames.size(), 10));

                    binding.homeFeaturedRow.removeAllViews();
                    for (Game game : randomGames) {
                        addGameCard(binding.homeFeaturedRow, game);
                    }
                });

        binding.homeFeaturedSeeAll.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new GamesFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }

    // ===== NEW ARRIVALS =====
    private void loadNewArrivals() {
        db.collection("games")
                .orderBy("releasedYear", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) return;
                    List<Game> games = querySnapshot.toObjects(Game.class);
                    binding.homeNewArrivalsRow.removeAllViews();
                    for (Game game : games) {
                        addGameCard(binding.homeNewArrivalsRow, game);
                    }
                });

        binding.homeNewArrivalsSeeAll.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new GamesFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }

    // ===== CATEGORIES =====
    private void loadCategories() {
        db.collection("categories")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) return;
                    List<Category> categories = querySnapshot.toObjects(Category.class);
                    CategoryAdapter adapter = new CategoryAdapter(categories, category -> {
                        Bundle bundle = new Bundle();
                        bundle.putString("catId", category.getCatId());

                        GamesFragment gamesFragment = new GamesFragment();
                        gamesFragment.setArguments(bundle);

                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.fragmentContainer, gamesFragment)
                                .addToBackStack(null)
                                .commit();
                    });
                    binding.homeCategoryGrid.setLayoutManager(new GridLayoutManager(getContext(), 3));
                    binding.homeCategoryGrid.setAdapter(adapter);
                });
    }



    // ===== HELPER: Add Game Card to horizontal row =====
    private void addGameCard(LinearLayout row, Game game) {
        View cardView = LayoutInflater.from(getContext())
                .inflate(R.layout.item_home_game, row, false);

        ImageView image = cardView.findViewById(R.id.homeGameImage);
        TextView price = cardView.findViewById(R.id.homeGamePrice);
        TextView title = cardView.findViewById(R.id.homeGameTitle);

        title.setText(game.getTitle());
        price.setText("LKR " + game.getPrice());

        Glide.with(requireContext())
                .load(game.getPosterUrl())
                .into(image);

        cardView.setOnClickListener(v -> {
            GameDetailFragment detailFragment = new GameDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putString("gameId", game.getGameId());
            detailFragment.setArguments(bundle);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });

        row.addView(cardView);
    }


    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupSlider(androidx.viewpager2.widget.ViewPager2 slider, com.tbuonomo.viewpagerdotsindicator.DotsIndicator dots, java.util.List<String> urls) {
        // Set Adapter
        com.blake.gamevault.adapter.BannerAdapter adapter = new com.blake.gamevault.adapter.BannerAdapter(urls);
        slider.setAdapter(adapter);

        // Attach DotsIndicator
        dots.attachTo(slider);

        // Auto-Scroll Logic
        android.os.Handler handler = new android.os.Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = slider.getCurrentItem();
                if (urls.size() > 0) {
                    int nextItem = (currentItem + 1) % urls.size();
                    slider.setCurrentItem(nextItem, true);
                }
                handler.postDelayed(this, 3500); // 3.5 seconds per slide
            }
        };

        handler.postDelayed(runnable, 3500);

        // Pause Auto-Scroll when user is manually swiping
        slider.registerOnPageChangeCallback(new androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_DRAGGING) {
                    handler.removeCallbacks(runnable);
                } else if (state == androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE) {
                    handler.removeCallbacks(runnable);
                    handler.postDelayed(runnable, 3500);
                }
            }
        });
    }
}