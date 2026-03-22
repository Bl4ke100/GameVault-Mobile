package com.blake.gamevault.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
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
                    .addOnSuccessListener(ds ->{

                        if (ds.exists()){
                        User user = ds.toObject(User.class);
                        sideNavHeaderBinding.headerUsername.setText(user.getUsername());
                        sideNavHeaderBinding.headerEmail.setText(user.getEmail());

                        Glide.with(MainActivity.this)
                                .load(user.getProfilePicUrl())
                                .circleCrop()
                                .into(sideNavHeaderBinding.headerPfp);
                        }

                    } );

            navigationView.getMenu().findItem(R.id.side_nav_profile).setVisible(true);
            navigationView.getMenu().findItem(R.id.side_nav_order).setVisible(true);
            navigationView.getMenu().findItem(R.id.side_nav_wishlist).setVisible(true);
            navigationView.getMenu().findItem(R.id.side_nav_Message).setVisible(true);
            navigationView.getMenu().findItem(R.id.side_nav_login).setVisible(false);
            navigationView.getMenu().findItem(R.id.side_nav_logout).setVisible(true);

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

        } else if (itemId == R.id.side_nav_Message) {

            loadFragment(new MessageFragment());
            navigationView.getMenu().findItem(R.id.side_nav_Message).setChecked(true);
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

            if (firebaseAuth.getCurrentUser() == null){
                Toast.makeText(MainActivity.this, "Please Login", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }else {
                loadFragment(new CartFragment());
                restoreBottomNavSelection();
                bottomNavigationView.getMenu().findItem(R.id.bottom_nav_cart).setChecked(true);
                clearSideNavSelection();
            }

        } else if (itemId == R.id.bottom_nav_library) {

            if (firebaseAuth.getCurrentUser() == null){
                Toast.makeText(MainActivity.this, "Please Login", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }else {
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


}