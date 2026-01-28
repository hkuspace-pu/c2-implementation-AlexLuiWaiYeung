package com.example.cw2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StaffHomeActivity extends AppCompatActivity {
    private TextView tvWelcome;
    private Button btnMenu, btnReservations, btnProfile, btnLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_home);

        String username = getIntent().getStringExtra("username");

        initViews();
        setupWelcomeMessage(username);
        setupButtonListeners();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tv_welcome);
        btnMenu = findViewById(R.id.btn_menu);
        btnReservations = findViewById(R.id.btn_reservations);
        btnProfile = findViewById(R.id.btn_profile);
        btnLogout = findViewById(R.id.btn_logout);
    }

    private void setupWelcomeMessage(String username) {
        if (username != null) {
            tvWelcome.setText("Welcome, " + username + "\n(Staff)");
        }
    }

    private void setupButtonListeners() {
        btnMenu.setOnClickListener(v -> {
            Intent intent = new Intent(StaffHomeActivity.this, MenuActivity.class);
            intent.putExtra("isStaff", true);
            startActivity(intent);
        });

        btnReservations.setOnClickListener(v -> {
            Intent intent = new Intent(StaffHomeActivity.this, ReservationActivity.class);
            intent.putExtra("isStaff", true);
            startActivity(intent);
        });

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(StaffHomeActivity.this, ProfileActivity.class);
            intent.putExtra("username", true);
            intent.putExtra("isStaff", true);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(StaffHomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}