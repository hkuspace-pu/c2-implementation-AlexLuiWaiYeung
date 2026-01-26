package com.example.cw2;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cw2.R;
import com.example.cw2.MenuItem;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    public interface MenuClickListener {
        void onItemClick(MenuItem item);
        void onEditClick(MenuItem item);
        void onDeleteClick(MenuItem item);
        void onOrderClick(MenuItem item);
    }

    private List<MenuItem> menuItems;
    private boolean isStaffMode;
    private final MenuClickListener clickListener;

    public MenuAdapter(List<MenuItem> menuItems, boolean isStaffMode, MenuClickListener clickListener) {
        this.menuItems = menuItems;
        this.isStaffMode = isStaffMode;
        this.clickListener = clickListener;
    }

    public void setStaffMode(boolean isStaffMode) {
        this.isStaffMode = isStaffMode;
        notifyDataSetChanged();
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_menu_item, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuItem item = menuItems.get(position);
        holder.bind(item, isStaffMode, clickListener);
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {

        // 3 Columns
        private ImageView ivMenuImage;          // Column 1: Image
        private TextView tvMenuName;           // Column 2: Name
        private TextView tvMenuDescription;    // Column 2: Description
        private TextView tvMenuPrice;          // Column 3: Price

        // Action buttons
        private View layoutStaffActions;
        private Button btnEdit, btnDelete, btnOrder;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);

            // Column 1: Image
            ivMenuImage = itemView.findViewById(R.id.iv_menu_image);

            // Column 2: Name & Description
            tvMenuName = itemView.findViewById(R.id.tv_menu_name);
            tvMenuDescription = itemView.findViewById(R.id.tv_menu_description);

            // Column 3: Price
            tvMenuPrice = itemView.findViewById(R.id.tv_menu_price);

            // Action buttons
            layoutStaffActions = itemView.findViewById(R.id.layout_staff_actions);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnOrder = itemView.findViewById(R.id.btn_order);
        }

        public void bind(MenuItem item, boolean isStaffMode, MenuClickListener clickListener) {
            // Column 1: Image
            // TODO: Load image with Glide/Picasso
            // For now, use placeholder
            ivMenuImage.setImageResource(R.drawable.ic_food_placeholder);

            // Column 2: Name & Description
            tvMenuName.setText(item.getName());
            tvMenuDescription.setText(item.getDescription());

            // Column 3: Price
            tvMenuPrice.setText(String.format("$%.2f", item.getPrice()));

            // Show/hide appropriate buttons based on user role
            if (isStaffMode) {
                layoutStaffActions.setVisibility(View.VISIBLE);
                btnOrder.setVisibility(View.GONE);

                btnEdit.setOnClickListener(v -> {
                    if (clickListener != null) {
                        clickListener.onEditClick(item);
                    }
                });

                btnDelete.setOnClickListener(v -> {
                    if (clickListener != null) {
                        clickListener.onDeleteClick(item);
                    }
                });
            } else {
                layoutStaffActions.setVisibility(View.GONE);
                btnOrder.setVisibility(View.VISIBLE);

                btnOrder.setOnClickListener(v -> {
                    if (clickListener != null) {
                        clickListener.onOrderClick(item);
                    }
                });
            }

            // Item click for viewing details
            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onItemClick(item);
                }
            });
        }
    }
}
