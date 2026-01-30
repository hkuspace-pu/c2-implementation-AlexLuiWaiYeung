package com.example.cw2;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {
    private List<MenuItem> menuItems;
    private Context context;
    private Boolean isStaff; // "staff" or "guest"
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onEditClick(int position);
        void onDeleteClick(int position);
    }

    public MenuAdapter(List<MenuItem> menuItems, Context context, boolean isStaff) {
        this.menuItems = new ArrayList<>(); // Always initialize
        if (menuItems != null) {
            this.menuItems.addAll(menuItems);
        }
        this.context = context;
        this.isStaff = isStaff;

        Log.d(TAG, "Adapter created with " + this.menuItems.size() + " items");
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "Creating view holder");

        // Inflate the layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_menu_item, parent, false);

        // DEBUG: Set background color to see the item
        view.setBackgroundColor(Color.parseColor("#E8F5E9")); // Light green

        return new MenuViewHolder(view);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        Log.d(TAG, "Binding position " + position + " of " + menuItems.size());

        if (menuItems == null || position >= menuItems.size()) {
            Log.e(TAG, "Invalid position or null list");
            return;
        }

        MenuItem item = menuItems.get(position);
        Log.d(TAG, "Item at position " + position + ": " + item.getName());

        // CRITICAL: Make sure views exist and set text
        if (holder.itemName != null) {
            holder.itemName.setText(item.getName());
            holder.itemName.setTextColor(Color.BLACK);
            Log.d(TAG, "Set name: " + item.getName());
        } else {
            Log.e(TAG, "itemName is NULL!");
        }

        if (holder.itemDescription != null) {
            holder.itemDescription.setText(item.getDescription());
            Log.d(TAG, "Set description: " + item.getDescription());
        }

        if (holder.itemPrice != null) {
            holder.itemPrice.setText("$" + item.getPrice());
            holder.itemPrice.setTextColor(Color.RED);
            Log.d(TAG, "Set price: $" + item.getPrice());
        }

        // Set button visibility based on staff status
        if (holder.editButton != null && holder.deleteButton != null) {
            if (isStaff) {
                holder.editButton.setVisibility(View.VISIBLE);
                holder.deleteButton.setVisibility(View.GONE);

                // Set click listeners for buttons
                holder.editButton.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onEditClick(position);
                    }
                });

                holder.deleteButton.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onDeleteClick(position);
                    }
                });
            } else {
                holder.editButton.setVisibility(View.GONE);
                holder.deleteButton.setVisibility(View.GONE);
            }
        }

        loadDinnerDiningIcon(holder, item);

        // Set click listener for entire item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position);
            }
        });
    }
    private void loadDinnerDiningIcon(MenuViewHolder holder, MenuItem item) {
        try {
            // Method 1: Directly set the icon (if not using URL)
            holder.itemImage.setImageResource(R.drawable.ic_outline_dinner_dining_24);

        } catch (Exception e) {
            Log.e(TAG, "Error loading dinner_dining icon: " + e.getMessage());

            // Fallback to Android default icon
            holder.itemImage.setImageResource(android.R.drawable.ic_menu_camera);
        }
    }
    @Override
    public int getItemCount() {
        return menuItems == null ? 0 : menuItems.size();
    }

    public void updateMenuItems(List<MenuItem> newItems) {
        if (menuItems != null) {
            menuItems.clear();
            menuItems.addAll(newItems);
            notifyDataSetChanged();
        }
    }

    public MenuItem getItemAtPosition(int position) {
        if (position >= 0 && position < getItemCount()) {
            return menuItems.get(position);
        }
        return null;
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName, itemDescription, itemPrice;
        Button editButton, deleteButton;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            itemName = itemView.findViewById(R.id.item_name);
            itemDescription = itemView.findViewById(R.id.item_description);
            itemPrice = itemView.findViewById(R.id.item_price);
            editButton = itemView.findViewById(R.id.btn_edit);
            deleteButton = itemView.findViewById(R.id.btn_delete);
        }
    }
}