package com.example.cw2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class EditMenuItemActivity extends AppCompatActivity {
    private static final String TAG = "EditMenuItemActivity";

    private EditText etName, etDescription, etPrice, etImageUrl;
    private Button btnUpdate, btnDelete;
    private DemoData dbHelper;
    private MenuItem currentItem;
    private int menuItemId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_menu_item);

        Log.d(TAG, "=== EditMenuItemActivity Started ===");

        // Initialize views
        initViews();

        // Get the menu item ID from Intent
        menuItemId = getIntent().getIntExtra("MENU_ITEM_ID", -1);
        String itemName = getIntent().getStringExtra("MENU_ITEM_NAME");

        Log.d(TAG, "Received menuItemId: " + menuItemId);
        Log.d(TAG, "Received itemName: " + itemName);

        if (menuItemId == -1) {
            Toast.makeText(this, "Error: No menu item selected", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "CRITICAL: No MENU_ITEM_ID found in Intent extras");

            // Debug: List all extras
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                for (String key : extras.keySet()) {
                    Log.d(TAG, "Extra key: " + key + ", value: " + extras.get(key));
                }
            }

            finish();
            return;
        }

        // Initialize database
        dbHelper = new DemoData(this);

        // Load the menu item
        loadMenuItem();

        // Setup button listeners
        setupListeners();
    }

    private void initViews() {
        etName = findViewById(R.id.et_name);
        etDescription = findViewById(R.id.et_description);
        etPrice = findViewById(R.id.et_price);
        etImageUrl = findViewById(R.id.et_image_url);
        btnUpdate = findViewById(R.id.btn_update);
        btnDelete = findViewById(R.id.btn_delete);
    }

    private void loadMenuItem() {
        currentItem = dbHelper.getMenuItemById(menuItemId);

        if (currentItem != null) {
            Log.d(TAG, "Loaded item: " + currentItem.getName() +
                    " (ID: " + currentItem.getId() + ")");

            // Populate the form with existing data
            etName.setText(currentItem.getName());
            etDescription.setText(currentItem.getDescription());
            etPrice.setText(currentItem.getPrice());
            etImageUrl.setText(currentItem.getImageUrl());

            // Set title
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Edit: " + currentItem.getName());
            }
        } else {
            Toast.makeText(this, "Error: Menu item not found", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Could not find menu item with ID: " + menuItemId);
            finish();
        }
    }

    private void setupListeners() {
        btnUpdate.setOnClickListener(v -> updateMenuItem());

        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void updateMenuItem() {
        // Get updated values
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String price = etPrice.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();

        // Validation
        if (name.isEmpty()) {
            etName.setError("Name is required");
            return;
        }

        if (price.isEmpty()) {
            etPrice.setError("Price is required");
            return;
        }

        // Update the current item
        currentItem.setName(name);
        currentItem.setDescription(description);
        currentItem.setPrice(price);
        currentItem.setImageUrl(imageUrl);

        // Update in database
        int rowsAffected = dbHelper.updateMenuItem(currentItem);

        if (rowsAffected > 0) {
            Toast.makeText(this, "Menu item updated successfully", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Updated item ID: " + currentItem.getId());

            // Set result and finish
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Failed to update menu item", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Update failed for item ID: " + currentItem.getId());
        }
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Menu Item")
                .setMessage("Are you sure you want to delete \"" + currentItem.getName() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> deleteMenuItem())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteMenuItem() {
        int rowsDeleted = dbHelper.deleteMenuItem(currentItem.getId());

        if (rowsDeleted > 0) {
            Toast.makeText(this, "Menu item deleted", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Deleted item ID: " + currentItem.getId());

            // Set result as deleted
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Failed to delete menu item", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Delete failed for item ID: " + currentItem.getId());
        }
    }
}