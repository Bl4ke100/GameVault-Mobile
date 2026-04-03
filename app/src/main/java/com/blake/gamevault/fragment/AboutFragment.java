package com.blake.gamevault.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blake.gamevault.databinding.FragmentAboutBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AboutFragment extends Fragment implements OnMapReadyCallback {

    private FragmentAboutBinding binding;
    private GoogleMap googleMap;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAboutBinding.inflate(inflater, container, false);

        binding.aboutMapView.onCreate(savedInstanceState);
        binding.aboutMapView.getMapAsync(this);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupContactButtons();
        setupSocialButtons();
    }

    private void setupContactButtons() {
        binding.btnContactEmail.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + binding.aboutEmail.getText().toString()));
            startActivity(Intent.createChooser(intent, "Send Email"));
        });

        binding.btnContactPhone.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + binding.aboutPhone.getText().toString()));
            startActivity(intent);
        });

        binding.btnContactWebsite.setOnClickListener(v -> {
            openUrl("https://github.com/Bl4ke100"); // Using your GitHub as a placeholder
        });
    }

    private void setupSocialButtons() {
        binding.btnSocialInstagram.setOnClickListener(v -> openUrl("https://instagram.com"));
        binding.btnSocialLinkedIn.setOnClickListener(v -> openUrl("https://linkedin.com/in/janidumagamage"));
        binding.btnSocialGitHub.setOnClickListener(v -> openUrl("https://github.com/JaniduMagamage"));
    }

    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        LatLng location = new LatLng(7.0167, 79.9833);
        googleMap.addMarker(new MarkerOptions().position(location).title("GameVault HQ"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14f));
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }

    // CRITICAL: MapView lifecycle management
    @Override
    public void onResume() {
        super.onResume();
        binding.aboutMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.aboutMapView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.aboutMapView.onDestroy();
        binding = null;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.aboutMapView.onLowMemory();
    }
}