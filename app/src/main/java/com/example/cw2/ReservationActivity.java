package com.example.cw2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.cw2.DemoData;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ReservationActivity extends AppCompatActivity implements
        ReservationAdapter.ReservationClickListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private FloatingActionButton fabAdd;
    private Toolbar toolbar;

    private ReservationAdapter adapter;
    private List<Reservation> reservationList = new ArrayList<>();

    private boolean isStaff = false;
    private String currentUsername;

    DemoData db = new DemoData(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        // Get user info
        isStaff = getIntent().getBooleanExtra("isStaff", false);
        currentUsername = getIntent().getStringExtra("username");

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupFab();
        loadReservations();
        db.debugTable();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.rv_reservations);
        progressBar = findViewById(R.id.progress_bar);
        tvEmpty = findViewById(R.id.tv_empty);
        fabAdd = findViewById(R.id.fab_add);

    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        TextView tvTitle = toolbar.findViewById(R.id.tv_title);
        tvTitle.setText(isStaff ? "All Reservations" : "My Reservations");
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ReservationAdapter(reservationList, isStaff, this);
        recyclerView.setAdapter(adapter);
    }

    private void setupFab() {
        if (isStaff) {
            fabAdd.setVisibility(View.GONE);
        } else {
            fabAdd.setVisibility(View.VISIBLE);
            fabAdd.setOnClickListener(v -> {
                // DEBUG: Log the current username
                Log.d("ReservationActivity", "FAB Clicked - currentUsername: " + currentUsername);

                Intent intent = new Intent(this, AddReservationActivity.class);
                intent.putExtra("customerName", currentUsername);

                // Also add debug to see all extras
                Bundle extras = intent.getExtras();
                Log.d("ReservationActivity", "Intent extras: " + (extras != null ? extras.toString() : "null"));

                startActivityForResult(intent, 100);
            });
        }
    }

    private void loadReservations() {
        showLoading(true);

        new Thread(() -> {
            // Use DemoData to get reservations
            DemoData dbHelper = new DemoData(this);

            List<Reservation> reservations;
            if (!isStaff) {
                // For guest: get only their reservations
                reservations = dbHelper.getReservationsByGuest(currentUsername);
            } else {
                // For staff: get all reservations
                reservations = dbHelper.getAllReservations();
            }

            runOnUiThread(() -> {
                reservationList.clear();
                reservationList.addAll(reservations);
                adapter.updateReservations(reservationList);
                checkEmptyState();
                showLoading(false);
            });
        }).start();
    }

    private List<Reservation> getSampleReservations() {
        List<Reservation> list = new ArrayList<>();
        // Add your sample reservations here
        return list;
    }

    private void filterGuestReservations() {
        List<Reservation> filtered = new ArrayList<>();
        for (Reservation r : reservationList) {
            if (r.getCustomerName().equalsIgnoreCase(currentUsername)) {
                filtered.add(r);
            }
        }
        reservationList.clear();
        reservationList.addAll(filtered);
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
    }

    private void checkEmptyState() {
        if (reservationList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    // ===== Click Listener Methods =====

    @Override
    public void onReservationClick(Reservation reservation) {
        // Show details
        String details = isStaff ?
                reservation.getFullDetailsForStaff() :
                reservation.getSimpleDetailsForGuest();

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Reservation Details")
                .setMessage(details)
                .setPositiveButton("OK", null)
                .setNeutralButton("Edit", (dialog, which) -> {
                    openEditReservation(reservation);
                })
                .show();
    }

    private void openEditReservation(Reservation reservation) {
        Intent intent = new Intent(this, EditReservationActivity.class);
        intent.putExtra("reservation", reservation);
        intent.putExtra("isStaff", isStaff);
        startActivityForResult(intent, 200);
    }

    @Override
    public void onCancelClick(Reservation reservation) {
        String message = isStaff ?
                "Cancel reservation for " + reservation.getCustomerName() + "?" :
                "Cancel your reservation?";

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Cancel Reservation")
                .setMessage(message)
                .setPositiveButton("Cancel Reservation", (dialog, which) -> {
                    reservation.setStatus("cancelled");
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "Reservation cancelled", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Keep", null)
                .show();
    }

    @Override
    public void onConfirmClick(Reservation reservation) {
        // Staff only - confirm pending reservation
        reservation.setStatus("confirmed");
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Reservation confirmed", Toast.LENGTH_SHORT).show();
    }

    // ===== Other Methods =====

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 100 || requestCode == 200) && resultCode == RESULT_OK) {
            loadReservations(); // Refresh after adding/editing
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReservations();
    }
}