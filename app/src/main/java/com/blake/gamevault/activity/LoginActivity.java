package com.blake.gamevault.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.blake.gamevault.R;
import com.blake.gamevault.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        binding.toSignUp.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        binding.btnSignIn.setOnClickListener(View -> {
            String email = binding.emailInput.getText().toString().trim();
            String password = binding.pwInput.getText().toString().trim();

            if (email.isEmpty()) {
                binding.emailInput.setError("Email is required");
                binding.emailInput.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.emailInput.setError("Invalid Email");
                binding.emailInput.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                binding.pwInput.setError("Password is required");
                binding.pwInput.requestFocus();
                return;
            }

            if (password.length() < 8) {
                binding.pwInput.setError("Password must be at least 8 characters");
                binding.pwInput.requestFocus();
                return;
            }

//            if (!isPasswordValid(password)){
//                binding.pwInput.setError("Password must contain at least one uppercase, lowercase, digit, and symbol");
//                binding.pwInput.requestFocus();
//                return;
//            }




            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                updateUI(firebaseAuth.getCurrentUser());

                            } else {
                                Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        });

    }

    private void updateUI(FirebaseUser user) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

//    private boolean isPasswordValid(String password) {
//        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSymbol = false;
//
//        for (char c : password.toCharArray()) {
//            if (Character.isUpperCase(c)) hasUpper = true;
//            else if (Character.isLowerCase(c)) hasLower = true;
//            else if (Character.isDigit(c)) hasDigit = true;
//            else hasSymbol = true;
//        }
//
//        return hasUpper && hasLower && hasDigit && hasSymbol;
//    }
}