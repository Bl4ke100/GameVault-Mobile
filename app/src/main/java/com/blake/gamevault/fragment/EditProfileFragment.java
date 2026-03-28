package com.blake.gamevault.fragment;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blake.gamevault.R;
import com.blake.gamevault.databinding.FragmentEditProfileBinding; // Adjust if your XML name differs
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class EditProfileFragment extends Fragment {

    private FragmentEditProfileBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Hide toolbar if needed
        if (getActivity() != null && getActivity().findViewById(R.id.toolBar) != null) {
            getActivity().findViewById(R.id.toolBar).setVisibility(View.GONE);
        }

        loadUserData();

        binding.btnSaveChanges.setOnClickListener(v -> {
            if (validateInputs()) {
                saveUserData();
            }
        });
    }

    private void loadUserData() {
        if (auth.getCurrentUser() == null) return;
        String uid = auth.getCurrentUser().getUid();

        // Pre-fill email from Auth as a fallback
        binding.updateInputEmail.setText(auth.getCurrentUser().getEmail());

        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && binding != null) {

                        // Username (Read-only)
                        String username = documentSnapshot.getString("username");
                        if (username != null) binding.updateInputUsername.setText(username);

                        // Personal Info
                        String name = documentSnapshot.getString("name");
                        if (name != null) binding.updateInputFullName.setText(name);

                        String email = documentSnapshot.getString("email");
                        if (email != null && !email.isEmpty()) binding.updateInputEmail.setText(email);

                        String phone = documentSnapshot.getString("phone");
                        if (phone != null) binding.updateInputPhone.setText(phone);

                        // Billing Details
                        String address1 = documentSnapshot.getString("addressLine1");
                        if (address1 != null) binding.updateInputAddressLine1.setText(address1);

                        String address2 = documentSnapshot.getString("addressLine2");
                        if (address2 != null) binding.updateInputAddressLine2.setText(address2);

                        String city = documentSnapshot.getString("city");
                        if (city != null) binding.updateInputCity.setText(city);

                        String postalCode = documentSnapshot.getString("postalCode");
                        if (postalCode != null) binding.updateInputPostalCode.setText(postalCode);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to load profile data", Toast.LENGTH_SHORT).show());
    }

    private boolean validateInputs() {
        String email = binding.updateInputEmail.getText().toString().trim();
        String phone = binding.updateInputPhone.getText().toString().trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.updateInputEmail.setError("Please enter a valid email address");
            binding.updateInputEmail.requestFocus();
            return false;
        }

        if (phone.isEmpty() || phone.length() < 10) {
            binding.updateInputPhone.setError("Please enter a valid phone number");
            binding.updateInputPhone.requestFocus();
            return false;
        }

        // Clear errors if valid
        binding.updateInputEmail.setError(null);
        binding.updateInputPhone.setError(null);

        return true;
    }

    private void saveUserData() {
        if (auth.getCurrentUser() == null) return;
        String uid = auth.getCurrentUser().getUid();

        binding.btnSaveChanges.setEnabled(false);
        binding.btnSaveChanges.setText("Saving...");

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", binding.updateInputFullName.getText().toString().trim());
        updates.put("email", binding.updateInputEmail.getText().toString().trim());
        updates.put("phone", binding.updateInputPhone.getText().toString().trim());
        updates.put("addressLine1", binding.updateInputAddressLine1.getText().toString().trim());
        updates.put("addressLine2", binding.updateInputAddressLine2.getText().toString().trim());
        updates.put("city", binding.updateInputCity.getText().toString().trim());
        updates.put("postalCode", binding.updateInputPostalCode.getText().toString().trim());

        // SetOptions.merge() creates the fields if they don't exist yet!
        db.collection("users").document(uid)
                .set(updates, SetOptions.merge())
                .addOnSuccessListener(unused -> {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().popBackStack(); // Go back to the previous screen
                    }
                })
                .addOnFailureListener(e -> {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                        binding.btnSaveChanges.setEnabled(true);
                        binding.btnSaveChanges.setText("Save Changes");
                    }
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