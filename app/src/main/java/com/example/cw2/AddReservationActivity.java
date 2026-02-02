package com.example.cw2;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;

public class AddReservationActivity extends AppCompatActivity {

    private TextInputEditText etDate, etTime, etGuests, etRequests;
    private Button btnSubmit;
    private Button btnBack;
    private String username;
    private Calendar selectedCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reservation);

        username = getIntent().getStringExtra("username");

        // Debug log to check the value
        Log.d("AddReservation", "Received userName: " + username);

        initViews();
        setupDatePicker();
        setupTimePicker();
        setupSubmitButton();
        btnBack.setOnClickListener(v -> finish());
    }

    private void initViews() {
        etDate = findViewById(R.id.et_date);
        etTime = findViewById(R.id.et_time);
        etGuests = findViewById(R.id.et_guests);
        etRequests = findViewById(R.id.et_requests);
        btnSubmit = findViewById(R.id.btn_submit);
        btnBack = findViewById(R.id.btn_back);
    }

    private void setupDatePicker() {
        etDate.setOnClickListener(v -> {
            Calendar today = Calendar.getInstance();
            DatePickerDialog datePicker = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        selectedCalendar.set(year, month, dayOfMonth);
                        // Format date for display (day/month/year)
                        String displayDate = String.format("%d/%d/%d", dayOfMonth, month + 1, year);
                        etDate.setText(displayDate);

                        // Also format for database (yyyy-MM-dd)
                        String dbDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        // Store dbDate somewhere if needed
                    },
                    today.get(Calendar.YEAR),
                    today.get(Calendar.MONTH),
                    today.get(Calendar.DAY_OF_MONTH)
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
                    19, 30, true
            );
            timePicker.show();
        });
    }

    private void setupSubmitButton() {
        btnSubmit.setOnClickListener(v -> {
            if (validateForm()) {
                // Create new reservation - FIXED: reservationTime should be String, not Date
                Reservation newReservation = new Reservation(
                        0, // ID will be auto-generated
                        username,
                        "555-1234",  // Default phone
                        "email@example.com",  // Default email
                        Integer.parseInt(etGuests.getText().toString()),
                        selectedCalendar.getTime(),  // Date object
                        etTime.getText().toString(),  // String time (already formatted as HH:mm)
                        etRequests.getText().toString(),
                        "pending"
                );

                // Save to database
                DemoData dbHelper = new DemoData(this);
                long newId = dbHelper.addReservation(newReservation);
                newReservation.setId((int) newId);

                // Return result
                Intent resultIntent = new Intent();
                resultIntent.putExtra("newReservation", newReservation);
                setResult(RESULT_OK, resultIntent);
                finish();

                Toast.makeText(this, "Reservation submitted!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateForm() {
        if (etDate.getText().toString().isEmpty()) {
            etDate.setError("Please select a date");
            return false;
        }
        if (etTime.getText().toString().isEmpty()) {
            etTime.setError("Please select a time");
            return false;
        }
        if (etGuests.getText().toString().isEmpty()) {
            etGuests.setError("Please enter number of guests");
            return false;
        }
        try {
            int guests = Integer.parseInt(etGuests.getText().toString());
            if (guests < 1 || guests > 20) {
                etGuests.setError("Please enter 1-20 guests");
                return false;
            }
        } catch (NumberFormatException e) {
            etGuests.setError("Please enter a valid number");
            return false;
        }
        return true;
    }

}