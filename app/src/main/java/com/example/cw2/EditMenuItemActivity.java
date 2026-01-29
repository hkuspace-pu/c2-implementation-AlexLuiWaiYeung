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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class EditMenuItemActivity extends AppCompatActivity {

    // UI Components
    private EditText etItemName, etItemPrice, etItemDescription;
    private ImageView ivItemImage;
    private Button btnSelectImage, btnUpdate, btnCancel, btnDelete;

    // Data
    private MenuItem menuItem;
    private static final int PICK_IMAGE_REQUEST = 1;
    private String selectedImagePath = "";
    private boolean imageChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_menu_item);

        // Get the MenuItem from intent
        menuItem = getIntent().getParcelableExtra("menuItem");
        if (menuItem == null) {
            Toast.makeText(this, "Error: No menu item data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        loadMenuItemData();
        setupButtonListeners();
    }

    private void initViews() {
        etItemName = findViewById(R.id.et_item_name);
        etItemPrice = findViewById(R.id.et_item_price);
        etItemDescription = findViewById(R.id.et_item_description);
        ivItemImage = findViewById(R.id.iv_item_image);
        btnSelectImage = findViewById(R.id.btn_select_image);
        btnUpdate = findViewById(R.id.btn_update);
        btnCancel = findViewById(R.id.btn_cancel);
        btnDelete = findViewById(R.id.btn_delete);
    }

    private void loadMenuItemData() {
        // Pre-fill form with existing data
        etItemName.setText(menuItem.getName());
        etItemPrice.setText(String.format("%.2f", menuItem.getPrice()));
        etItemDescription.setText(menuItem.getDescription());

        // Set image (in real app, load from URL)
        // For now, use placeholder
        ivItemImage.setImageResource(R.drawable.ic_food_placeholder);

        // Store original image path
        selectedImagePath = menuItem.getImageUrl();
    }

    private void setupButtonListeners() {
        // Select Image Button
        btnSelectImage.setOnClickListener(v -> openImagePicker());

        // Update Button
        btnUpdate.setOnClickListener(v -> updateMenuItem());

        // Cancel Button
        btnCancel.setOnClickListener(v -> finish());

        // Delete Button
        btnDelete.setOnClickListener(v -> showDeleteConfirmation());

        // Image click to select
        ivItemImage.setOnClickListener(v -> openImagePicker());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void updateMenuItem() {
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
        if (description.isEmpty()) {
            etItemDescription.setError("Please enter description");
            etItemDescription.requestFocus();
            return;
        }

        // Update the MenuItem object
        menuItem.setName(name);
        menuItem.setPrice(priceStr);
        menuItem.setDescription(description);

        // Update image if changed
        if (imageChanged && !selectedImagePath.isEmpty()) {
            menuItem.setImageUrl(selectedImagePath);
        }

        // TODO: Call API to update item in database
        // For now, return updated item

        //ntent resultIntent = new Intent();
        //resultIntent.putExtra("updatedMenuItem", menuItem);
        //setResult(RESULT_OK, resultIntent);

        Toast.makeText(this, "Menu item updated successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void showDeleteConfirmation() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);

        builder.setTitle("Delete Menu Item");
        builder.setMessage("Are you sure you want to delete \"" + menuItem.getName() + "\"?");
        builder.setCancelable(true);

        builder.setPositiveButton("Delete", (dialog, which) -> {
            // Delete the item
            deleteMenuItem();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });

        builder.show();
    }

    private void deleteMenuItem() {
        // TODO: Call API to delete item from database
        // For now, return delete signal

        Intent resultIntent = new Intent();
        resultIntent.putExtra("deletedItemId", menuItem.getId());
        resultIntent.putExtra("action", "delete");
        setResult(RESULT_OK, resultIntent);

        Toast.makeText(this, "Menu item deleted", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                // Set image to ImageView
                ivItemImage.setImageURI(selectedImageUri);

                // Get image path
                selectedImagePath = selectedImageUri.toString();
                imageChanged = true;

                // Update button text
                btnSelectImage.setText("Change Image");
            }
        }
    }
}