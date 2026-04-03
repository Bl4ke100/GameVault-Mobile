package com.blake.gamevault.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blake.gamevault.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordBottomSheet extends BottomSheetDialogFragment {

    private TextInputEditText inputCurrentPassword, inputNewPassword, inputConfirmPassword;
    private MaterialButton btnUpdatePassword;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_change_password, container, false);

        auth = FirebaseAuth.getInstance();

        inputCurrentPassword = view.findViewById(R.id.inputCurrentPassword);
        inputNewPassword = view.findViewById(R.id.inputNewPassword);
        inputConfirmPassword = view.findViewById(R.id.inputConfirmPassword);
        btnUpdatePassword = view.findViewById(R.id.btnUpdatePassword);

        btnUpdatePassword.setOnClickListener(v -> changePassword());

        return view;
    }

    private void changePassword() {
        String currentPass = inputCurrentPassword.getText().toString().trim();
        String newPass = inputNewPassword.getText().toString().trim();
        String confirmPass = inputConfirmPassword.getText().toString().trim();

        if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPass.length() < 6) {
            inputNewPassword.setError("Password must be at least 6 characters");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            inputConfirmPassword.setError("Passwords do not match");
            return;
        }

        FirebaseUser user = auth.getCurrentUser();
        if (user != null && user.getEmail() != null) {
            btnUpdatePassword.setEnabled(false);
            btnUpdatePassword.setText("Verifying...");

            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPass);

            user.reauthenticate(credential).addOnSuccessListener(unused -> {
                btnUpdatePassword.setText("Updating...");

                user.updatePassword(newPass).addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                    dismiss();
                }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to update password: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    resetButton();
                });

            }).addOnFailureListener(e -> {
                inputCurrentPassword.setError("Incorrect current password");
                resetButton();
            });
        }
    }

    private void resetButton() {
        btnUpdatePassword.setEnabled(true);
        btnUpdatePassword.setText("Update Password");
    }
}