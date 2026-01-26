package com.example.cw2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GuestHomeActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private Button btnBrowseMenu, btnMakeReservation, btnMyReservations,
            btnNotifications, btnProfile, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_home);

        String username = getIntent().getStringExtra("username");

        initViews();
        setupWelcomeMessage(username);
        setupButtonListeners();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tv_welcome);
        btnBrowseMenu = findViewById(R.id.btn_browse_menu);
        btnMakeReservation = findViewById(R.id.btn_make_reservation);
        btnMyReservations = findViewById(R.id.btn_my_reservations);
        btnNotifications = findViewById(R.id.btn_notifications);
        btnProfile = findViewById(R.id.btn_profile);
        btnLogout = findViewById(R.id.btn_logout);
    }

    private void setupWelcomeMessage(String username) {
        if (username != null) {
            tvWelcome.setText("Welcome, " + username);
        }
    }

    private void setupButtonListeners() {
        btnBrowseMenu.setOnClickListener(v -> {
            Intent intent = new Intent(GuestHomeActivity.this, MenuActivity.class);
            intent.putExtra("isStaff", false);
            startActivity(intent);
        });

        btnMakeReservation.setOnClickListener(v -> {
            Intent intent = new Intent(GuestHomeActivity.this, AddReservationActivity.class);
            startActivity(intent);
        });

        btnMyReservations.setOnClickListener(v -> {
            Intent intent = new Intent(GuestHomeActivity.this, ReservationActivity.class);
            intent.putExtra("isStaff", false);
            startActivity(intent);
        });

        btnNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(GuestHomeActivity.this, NotificationActivity.class);
            startActivity(intent);
        });

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(GuestHomeActivity.this, ProfileActivity.class);
            intent.putExtra("isStaff", false);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(GuestHomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}