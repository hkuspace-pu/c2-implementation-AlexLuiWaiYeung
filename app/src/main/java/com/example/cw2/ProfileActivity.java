package com.example.cw2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cw2.User;

public class ProfileActivity extends AppCompatActivity {

    // UI Components
    private TextView tvUsername, tvUserRole;
    private EditText etFullName, etEmail, etPhone;
    private Switch switchNotifications;
    private Button btnSave, btnCancel, btnChangePassword;

    // User data
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Get user info from intent
        String username = getIntent().getStringExtra("username");
        boolean isStaff = getIntent().getBooleanExtra("isStaff", false);

        initViews();
        loadUserData(username, isStaff);
        setupButtonListeners();
    }

    private void initViews() {
        tvUsername = findViewById(R.id.tv_username);
        tvUserRole = findViewById(R.id.tv_user_role);
        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        switchNotifications = findViewById(R.id.switch_notifications);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
        btnChangePassword = findViewById(R.id.btn_change_password);
    }

    private void loadUserData(String username, boolean isStaff) {
        // TODO: Load user data from database/API
        // For now, create sample user data

        currentUser = new User(
                username,
                username + "@example.com", // sample email
                "555-0123", // sample phone
                "John Doe", // sample full name
                isStaff,
                true // notifications enabled by default
        );

        // Display user data
        tvUsername.setText(currentUser.getUsername());
        tvUserRole.setText(currentUser.isStaff() ? "Staff Member" : "Guest Customer");
        etFullName.setText(currentUser.getFullName());
        etEmail.setText(currentUser.getEmail());
        etPhone.setText(currentUser.getPhone());
        switchNotifications.setChecked(currentUser.isNotificationsEnabled());
    }

    private void setupButtonListeners() {
        // Save Button
        btnSave.setOnClickListener(v -> saveProfile());

        // Cancel Button
        btnCancel.setOnClickListener(v -> finish());

        // Change Password Button
        btnChangePassword.setOnClickListener(v -> changePassword());
    }

    private void saveProfile() {
        // Get updated values
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        boolean notificationsEnabled = switchNotifications.isChecked();

        // Validation
        if (fullName.isEmpty()) {
            etFullName.setError("Please enter your full name");
            etFullName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Please enter your email");
            etEmail.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            etPhone.setError("Please enter your phone number");
            etPhone.requestFocus();
            return;
        }

        // Update user object
        currentUser.setFullName(fullName);
        currentUser.setEmail(email);
        currentUser.setPhone(phone);
        currentUser.setNotificationsEnabled(notificationsEnabled);

        // TODO: Save to database/API
        // For now, show success message

        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

        // Return to previous activity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updatedUser", currentUser);
        setResult(RESULT_OK, resultIntent);

        finish();
    }

    private void changePassword() {
        // TODO: Implement password change functionality
        // For now, show a placeholder message
        Toast.makeText(this, "Password change feature coming soon", Toast.LENGTH_SHORT).show();

        // In a real app, you would:
        // 1. Show a dialog with current password, new password, confirm new password
        // 2. Validate current password
        // 3. Send API request to update password
    }

}