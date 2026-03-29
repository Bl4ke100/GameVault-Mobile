package com.blake.gamevault.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.blake.gamevault.BuildConfig; // Ensure this is imported for the version name!
import com.blake.gamevault.databinding.FragmentSettingsBinding; // Change if your layout name is different
import com.bumptech.glide.Glide;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- 1. THEME TOGGLE ---
        SharedPreferences prefs = requireActivity().getSharedPreferences("GameVaultPrefs", Context.MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("darkMode", true);

        binding.switchTheme.setChecked(isDarkMode);

        binding.switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("darkMode", isChecked).apply();

            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES
                            : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        // --- 2. CLEAR CACHE ---
        binding.btnClearCache.setOnClickListener(v -> {
            Context appContext = requireContext().getApplicationContext();

            // Clear memory instantly (Must be on Main Thread)
            Glide.get(appContext).clearMemory();

            // Clear disk storage (Must be on Background Thread)
            new Thread(() -> {
                Glide.get(appContext).clearDiskCache();

                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(appContext, "Image cache cleared!", Toast.LENGTH_SHORT).show();
                });
            }).start();
        });

        // --- 3. DYNAMIC APP VERSION ---
        // This automatically grabs the version from your build.gradle file!
        String versionName = BuildConfig.VERSION_NAME;
        binding.settingsAppVersion.setText(versionName);
        binding.settingsVersionFooter.setText("GameVault v" + versionName);

        // --- 4. ABOUT DIALOG ---
        binding.btnSettingsAbout.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("About GameVault")
                    .setMessage("GameVault is a premium game key store developed by Blake.\n\nVersion: " + versionName)
                    .setPositiveButton("Close", null)
                    .show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}