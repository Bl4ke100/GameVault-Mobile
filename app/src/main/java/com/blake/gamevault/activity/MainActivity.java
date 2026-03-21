package com.blake.gamevault.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
import com.blake.gamevault.fragment.CartFragment;
import com.blake.gamevault.fragment.HomeFragment;
import com.blake.gamevault.fragment.LibraryFragment;
import com.blake.gamevault.fragment.MessageFragment;
import com.blake.gamevault.fragment.OrdersFragment;
import com.blake.gamevault.fragment.ProfileFragment;
import com.blake.gamevault.fragment.SettingsFragment;
import com.blake.gamevault.fragment.ShopFragment;
import com.blake.gamevault.fragment.WishlistFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NavigationBarView.OnItemSelectedListener {

    private DrawerLayout drawerLayout;
    private MaterialToolbar toolbar;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;

    EditText searchTextInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolBar);
        navigationView = findViewById(R.id.sideNav);
        bottomNavigationView = findViewById(R.id.bottomNav);

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

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        navigationView.setCheckedItem(-1);

        if (itemId == R.id.side_nav_profile) {

            loadFragment(new ProfileFragment());
            //navigationView.setCheckedItem(R.id.side_nav_profile);
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

        } else if (itemId == R.id.bottom_nav_home) {

            loadFragment(new HomeFragment());
            //bottomNavigationView.setSelectedItemId(R.id.bottom_nav_home);
            restoreBottomNavSelection();
            bottomNavigationView.getMenu().findItem(R.id.bottom_nav_home).setChecked(true);
            clearSideNavSelection();

        } else if (itemId == R.id.bottom_nav_shop) {

            loadFragment(new ShopFragment());
            restoreBottomNavSelection();
            bottomNavigationView.getMenu().findItem(R.id.bottom_nav_shop).setChecked(true);
            clearSideNavSelection();

        } else if (itemId == R.id.bottom_nav_cart) {

            loadFragment(new CartFragment());
            restoreBottomNavSelection();
            bottomNavigationView.getMenu().findItem(R.id.bottom_nav_cart).setChecked(true);
            clearSideNavSelection();

        } else if (itemId == R.id.bottom_nav_library) {

            loadFragment(new LibraryFragment());
            restoreBottomNavSelection();
            bottomNavigationView.getMenu().findItem(R.id.bottom_nav_library).setChecked(true);
            clearSideNavSelection();

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