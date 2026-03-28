package com.blake.gamevault.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blake.gamevault.R;
import com.blake.gamevault.activity.LoginActivity;
import com.blake.gamevault.databinding.FragmentProfileBinding;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    // Image Picker Launcher
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();

                    // 1. Show the image instantly using Glide
                    if (getContext() != null && binding != null) {
                        Glide.with(getContext())
                                .load(uri)
                                .placeholder(R.drawable.person)
                                .circleCrop()
                                .into(binding.profileImage);
                    }

                    // 2. Upload to Firebase Storage
                    String imageId = UUID.randomUUID().toString();
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference imageReference = storage.getReference("/images/profile-images/").child(imageId);

                    Toast.makeText(getContext(), "Uploading photo...", Toast.LENGTH_SHORT).show();

                    imageReference.putFile(uri)
                            .addOnSuccessListener(taskSnapshot -> {
                                // 3. Update the Firestore user document with the new imageId
                                db.collection("users")
                                        .document(auth.getUid())
                                        .update("profilePicUrl", imageId) // Using the exact field name from MainActivity
                                        .addOnSuccessListener(aVoid -> {
                                            if (getContext() != null) {
                                                Toast.makeText(getContext(), "Profile Picture Updated", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            })
                            .addOnFailureListener(e -> {
                                if (getContext() != null) {
                                    Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() != null && getActivity().findViewById(R.id.toolBar) != null) {
            getActivity().findViewById(R.id.toolBar).setVisibility(View.GONE);
        }

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            // Failsafe: if somehow reached without being logged in
            startActivity(new Intent(getActivity(), LoginActivity.class));
            requireActivity().finish();
            return;
        }

        // 1. Load User Profile Info
        loadUserProfile(currentUser);

        // 2. Load Account Stats
        loadAccountStats(currentUser.getUid());

        // 3. Setup Click Listeners
        setupClickListeners();
    }

    private void loadUserProfile(FirebaseUser currentUser) {
        // Fallback to Auth data first
        binding.profileEmail.setText(currentUser.getEmail());
        if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
            binding.profileName.setText(currentUser.getDisplayName());
        }

        // Fetch custom data from Firestore users collection
        db.collection("users").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && binding != null) {
                        // Look for username or name
                        String name = documentSnapshot.getString("username");
                        if (name == null) name = documentSnapshot.getString("name");
                        if (name != null) binding.profileName.setText(name);

                        // Load the image using the UUID from Firestore + Firebase Storage
                        String profilePicUrl = documentSnapshot.getString("profilePicUrl");

                        if (profilePicUrl != null && !profilePicUrl.isEmpty() && getContext() != null) {
                            FirebaseStorage.getInstance().getReference("/images/profile-images/" + profilePicUrl)
                                    .getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        if (getContext() != null && binding != null) {
                                            Glide.with(getContext())
                                                    .load(uri)
                                                    .placeholder(R.drawable.person)
                                                    .circleCrop()
                                                    .into(binding.profileImage);
                                        }
                                    });
                        }
                    }
                });
    }

    private void loadAccountStats(String uid) {
        // Count Orders
        db.collection("orders").whereEqualTo("userId", uid).get()
                .addOnSuccessListener(querySnapshot -> {
                    if (binding != null) binding.statOrders.setText(String.valueOf(querySnapshot.size()));
                });

        // Count Games Owned (Library)
        db.collection("users").document(uid).collection("library").get()
                .addOnSuccessListener(querySnapshot -> {
                    if (binding != null) binding.statGamesOwned.setText(String.valueOf(querySnapshot.size()));
                });

        // Count Wishlist (Favorites)
        db.collection("users").document(uid).collection("favorites").get()
                .addOnSuccessListener(querySnapshot -> {
                    if (binding != null) binding.statWishlist.setText(String.valueOf(querySnapshot.size()));
                });
    }

    private void setupClickListeners() {
        // Edit Photo - Launches the Image Picker
        binding.btnEditPhoto.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            imagePickerLauncher.launch(intent);
        });

        // Edit Profile
        binding.btnEditProfile.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new EditProfileFragment())
                    .addToBackStack(null)
                    .commit();
        });

        binding.btnChangePassword.setOnClickListener(v -> {
            com.blake.gamevault.fragment.ChangePasswordBottomSheet bottomSheet = new com.blake.gamevault.fragment.ChangePasswordBottomSheet();
            bottomSheet.show(getParentFragmentManager(), "ChangePasswordBottomSheet");
        });

        // Privacy Policy
        binding.btnPrivacyPolicy.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Privacy Policy clicked", Toast.LENGTH_SHORT).show();
        });

        // About
        binding.btnAbout.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new AboutFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Logout
        binding.btnLogout.setOnClickListener(v -> {
            auth.signOut();
            Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();

            // Redirect to Login
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            // Clear the backstack so they can't press 'back' to return to the profile
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null && getActivity().findViewById(R.id.toolBar) != null) {
            getActivity().findViewById(R.id.toolBar).setVisibility(View.VISIBLE);
        }
        binding = null;
    }
}