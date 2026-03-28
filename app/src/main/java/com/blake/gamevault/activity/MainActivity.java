package com.blake.gamevault.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.blake.gamevault.R;
import com.blake.gamevault.databinding.ActivityMainBinding;
import com.blake.gamevault.databinding.SideNavHeaderBinding;
import com.blake.gamevault.fragment.CartFragment;
import com.blake.gamevault.fragment.HomeFragment;
import com.blake.gamevault.fragment.LibraryFragment;
import com.blake.gamevault.fragment.MessageFragment;
import com.blake.gamevault.fragment.OrdersFragment;
import com.blake.gamevault.fragment.ProfileFragment;
import com.blake.gamevault.fragment.SettingsFragment;
import com.blake.gamevault.fragment.ShopFragment;
import com.blake.gamevault.fragment.WishlistFragment;
import com.blake.gamevault.model.User;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NavigationBarView.OnItemSelectedListener {

    private ActivityMainBinding binding;
    private SideNavHeaderBinding sideNavHeaderBinding;
    private DrawerLayout drawerLayout;
    private MaterialToolbar toolbar;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;


    EditText searchTextInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        saveDeviceToken();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        View headerView = binding.sideNav.getHeaderView(0);

        sideNavHeaderBinding = SideNavHeaderBinding.bind(headerView);

        drawerLayout = binding.drawerLayout;
        toolbar = binding.toolBar;
        navigationView = binding.sideNav;
        bottomNavigationView = binding.bottomNav;

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    finish();
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigationView.setOnItemSelectedListener(this);

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //Load user data
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            firebaseFirestore.collection("users").document(currentUser.getUid()).get()
                    .addOnSuccessListener(ds -> {

                        if (ds.exists()) {
                            User user = ds.toObject(User.class);
                            sideNavHeaderBinding.headerUsername.setText(user.getUsername());
                            sideNavHeaderBinding.headerEmail.setText(user.getEmail());

                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            storage.getReference("/images/profile-images/" + user.getProfilePicUrl()).getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        Glide.with(MainActivity.this)
                                                .load(uri)
                                                .circleCrop()
                                                .into(sideNavHeaderBinding.headerPfp);

                                    });

                        }

                    });

            navigationView.getMenu().findItem(R.id.side_nav_profile).setVisible(true);
            navigationView.getMenu().findItem(R.id.side_nav_order).setVisible(true);
            navigationView.getMenu().findItem(R.id.side_nav_wishlist).setVisible(true);
            navigationView.getMenu().findItem(R.id.side_nav_login).setVisible(false);
            navigationView.getMenu().findItem(R.id.side_nav_logout).setVisible(true);


            sideNavHeaderBinding.headerPfp.setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                activityResultLauncher.launch(intent);
            });

        }

        searchTextInput = binding.searchTextInput;

        // Setup the Dropdown
        android.widget.ListPopupWindow listPopupWindow = new android.widget.ListPopupWindow(this);
        listPopupWindow.setAnchorView(searchTextInput);
        java.util.List<com.blake.gamevault.model.Game> searchResults = new java.util.ArrayList<>();
        android.widget.ArrayAdapter<String> popupAdapter = new android.widget.ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new java.util.ArrayList<>());
        listPopupWindow.setAdapter(popupAdapter);

        // Handle Clicks
        listPopupWindow.setOnItemClickListener((parent, view, position, id) -> {
            com.blake.gamevault.model.Game game = searchResults.get(position);
            listPopupWindow.dismiss();
            searchTextInput.clearFocus();

            Bundle bundle = new Bundle();
            bundle.putString("gameId", game.getGameId());
            bundle.putString("catId", game.getCategoryId());

            com.blake.gamevault.fragment.GameDetailFragment detailFragment = new com.blake.gamevault.fragment.GameDetailFragment();
            detailFragment.setArguments(bundle);
            loadFragment(detailFragment);
        });

        // Search logic
        searchTextInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    listPopupWindow.dismiss();
                    return;
                }

                firebaseFirestore.collection("games")
                        .orderBy("title")
                        .startAt(query)
                        .endAt(query + "\uf8ff")
                        .limit(5)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            searchResults.clear();
                            popupAdapter.clear();

                            for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                                com.blake.gamevault.model.Game g = doc.toObject(com.blake.gamevault.model.Game.class);
                                if (g != null) {
                                    searchResults.add(g);
                                    popupAdapter.add(g.getTitle());
                                }
                            }

                            if (!searchResults.isEmpty()) {
                                listPopupWindow.show();
                            } else {
                                listPopupWindow.dismiss();
                            }
                        });
            }
        });

    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Uri uri = result.getData().getData();
                    Log.i("MainActivity", "Image URI:" + uri.getPath());

                    Glide.with(MainActivity.this)
                            .load(uri)
                            .circleCrop()
                            .into(sideNavHeaderBinding.headerPfp);

                    String imageId = UUID.randomUUID().toString();

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference imageReference = storage.getReference("/images/profile-images/").child(imageId);

                    imageReference.putFile(uri)
                            .addOnSuccessListener(taskSnapshot -> {

                                firebaseFirestore.collection("users")
                                        .document(firebaseAuth.getUid())
                                        .update("profilePicUrl", imageId)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(MainActivity.this, "Profile Picture Updated", Toast.LENGTH_SHORT).show();
                                        });
                            });


                }
            }
    );

    private void setStatusBarColor() {
        int currentNightMode = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;

        // Get the window controller
        androidx.core.view.WindowInsetsControllerCompat windowController =
                androidx.core.view.WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());

        if (currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_NO) {
            // Light mode: Make status bar white and icons dark
            getWindow().setStatusBarColor(getResources().getColor(R.color.pure_white));
            windowController.setAppearanceLightStatusBars(true);
        } else {
            // Dark mode: Make status bar black and icons light
            getWindow().setStatusBarColor(getResources().getColor(R.color.void_black));
            windowController.setAppearanceLightStatusBars(false);
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        navigationView.setCheckedItem(-1);

        if (itemId == R.id.side_nav_profile) {

            loadFragment(new ProfileFragment());
            navigationView.getMenu().findItem(R.id.side_nav_profile).setChecked(true);
            clearBottomNavSelection();

        } else if (itemId == R.id.side_nav_order) {

            loadFragment(new OrdersFragment());
            navigationView.getMenu().findItem(R.id.side_nav_order).setChecked(true);
            clearBottomNavSelection();

        } else if (itemId == R.id.side_nav_wishlist) {

            loadFragment(new WishlistFragment());
            navigationView.getMenu().findItem(R.id.side_nav_wishlist).setChecked(true);
            clearBottomNavSelection();

        } else if (itemId == R.id.side_nav_settings) {

            loadFragment(new SettingsFragment());
            navigationView.getMenu().findItem(R.id.side_nav_settings).setChecked(true);
            clearBottomNavSelection();

        } else if (itemId == R.id.side_nav_login) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);

        } else if (itemId == R.id.side_nav_logout) {

            firebaseAuth.signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        } else if (itemId == R.id.bottom_nav_home) {

            loadFragment(new HomeFragment());
            restoreBottomNavSelection();
            bottomNavigationView.getMenu().findItem(R.id.bottom_nav_home).setChecked(true);
            clearSideNavSelection();

        } else if (itemId == R.id.bottom_nav_shop) {

            loadFragment(new ShopFragment());
            restoreBottomNavSelection();
            bottomNavigationView.getMenu().findItem(R.id.bottom_nav_shop).setChecked(true);
            clearSideNavSelection();

        } else if (itemId == R.id.bottom_nav_cart) {

            if (firebaseAuth.getCurrentUser() == null) {
                Toast.makeText(MainActivity.this, "Please Login", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                loadFragment(new CartFragment());
                restoreBottomNavSelection();
                bottomNavigationView.getMenu().findItem(R.id.bottom_nav_cart).setChecked(true);
                clearSideNavSelection();
            }

        } else if (itemId == R.id.bottom_nav_library) {

            if (firebaseAuth.getCurrentUser() == null) {
                Toast.makeText(MainActivity.this, "Please Login", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                loadFragment(new LibraryFragment());
                restoreBottomNavSelection();
                bottomNavigationView.getMenu().findItem(R.id.bottom_nav_library).setChecked(true);
                clearSideNavSelection();
            }

        }


        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }

        return true;
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();
    }

    private void clearBottomNavSelection() {
        bottomNavigationView.setItemActiveIndicatorEnabled(false);
        Menu menu = bottomNavigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setCheckable(false);
        }
    }

    private void restoreBottomNavSelection() {
        bottomNavigationView.setItemActiveIndicatorEnabled(true);
        Menu menu = bottomNavigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setCheckable(true);
        }
    }

    private void clearSideNavSelection() {
        Menu menu = navigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setChecked(false);
        }
    }

//    private void fixGameImages() {
//        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
//
//        db.collection("games").get().addOnSuccessListener(queryDocumentSnapshots -> {
//            com.google.firebase.firestore.WriteBatch batch = db.batch();
//
//            java.util.List<String> newImagesArray = java.util.Arrays.asList(
//                    "image1.png", "image2.png", "image3.png", "image4.png"
//            );
//
//            for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
//                batch.update(doc.getReference(), "images", newImagesArray);
//
//                batch.update(doc.getReference(), "posterUrl", "poster.png");
//            }
//
//            batch.commit().addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    android.util.Log.i("FIX_SCRIPT", "SUCCESS! All games updated to new images array and poster.png.");
//                } else {
//                    android.util.Log.e("FIX_SCRIPT", "Failed to update games", task.getException());
//                }
//            });
//        });
//    }

    private void saveDeviceToken() {
        com.google.firebase.auth.FirebaseAuth auth = com.google.firebase.auth.FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) return; // Only save if someone is logged in

        com.google.firebase.messaging.FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        android.util.Log.w("FCM", "Fetching FCM token failed", task.getException());
                        return;
                    }

                    // Get the unique device token
                    String token = task.getResult();
                    String uid = auth.getCurrentUser().getUid();

                    // Package it up
                    java.util.Map<String, Object> tokenData = new java.util.HashMap<>();
                    tokenData.put("fcmToken", token);

                    // Save it to their user document in Firestore
                    // NOTE: We use merge() so it doesn't accidentally delete their name/email!
                    com.google.firebase.firestore.FirebaseFirestore.getInstance()
                            .collection("users").document(uid)
                            .set(tokenData, com.google.firebase.firestore.SetOptions.merge())
                            .addOnSuccessListener(aVoid -> android.util.Log.d("FCM", "Token saved successfully!"))
                            .addOnFailureListener(e -> android.util.Log.e("FCM", "Failed to save token", e));
                });
    }



}