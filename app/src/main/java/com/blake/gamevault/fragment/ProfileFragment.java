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

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();

<<<<<<< HEAD
                    // 🛑 THE SHIELD
                    if (!isAdded() || binding == null) return;

                    // 1. Show the image instantly using Glide + CACHE
                    Glide.with(this) // Use 'this' for fragment lifecycle safety
                            .load(uri)
                            .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.person)
                            .circleCrop()
                            .into(binding.profileImage);
=======
                    if (getContext() != null && binding != null) {
                        Glide.with(getContext())
                                .load(uri)
                                .placeholder(R.drawable.person)
                                .circleCrop()
                                .into(binding.profileImage);
                    }
>>>>>>> d0e449b8f2fe214ea1effb6812f4624bd8ff5d73

                    String imageId = UUID.randomUUID().toString();
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference imageReference = storage.getReference("/images/profile-images/").child(imageId);

                    Toast.makeText(getContext(), "Uploading photo...", Toast.LENGTH_SHORT).show();

                    imageReference.putFile(uri)
                            .addOnSuccessListener(taskSnapshot -> {
<<<<<<< HEAD
                                // 3. Update the Firestore user document
=======
>>>>>>> d0e449b8f2fe214ea1effb6812f4624bd8ff5d73
                                db.collection("users")
                                        .document(auth.getUid())
                                        .update("profilePicUrl", imageId)
                                        .addOnSuccessListener(aVoid -> {
                                            // 🛑 THE SHIELD
                                            if (!isAdded() || getContext() == null) return;
                                            Toast.makeText(getContext(), "Profile Picture Updated", Toast.LENGTH_SHORT).show();
                                        });
                            })
                            .addOnFailureListener(e -> {
                                if (isAdded() && getContext() != null) {
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


        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            requireActivity().finish();
            return;
        }

        loadUserProfile(currentUser);

        loadAccountStats(currentUser.getUid());

        setupClickListeners();
    }

    private void loadUserProfile(FirebaseUser currentUser) {
        binding.profileEmail.setText(currentUser.getEmail());
        if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
            binding.profileName.setText(currentUser.getDisplayName());
        }

        db.collection("users").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
<<<<<<< HEAD
                    // 🛑 THE SHIELD
                    if (!isAdded() || binding == null) return;

                    if (documentSnapshot.exists()) {
=======
                    if (documentSnapshot.exists() && binding != null) {
>>>>>>> d0e449b8f2fe214ea1effb6812f4624bd8ff5d73
                        String name = documentSnapshot.getString("username");
                        if (name == null) name = documentSnapshot.getString("name");
                        if (name != null) binding.profileName.setText(name);

                        String profilePicUrl = documentSnapshot.getString("profilePicUrl");

                        if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
                            FirebaseStorage.getInstance().getReference("/images/profile-images/" + profilePicUrl)
                                    .getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        // 🛑 THE SHIELD
                                        if (!isAdded() || binding == null) return;

                                        Glide.with(this) // Use 'this' for safety
                                                .load(uri)
                                                .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL) // CACHE
                                                .placeholder(R.drawable.person)
                                                .circleCrop()
                                                .into(binding.profileImage);
                                    });
                        }
                    }
                });
    }

    private void loadAccountStats(String uid) {
        db.collection("orders").whereEqualTo("userId", uid).get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!isAdded() || binding == null) return; // 🛑 SHIELD
                    binding.statOrders.setText(String.valueOf(querySnapshot.size()));
                });

        db.collection("users").document(uid).collection("library").get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!isAdded() || binding == null) return; // 🛑 SHIELD
                    binding.statGamesOwned.setText(String.valueOf(querySnapshot.size()));
                });

        db.collection("users").document(uid).collection("favorites").get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!isAdded() || binding == null) return; // 🛑 SHIELD
                    binding.statWishlist.setText(String.valueOf(querySnapshot.size()));
                });
    }

    private void setupClickListeners() {
        binding.btnEditPhoto.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            imagePickerLauncher.launch(intent);
        });

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

        binding.btnPrivacyPolicy.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Privacy Policy clicked", Toast.LENGTH_SHORT).show();
        });

        binding.btnAbout.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new AboutFragment())
                    .addToBackStack(null)
                    .commit();
        });

        binding.btnLogout.setOnClickListener(v -> {
            auth.signOut();
            Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getActivity(), LoginActivity.class);
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