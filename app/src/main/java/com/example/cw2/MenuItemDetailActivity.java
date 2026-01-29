package com.example.cw2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cw2.MenuItem;

public class MenuItemDetailActivity extends AppCompatActivity {

    // UI Components
    private ImageView ivItemImage;
    private TextView tvItemName, tvItemPrice, tvItemDescription;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_item_detail);

        // Get MenuItem from intent
        MenuItem menuItem = getIntent().getParcelableExtra("menuItem");
        if (menuItem == null) {
            finish();
            return;
        }

        initViews();
        displayMenuItem(menuItem);
    }

    private void initViews() {
        ivItemImage = findViewById(R.id.iv_item_image);
        tvItemName = findViewById(R.id.tv_item_name);
        tvItemPrice = findViewById(R.id.tv_item_price);
        tvItemDescription = findViewById(R.id.tv_item_description);
        btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> finish());
    }

    private void displayMenuItem(MenuItem item) {
        tvItemName.setText(item.getName());
        tvItemPrice.setText(String.format("$%.2f", item.getPrice()));
        tvItemDescription.setText(item.getDescription());

        // TODO: Load actual image using Glide/Picasso
        // For now, use placeholder
        ivItemImage.setImageResource(R.drawable.ic_food_placeholder);
    }
}