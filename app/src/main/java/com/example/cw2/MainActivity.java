package com.example.cw2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private RadioGroup rgUserType;
    private Button btnLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Find views
        etUsername = findViewById(R.id.editTextText);
        etPassword = findViewById(R.id.editTextTextPassword);
        rgUserType = findViewById(R.id.rgUserType);
        btnLogin = findViewById(R.id.Login);

        // Set click listener
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Basic validation
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check which role is selected
        int selectedId = rgUserType.getCheckedRadioButtonId();
        boolean isStaff = (selectedId == R.id.rbStaff);

        // TODO: Call your API to authenticate
        // For now, simulate successful login
        redirectToHomePage(isStaff);
    }

    private void redirectToHomePage(boolean isStaff) {
        Intent intent;

        if (isStaff) {
            // Redirect to Staff Home
            intent = new Intent(this, StaffHomeActivity.class);
        } else {
            // Redirect to Guest Home
            intent = new Intent(this, GuestHomeActivity.class);
        }

        // Pass user info if needed
        intent.putExtra("username", etUsername.getText().toString());
        intent.putExtra("isStaff", isStaff);

        startActivity(intent);
        finish(); // Close login activity so user can't go back
    }
}