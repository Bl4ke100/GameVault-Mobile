package com.blake.gamevault.fragment;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.blake.gamevault.R;
import com.blake.gamevault.adapter.BannerAdapter;
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

public class HomeFragment extends Fragment implements SensorEventListener {

    private FragmentHomeBinding binding;
    private FirebaseFirestore db;

    // Shake Sensor Variables
    private SensorManager sensorManager;
    private float acceleration = 0f;
    private float currentAcceleration = 0f;
    private float lastAcceleration = 0f;
    private List<Game> allGamesList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // Initialize Sensors
        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        lastAcceleration = SensorManager.GRAVITY_EARTH;
        currentAcceleration = SensorManager.GRAVITY_EARTH;
        acceleration = 0.00f;

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        com.google.firebase.storage.FirebaseStorage storage = com.google.firebase.storage.FirebaseStorage.getInstance();

        // 1. Fetch Top Banners
        storage.getReference().child("images/banners").listAll().addOnSuccessListener(listResult -> {
            List<String> urls = new ArrayList<>();
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
            List<String> urls = new ArrayList<>();
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

        loadFeaturedGames();
        loadCategories();
        loadNewArrivals();
    }

    // ===== SHAKE LOGIC =====
    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        lastAcceleration = currentAcceleration;
        currentAcceleration = (float) Math.sqrt(x * x + y * y + z * z);
        float delta = currentAcceleration - lastAcceleration;
        acceleration = acceleration * 0.9f + delta;

        if (acceleration > 10) { // Sensitivity threshold
            pickRandomGame();
            acceleration = 0;
        }
    }

    private void pickRandomGame() {
        if (allGamesList != null && !allGamesList.isEmpty()) {
            int randomIndex = new java.util.Random().nextInt(allGamesList.size());
            Game randomGame = allGamesList.get(randomIndex);

            new AlertDialog.Builder(requireContext())
                    .setTitle("🎲 Feeling Lucky?")
                    .setMessage("You should try: " + randomGame.getTitle())
                    .setPositiveButton("View Details", (dialog, which) -> {
                        // Pass both IDs here
                        navigateToDetail(randomGame.getGameId(), randomGame.getCategoryId());
                    })
                    .setNegativeButton("Shake Again", null)
                    .show();
        }
    }

    private void navigateToDetail(String gameId, String catId) {
        GameDetailFragment detailFragment = new GameDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("gameId", gameId);
        bundle.putString("catId", catId); // Added the category ID here!
        detailFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, detailFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    // ===== FEATURED GAMES =====
    private void loadFeaturedGames() {
        db.collection("games")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) return;

                    List<Game> games = querySnapshot.toObjects(Game.class);
                    this.allGamesList = games; // Store all games for the shake feature

                    List<Game> featured = new ArrayList<>(games);
                    java.util.Collections.shuffle(featured);
                    List<Game> randomGames = featured.subList(0, Math.min(featured.size(), 10));

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

    // ===== HELPER: Add Game Card =====
    private void addGameCard(LinearLayout row, Game game) {
        View cardView = LayoutInflater.from(getContext())
                .inflate(R.layout.item_home_game, row, false);

        ImageView image = cardView.findViewById(R.id.homeGameImage);
        TextView price = cardView.findViewById(R.id.homeGamePrice);
        TextView title = cardView.findViewById(R.id.homeGameTitle);

        title.setText(game.getTitle());
        price.setText("LKR " + game.getPrice() + "0");

        // --- NEW IMAGE LOAD METHOD ---
        String posterName = game.getPosterUrl();
        if (posterName == null || posterName.isEmpty()) {
            posterName = "poster.png";
        }

        com.google.firebase.storage.StorageReference storageRef = com.google.firebase.storage.FirebaseStorage.getInstance().getReference()
                .child("images")
                .child("game-images")
                .child(game.getGameId())
                .child(posterName);

        // Set placeholder immediately
        image.setImageResource(R.drawable.placeholder_game);

        // Fetch URL and load with Glide
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            if (getContext() != null) { // Prevents crash if fragment closes before image loads
                Glide.with(requireContext())
                        .load(uri)
                        .placeholder(R.drawable.placeholder_game)
                        .error(R.drawable.placeholder_game)
                        .into(image);
            }
        }).addOnFailureListener(e -> {
            android.util.Log.e("HomeFragment", "Error loading image for " + game.getGameId() + ": " + e.getMessage());
        });

        cardView.setOnClickListener(v -> navigateToDetail(game.getGameId(), game.getCategoryId()));

        row.addView(cardView);
    }

    // ===== BANNER SLIDER SETUP =====
    private void setupSlider(ViewPager2 slider, com.tbuonomo.viewpagerdotsindicator.DotsIndicator dots, List<String> urls) {
        BannerAdapter adapter = new BannerAdapter(urls);
        slider.setAdapter(adapter);
        dots.attachTo(slider);

        android.os.Handler handler = new android.os.Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = slider.getCurrentItem();
                if (urls.size() > 0) {
                    int nextItem = (currentItem + 1) % urls.size();
                    slider.setCurrentItem(nextItem, true);
                }
                handler.postDelayed(this, 3500);
            }
        };
        handler.postDelayed(runnable, 3500);

        slider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    handler.removeCallbacks(runnable);
                } else if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    handler.removeCallbacks(runnable);
                    handler.postDelayed(runnable, 3500);
                }
            }
        });
    }

    // Lifecycle Management
    @Override
    public void onResume() {
        super.onResume();
        if (sensorManager != null) {
            sensorManager.registerListener(this,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}