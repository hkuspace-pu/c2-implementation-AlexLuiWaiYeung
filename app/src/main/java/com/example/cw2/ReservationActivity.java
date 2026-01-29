package com.example.cw2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cw2.ReservationAdapter;
import com.example.cw2.Reservation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReservationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmptyState, tvTitle;
    private Button btnBack, btnNewReservation;
    private ReservationAdapter adapter;
    private boolean isStaff;
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        // Get user info from intent
        isStaff = getIntent().getBooleanExtra("isStaff", false);
        currentUser = getIntent().getStringExtra("username");

        initViews();
        setupRecyclerView();
        loadReservations();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.rv_reservations);
        tvTitle = findViewById(R.id.tv_title);
        btnBack = findViewById(R.id.btn_back);
        btnNewReservation = findViewById(R.id.btn_new_reservation);

        // Set title based on user role
        tvTitle.setText(isStaff ? "All Reservations" : "My Reservations");

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // New reservation button (only for guests)
        if (isStaff) {
            btnNewReservation.setVisibility(View.GONE);
        } else {
            btnNewReservation.setVisibility(View.VISIBLE);
            btnNewReservation.setOnClickListener(v -> {
                Intent intent = new Intent(ReservationActivity.this, AddReservationActivity.class);
                intent.putExtra("customerName", currentUser);
                startActivityForResult(intent, 100);
            });
        }
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ReservationAdapter(new ArrayList<>(), isStaff,
                new ReservationAdapter.ReservationClickListener() {
                    @Override
                    public void onReservationClick(Reservation reservation) {
                        // Show reservation details
                        showReservationDetails(reservation);
                    }

                    @Override
                    public void onCancelClick(Reservation reservation) {
                        // Cancel reservation
                        cancelReservation(reservation);
                    }

                    @Override
                    public void onConfirmClick(Reservation reservation) {
                        // Confirm reservation (staff only)
                        confirmReservation(reservation);
                    }
                });

        recyclerView.setAdapter(adapter);
    }

    private void loadReservations() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.GONE);

        // Simulate API call delay
        recyclerView.postDelayed(() -> {
            List<Reservation> reservations = getSampleReservations();

            // For guests, filter to show only their reservations
            if (!isStaff && currentUser != null) {
                List<Reservation> userReservations = new ArrayList<>();
                for (Reservation r : reservations) {
                    if (currentUser.equalsIgnoreCase(r.getCustomerName())) {
                        userReservations.add(r);
                    }
                }
                adapter.setReservations(userReservations);
            } else {
                adapter.setReservations(reservations);
            }

            progressBar.setVisibility(View.GONE);
            if (adapter.getItemCount() == 0) {
                tvEmptyState.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                String emptyMessage = isStaff ?
                        "No reservations found" :
                        "You have no reservations\nTap '+ New Reservation' to book";
                tvEmptyState.setText(emptyMessage);
            } else {
                tvEmptyState.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }, 1500);
    }

    private List<Reservation> getSampleReservations() {
        List<Reservation> reservations = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        // Add sample reservations
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        reservations.add(new Reservation(1, "John Smith", "555-0123", "john@email.com",
                4, calendar.getTime(), "19:30", "Window seat preferred", "confirmed"));

        calendar.add(Calendar.DAY_OF_MONTH, 1);
        reservations.add(new Reservation(2, currentUser != null ? currentUser : "Jane Doe", "555-0456", "jane@email.com",
                2, calendar.getTime(), "20:00", "", "pending"));

        calendar.add(Calendar.DAY_OF_MONTH, 2);
        reservations.add(new Reservation(3, "Robert Johnson", "555-0789", "robert@email.com",
                6, calendar.getTime(), "18:00", "Birthday party", "confirmed"));

        calendar.add(Calendar.DAY_OF_MONTH, -1);
        reservations.add(new Reservation(4, "Mary Williams", "555-0912", "mary@email.com",
                3, calendar.getTime(), "21:00", "Vegetarian options needed", "cancelled"));

        return reservations;
    }

    private void showReservationDetails(Reservation reservation) {
        if (isStaff) {
            // For staff: Show detailed info in Toast
            String details =
                    "Customer: " + reservation.getCustomerName() + "\n" +
                            "Phone: " + reservation.getCustomerPhone() + "\n" +
                            "Email: " + reservation.getCustomerEmail() + "\n" +
                            "Date: " + reservation.getFormattedDate() + "\n" +
                            "Time: " + reservation.getFormattedTime() + "\n" +
                            "Party Size: " + reservation.getNumberOfPeople() + " people\n" +
                            "Status: " + reservation.getStatus().toUpperCase() + "\n" +
                            "Requests: " +
                            (reservation.getSpecialRequests().isEmpty() ? "None" : reservation.getSpecialRequests());

            Toast.makeText(this, details, Toast.LENGTH_LONG).show();
        } else {
            // For guests: Show simplified info in Toast
            String details =
                    "Name: " + reservation.getCustomerName() + "\n" +
                            "Phone: " + reservation.getCustomerPhone() + "\n" +
                            "Date: " + reservation.getFormattedDate() + "\n" +
                            "Time: " + reservation.getFormattedTime() + "\n" +
                            "Party Size: " + reservation.getNumberOfPeople() + " people\n" +
                            "Status: " + reservation.getStatus().toUpperCase();

            Toast.makeText(this, details, Toast.LENGTH_LONG).show();
        }
    }

    private void cancelReservation(Reservation reservation) {
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(this);

        String message = isStaff ?
                "Cancel reservation for " + reservation.getCustomerName() + "?" :
                "Cancel your reservation?";

        builder.setTitle("Cancel Reservation");
        builder.setMessage(message);

        builder.setPositiveButton("Cancel Reservation", (dialog, which) -> {
            // TODO: Call API to cancel reservation
            reservation.setStatus("cancelled");
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Reservation cancelled", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Keep", null);
        builder.show();
    }

    private void confirmReservation(Reservation reservation) {
        // Staff only - confirm a pending reservation
        reservation.setStatus("confirmed");
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Reservation confirmed", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            // New reservation added
            Reservation newReservation = data.getParcelableExtra("newReservation");
            if (newReservation != null) {
                Toast.makeText(this,
                        "Reservation created for " + newReservation.getCustomerName(),
                        Toast.LENGTH_SHORT).show();
            }
            loadReservations(); // Refresh list
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReservations();
    }
}