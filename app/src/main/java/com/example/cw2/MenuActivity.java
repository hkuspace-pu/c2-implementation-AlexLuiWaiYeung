package com.example.cw2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {
    private static final String TAG = "MenuActivity";
    private static final int REQUEST_ADD_ITEM = 1;
    private static final int REQUEST_EDIT_ITEM = 2;

    private RecyclerView recyclerView;
    private MenuAdapter adapter;
    private List<MenuItem> menuItems = new ArrayList<>();
    private FloatingActionButton fabAddItem;
    private DemoData dbHelper;

    private Boolean isStaff = false;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Log.d(TAG, "MenuActivity onCreate");

        // Initialize views
        recyclerView = findViewById(R.id.recycler_view_menu);
        fabAddItem = findViewById(R.id.fab_add_item);

        // Get user role from Intent
        isStaff = getIntent().getBooleanExtra("isStaff", false);
        Log.d(TAG, "User is staff: " + isStaff);

        // Initialize database
        dbHelper = new DemoData(this);
        dbHelper.updateAllItemsWithDinnerDiningIcon();

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        // Load initial data
        loadMenuItems();

        // Setup adapter with user role
        adapter = new MenuAdapter(menuItems, this, isStaff);
        recyclerView.setAdapter(adapter);

        // Setup click listeners
        adapter.setOnItemClickListener(new MenuAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.d(TAG, "Item clicked at position: " + position);
                MenuItem item = adapter.getItemAtPosition(position);
                if (item != null) {
                    Log.d(TAG, "Opening ViewMenuItemActivity for: " + item.getName());
                    Intent intent = new Intent(MenuActivity.this, MenuItemDetailActivity.class);
                    intent.putExtra("menu_item_id", item.getId());
                    startActivity(intent);
                } else {
                    Toast.makeText(MenuActivity.this, "Menu item not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onEditClick(int position) {
                if (isStaff) {
                    MenuItem item = adapter.getItemAtPosition(position);
                    if (item != null) {
                        Log.d(TAG, "Editing item at position " + position +
                                ", ID: " + item.getId() + ", Name: " + item.getName());

                        Intent intent = new Intent(MenuActivity.this, EditMenuItemActivity.class);
                        intent.putExtra("MENU_ITEM_ID", item.getId());  // Use consistent key
                        intent.putExtra("MENU_ITEM_NAME", item.getName()); // Optional: pass name for debugging
                        startActivityForResult(intent, REQUEST_EDIT_ITEM);
                    } else {
                        Toast.makeText(MenuActivity.this,
                                "Error: Could not find menu item", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Item is null at position: " + position);
                    }
                }
            }

            @Override
            public void onDeleteClick(int position) {
                Log.d(TAG, "Delete clicked at position: " + position);
                MenuItem item = adapter.getItemAtPosition(position);
            }
        });

        // Setup FAB visibility based on user role
        if (isStaff) {
            fabAddItem.setVisibility(View.VISIBLE);
            fabAddItem.setOnClickListener(v -> {
                Log.d(TAG, "Add button clicked");
                Intent intent = new Intent(MenuActivity.this, AddMenuItemActivity.class);
                startActivityForResult(intent, REQUEST_ADD_ITEM);
            });
        } else {
            fabAddItem.setVisibility(View.GONE);
        }
    }

    private void loadMenuItems() {
        menuItems.clear();
        List<MenuItem> loadedItems = dbHelper.getAllMenuItems();
        menuItems.addAll(loadedItems);
        Log.d(TAG, "Loaded " + menuItems.size() + " menu items");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning from edit/add activities
        loadMenuItems();
        if (adapter != null) {
            adapter.updateMenuItems(menuItems);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadMenuItems();
            adapter.updateMenuItems(menuItems);
        }
    }

}