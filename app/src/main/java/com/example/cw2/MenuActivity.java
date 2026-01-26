package com.example.cw2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmptyState, tvTitle;
    private Button btnAddNew, btnBack;
    private MenuAdapter adapter;
    private boolean isStaff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Get user type from intent
        isStaff = getIntent().getBooleanExtra("isStaff", false);

        initViews();
        setupRecyclerView();
        loadMenuData();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.rv_menu);
        progressBar = findViewById(R.id.progress_bar);
        tvEmptyState = findViewById(R.id.tv_empty_state);
        tvTitle = findViewById(R.id.tv_title);
        btnAddNew = findViewById(R.id.btn_add_new);
        btnBack = findViewById(R.id.btn_back);

        // Set title based on user type
        tvTitle.setText(isStaff ? "Menu Management" : "Our Menu");

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Add new button (only for staff)
        if (isStaff) {
            btnAddNew.setVisibility(View.VISIBLE);
            btnAddNew.setOnClickListener(v -> {
                Intent intent = new Intent(MenuActivity.this, AddMenuItemActivity.class);
                startActivityForResult(intent, 100);
            });
        } else {
            btnAddNew.setVisibility(View.GONE);
        }
    }

    private void setupRecyclerView() {
        // Set layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Add divider between items
        DividerItemDecoration divider = new DividerItemDecoration(this, layoutManager.getOrientation());
        recyclerView.addItemDecoration(divider);

        // Create adapter with click listener
        adapter = new MenuAdapter(new ArrayList<>(), isStaff, new MenuAdapter.MenuClickListener() {
            @Override
            public void onItemClick(MenuItem item) {
                // View item details
                Intent intent = new Intent(MenuActivity.this, MenuItemDetailActivity.class);
                intent.putExtra("menuItem", item);
                startActivity(intent);
            }

            @Override
            public void onEditClick(MenuItem item) {
                // Edit item
                Intent intent = new Intent(MenuActivity.this, EditMenuItemActivity.class);
                intent.putExtra("menuItem", item);
                startActivityForResult(intent, 101);
            }

            @Override
            public void onDeleteClick(MenuItem item) {
                // Confirm delete
                showDeleteConfirmation(item);
            }

            @Override
            public void onOrderClick(MenuItem item) {
                // Add to order/cart
                addToOrder(item);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private void loadMenuData() {
        // Show loading
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.GONE);

        // Simulate network delay
        recyclerView.postDelayed(() -> {
            List<MenuItem> menuItems = getSampleMenuItems();

            // Update UI
            adapter.setMenuItems(menuItems);
            progressBar.setVisibility(View.GONE);

            if (menuItems.isEmpty()) {
                tvEmptyState.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                tvEmptyState.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }, 1000);
    }

    private List<MenuItem> getSampleMenuItems() {
        List<MenuItem> items = new ArrayList<>();

        // Add sample menu items
        items.add(new MenuItem(1, "Margherita Pizza", 12.99,
                "Fresh tomatoes, mozzarella cheese, and basil", "pizza.jpg"));
        items.add(new MenuItem(2, "Classic Burger", 9.99,
                "Beef patty with lettuce, tomato, cheese, and special sauce", "burger.jpg"));
        items.add(new MenuItem(3, "Caesar Salad", 8.50,
                "Romaine lettuce with Caesar dressing, croutons, and parmesan", "salad.jpg"));
        items.add(new MenuItem(4, "Spaghetti Carbonara", 14.99,
                "Pasta with eggs, cheese, pancetta, and black pepper", "pasta.jpg"));
        items.add(new MenuItem(5, "Grilled Salmon", 16.99,
                "Atlantic salmon with lemon butter sauce and vegetables", "salmon.jpg"));
        items.add(new MenuItem(6, "Chocolate Lava Cake", 6.99,
                "Warm chocolate cake with molten center and vanilla ice cream", "cake.jpg"));

        return items;
    }

    private void showDeleteConfirmation(MenuItem item) {
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(this);

        builder.setTitle("Delete Menu Item");
        builder.setMessage("Are you sure you want to delete \"" + item.getName() + "\"?");

        builder.setPositiveButton("Delete", (dialog, which) -> {
            // TODO: Call API to delete item
            Toast.makeText(this, "Deleted: " + item.getName(), Toast.LENGTH_SHORT).show();
            // Refresh the list
            loadMenuData();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void addToOrder(MenuItem item) {
        Toast.makeText(this,
                "Added " + item.getName() + " to your order",
                Toast.LENGTH_SHORT).show();

        // TODO: Implement cart/order logic
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 100) { // AddMenuItemActivity result
                // Item added successfully
                Toast.makeText(this, "Menu item added", Toast.LENGTH_SHORT).show();
                loadMenuData(); // Refresh the list
            } else if (requestCode == 101) { // EditMenuItemActivity result
                // Check if item was updated or deleted
                String action = data.getStringExtra("action");

                if ("delete".equals(action)) {
                    // Item was deleted
                    int deletedItemId = data.getIntExtra("deletedItemId", -1);
                    Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();
                } else {
                    // Item was updated
                    MenuItem updatedItem = data.getParcelableExtra("updatedMenuItem");
                    if (updatedItem != null) {
                        Toast.makeText(this, "Updated: " + updatedItem.getName(), Toast.LENGTH_SHORT).show();
                    }
                }
                loadMenuData(); // Refresh the list
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when activity resumes
        loadMenuData();
    }

}
