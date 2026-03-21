package com.blake.gamevault.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.blake.gamevault.R;
import com.blake.gamevault.databinding.ActivityLoginBinding;
import com.blake.gamevault.databinding.ActivityRegisterBinding;
import com.blake.gamevault.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        binding.toSignIn.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        binding.btnCreateAccount.setOnClickListener(View -> {
            String username = binding.username.getText().toString().trim();
            String email = binding.email.getText().toString().trim();
            String password = binding.password.getText().toString().trim();
            String confirmPassword = binding.confirmPassword.getText().toString().trim();
            boolean termsAccepted = binding.cbTerms.isChecked();

            if (username.isEmpty()) {
                binding.username.setError("Username is required");
                binding.username.requestFocus();
                return;
            }

            if (email.isEmpty()) {
                binding.email.setError("Email is required");
                binding.email.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.email.setError("Invalid Email");
                binding.email.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                binding.password.setError("Password is required");
                binding.password.requestFocus();
                return;
            }

            if (password.length() < 6) {
                binding.password.setError("Password must be at least 6 characters");
                binding.password.requestFocus();
                return;
            }


            if (!isPasswordValid(password)) {
                binding.password.setError("Password must contain at least one uppercase, lowercase, digit, and symbol");
                binding.password.requestFocus();
                return;
            }

            if (confirmPassword.isEmpty()) {
                binding.confirmPassword.setError("Please confirm your password");
                binding.confirmPassword.requestFocus();
                return;
            }

            if (!password.equals(confirmPassword)) {
                binding.confirmPassword.setError("Passwords do not match");
                binding.confirmPassword.requestFocus();
                return;
            }

            if (!termsAccepted) {
                Toast.makeText(RegisterActivity.this, "Please accept the terms and conditions", Toast.LENGTH_SHORT).show();
                return;

            }

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            String uid = task.getResult().getUser().getUid();
                            User user = User.builder()
                                    .uid(uid)
                                    .username(username)
                                    .email(email)
                                    .build();

                            firebaseFirestore.collection("users")
                                    .document(uid)
                                    .set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getApplicationContext(), "Registered Successfully", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                            intent.putExtra("email", email);
                                            intent.putExtra("password", password);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(),"Registration Failed", Toast.LENGTH_SHORT).show();
                                            e.printStackTrace();
                                        }
                                    });
                        }
                    });


        });

    }


    private boolean isPasswordValid(String password) {
        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSymbol = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSymbol = true;
        }

        return hasUpper && hasLower && hasDigit && hasSymbol;
    }
}