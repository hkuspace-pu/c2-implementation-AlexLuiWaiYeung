package com.example.cw2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cw2.MenuItem;

import java.util.UUID;

public class AddMenuItemActivity extends AppCompatActivity {

    // UI Components
    private EditText etItemName, etItemPrice, etItemDescription;
    private ImageView ivItemImage;
    private Button btnSelectImage, btnSave, btnCancel;

    // Image handling
    private static final int PICK_IMAGE_REQUEST = 1;
    private String selectedImagePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu_item);

        initViews();
        setupButtonListeners();
    }

    private void initViews() {
        etItemName = findViewById(R.id.et_item_name);
        etItemPrice = findViewById(R.id.et_item_price);
        etItemDescription = findViewById(R.id.et_item_description);
        ivItemImage = findViewById(R.id.iv_item_image);
        btnSelectImage = findViewById(R.id.btn_select_image);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
    }

    private void setupButtonListeners() {
        // Select Image Button
        btnSelectImage.setOnClickListener(v -> openImagePicker());

        // Save Button
        btnSave.setOnClickListener(v -> saveMenuItem());

        // Cancel Button
        btnCancel.setOnClickListener(v -> finish());

        // Image click to select
        ivItemImage.setOnClickListener(v -> openImagePicker());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void saveMenuItem() {
        // Get input values
        String name = etItemName.getText().toString().trim();
        String priceStr = etItemPrice.getText().toString().trim();
        String description = etItemDescription.getText().toString().trim();

        // Validate inputs
        if (name.isEmpty()) {
            etItemName.setError("Please enter item name");
            etItemName.requestFocus();
            return;
        }

        if (priceStr.isEmpty()) {
            etItemPrice.setError("Please enter price");
            etItemPrice.requestFocus();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
            if (price <= 0) {
                etItemPrice.setError("Price must be greater than 0");
                etItemPrice.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etItemPrice.setError("Please enter a valid price");
            etItemPrice.requestFocus();
            return;
        }

        if (description.isEmpty()) {
            etItemDescription.setError("Please enter description");
            etItemDescription.requestFocus();
            return;
        }

        // Create new MenuItem
        MenuItem newItem = new MenuItem(
                generateId(), // Generate unique ID
                name,
                price,
                description,
                selectedImagePath.isEmpty() ? "default_food.jpg" : selectedImagePath
        );

        // TODO: Save to database/API
        // For now, return result
        Intent resultIntent = new Intent();
        resultIntent.putExtra("newMenuItem", newItem);
        setResult(RESULT_OK, resultIntent);

        Toast.makeText(this, "Menu item added successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    private int generateId() {
        // Generate a unique ID (in real app, this would come from API)
        return UUID.randomUUID().hashCode() & Integer.MAX_VALUE;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                // Set image to ImageView
                ivItemImage.setImageURI(selectedImageUri);

                // Get image path (simplified - in real app, you'd save the file)
                selectedImagePath = selectedImageUri.toString();

                // Hide the select image button text
                btnSelectImage.setText("Change Image");
            }
        }
    }

    private boolean hasUnsavedChanges() {
        return !etItemName.getText().toString().isEmpty() ||
                !etItemPrice.getText().toString().isEmpty() ||
                !etItemDescription.getText().toString().isEmpty() ||
                !selectedImagePath.isEmpty();
    }

    private void showUnsavedChangesDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(this);

        builder.setTitle("Unsaved Changes");
        builder.setMessage("You have unsaved changes. Are you sure you want to discard them?");

        builder.setPositiveButton("Discard", (dialog, which) -> finish());
        builder.setNegativeButton("Cancel", null);

        builder.show();
    }
}