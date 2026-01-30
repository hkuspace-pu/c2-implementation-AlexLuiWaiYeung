package com.example.cw2;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class EditReservationActivity extends AppCompatActivity {

    private TextInputEditText etDate, etTime, etGuests, etRequests;
    private Button btnUpdate, btnCancel;

    private Reservation reservation;
    private boolean isStaff;
    private Calendar selectedCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_reservation);

        // Get reservation from intent
        reservation = getIntent().getParcelableExtra("reservation");
        isStaff = getIntent().getBooleanExtra("isStaff", false);

        if (reservation == null) {
            Toast.makeText(this, "Reservation not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        populateForm();
        setupDatePicker();
        setupTimePicker();
        setupButtons();
    }

    private void initViews() {
        etDate = findViewById(R.id.et_date);
        etTime = findViewById(R.id.et_time);
        etGuests = findViewById(R.id.et_guests);
        etRequests = findViewById(R.id.et_requests);
        btnUpdate = findViewById(R.id.btn_update);
        btnCancel = findViewById(R.id.btn_cancel);
    }

    private void populateForm() {
        // Set initial values
        selectedCalendar.setTime(reservation.getReservationDate());

        String date = String.format("%d/%d/%d",
                selectedCalendar.get(Calendar.DAY_OF_MONTH),
                selectedCalendar.get(Calendar.MONTH) + 1,
                selectedCalendar.get(Calendar.YEAR));

        etDate.setText(date);
        etTime.setText(reservation.getReservationTime());
        etGuests.setText(String.valueOf(reservation.getNumberOfPeople()));
        etRequests.setText(reservation.getSpecialRequests());

        // Staff can edit more fields
        if (isStaff) {
            // TODO: Add staff-only fields (status dropdown, etc.)
        }
    }

    private void setupDatePicker() {
        etDate.setOnClickListener(v -> {
            DatePickerDialog datePicker = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        selectedCalendar.set(year, month, dayOfMonth);
                        String date = String.format("%d/%d/%d", dayOfMonth, month + 1, year);
                        etDate.setText(date);
                    },
                    selectedCalendar.get(Calendar.YEAR),
                    selectedCalendar.get(Calendar.MONTH),
                    selectedCalendar.get(Calendar.DAY_OF_MONTH)
            );
            datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePicker.show();
        });
    }

    private void setupTimePicker() {
        etTime.setOnClickListener(v -> {
            TimePickerDialog timePicker = new TimePickerDialog(
                    this,
                    (view, hourOfDay, minute) -> {
                        selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedCalendar.set(Calendar.MINUTE, minute);
                        String time = String.format("%02d:%02d", hourOfDay, minute);
                        etTime.setText(time);
                    },
                    selectedCalendar.get(Calendar.HOUR_OF_DAY),
                    selectedCalendar.get(Calendar.MINUTE),
                    true
            );
            timePicker.show();
        });
    }

    private void setupButtons() {
        btnUpdate.setOnClickListener(v -> {
            if (validateForm()) {
                // Update reservation object
                reservation.setReservationDate(selectedCalendar.getTime());
                reservation.setReservationTime(etTime.getText().toString());
                reservation.setNumberOfPeople(Integer.parseInt(etGuests.getText().toString()));
                reservation.setSpecialRequests(etRequests.getText().toString());

                // TODO: Call API to update reservation

                // Return with updated reservation
                Intent resultIntent = new Intent();
                resultIntent.putExtra("updatedReservation", reservation);
                setResult(RESULT_OK, resultIntent);
                finish();

                Toast.makeText(this, "Reservation updated!", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> finish());
    }

    private boolean validateForm() {
        // Same validation as AddReservationActivity
        return true;
    }
}