package com.example.cw2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private RadioGroup rgUserType;
    private Button btnLogin;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize auth repository
        authRepository = new AuthRepository(this);

        // Check if already logged in
        if (authRepository.isLoggedIn()) {
            User user = authRepository.getCurrentUser();
            if (user != null) {
                navigateToHome(user);
                return;
            }
        }

        initViews();
        setupLoginButton();
    }

    private void initViews() {
        etUsername = findViewById(R.id.editTextText);
        etPassword = findViewById(R.id.editTextTextPassword);
        rgUserType = findViewById(R.id.rgUserType);
        btnLogin = findViewById(R.id.Login);

        // demo
        etUsername.setText("restaurant_staff");
        etPassword.setText("staff123");
    }

    private void setupLoginButton() {
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isStaff = rgUserType.getCheckedRadioButtonId() == R.id.rgUserType;

            // Call API for authentication
            authRepository.login(username, password, isStaff, new AuthRepository.AuthCallback() {
                @Override
                public void onSuccess(User user) {
                    runOnUiThread(() -> {
                        btnLogin.setEnabled(true);
                        etUsername.setEnabled(true);
                        etPassword.setEnabled(true);

                        Toast.makeText(MainActivity.this,
                                "Welcome, " + user.getFullName(), Toast.LENGTH_SHORT).show();

                        navigateToHome(user);
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    runOnUiThread(() -> {
                        btnLogin.setEnabled(true);
                        etUsername.setEnabled(true);
                        etPassword.setEnabled(true);

                        // Try demo mode as fallback
                        if (username.equals("admin") && password.equals("123456")) {
                            authRepository.demoLogin(username, password, isStaff, new AuthRepository.AuthCallback() {
                                @Override
                                public void onSuccess(User user) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(MainActivity.this,
                                                "Demo Mode: Welcome " + user.getUsername(), Toast.LENGTH_SHORT).show();
                                        navigateToHome(user);
                                    });
                                }

                                @Override
                                public void onError(String demoError) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(MainActivity.this,
                                                "Login failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(MainActivity.this,
                                    "Login failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        });
    }
    private void navigateToHome(User user) {
        Intent intent;

        if (user.isStaff()) {
            intent = new Intent(MainActivity.this, StaffHomeActivity.class);
        } else {
            intent = new Intent(MainActivity.this, GuestHomeActivity.class);
        }

        intent.putExtra("username", user.getUsername());
        intent.putExtra("isStaff", user.isStaff());
        intent.putExtra("fullName", user.getFullName());
        startActivity(intent);
        finish();
    }
}