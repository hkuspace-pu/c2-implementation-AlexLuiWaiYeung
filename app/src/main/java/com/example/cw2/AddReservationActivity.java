package com.example.cw2;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cw2.Reservation;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddReservationActivity extends AppCompatActivity {

    // UI Components
    private EditText etCustomerName, etCustomerPhone, etNumberOfPeople, etSpecialRequests;
    private TextView tvDate, tvTime;
    private Button btnSelectDate, btnSelectTime, btnSubmit, btnCancel;

    // Date and time
    private Calendar selectedDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reservation);

        // Get customer name from intent (if available)
        String customerName = getIntent().getStringExtra("customerName");

        initViews();
        setupDateTime();
        prefillCustomerName(customerName);
        setupButtonListeners();
    }

    private void initViews() {
        etCustomerName = findViewById(R.id.et_customer_name);
        etCustomerPhone = findViewById(R.id.et_customer_phone);
        etNumberOfPeople = findViewById(R.id.et_number_of_people);
        etSpecialRequests = findViewById(R.id.et_special_requests);
        tvDate = findViewById(R.id.tv_date);
        tvTime = findViewById(R.id.tv_time);
        btnSelectDate = findViewById(R.id.btn_select_date);
        btnSelectTime = findViewById(R.id.btn_select_time);
        btnSubmit = findViewById(R.id.btn_submit);
        btnCancel = findViewById(R.id.btn_cancel);
    }

    private void setupDateTime() {
        // Set default to tomorrow at 7:00 PM
        selectedDateTime = Calendar.getInstance();
        selectedDateTime.add(Calendar.DAY_OF_MONTH, 1);
        selectedDateTime.set(Calendar.HOUR_OF_DAY, 19);
        selectedDateTime.set(Calendar.MINUTE, 0);

        updateDateTimeDisplay();
    }

    private void prefillCustomerName(String customerName) {
        if (customerName != null && !customerName.isEmpty()) {
            etCustomerName.setText(customerName);
        }
    }

    private void setupButtonListeners() {
        // Select Date Button
        btnSelectDate.setOnClickListener(v -> showDatePicker());

        // Select Time Button
        btnSelectTime.setOnClickListener(v -> showTimePicker());

        // Submit Button
        btnSubmit.setOnClickListener(v -> createReservation());

        // Cancel Button
        btnCancel.setOnClickListener(v -> finish());
    }

    private void showDatePicker() {
        Calendar today = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(year, month, dayOfMonth);
                    updateDateTimeDisplay();
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH)
        );

        // Don't allow past dates
        datePickerDialog.getDatePicker().setMinDate(today.getTimeInMillis());

        // Set max date to 30 days from now
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.DAY_OF_MONTH, 30);
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDateTime.set(Calendar.MINUTE, minute);
                    updateDateTimeDisplay();
                },
                selectedDateTime.get(Calendar.HOUR_OF_DAY),
                selectedDateTime.get(Calendar.MINUTE),
                true // 24-hour format
        );

        timePickerDialog.show();
    }

    private void updateDateTimeDisplay() {
        // Format date
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault());
        tvDate.setText(dateFormat.format(selectedDateTime.getTime()));

        // Format time
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        tvTime.setText(timeFormat.format(selectedDateTime.getTime()));
    }

    private void createReservation() {
        // Get input values
        String customerName = etCustomerName.getText().toString().trim();
        String customerPhone = etCustomerPhone.getText().toString().trim();
        String numberOfPeopleStr = etNumberOfPeople.getText().toString().trim();
        String specialRequests = etSpecialRequests.getText().toString().trim();

        // Validation
        if (customerName.isEmpty()) {
            etCustomerName.setError("Please enter your name");
            etCustomerName.requestFocus();
            return;
        }

        if (customerPhone.isEmpty()) {
            etCustomerPhone.setError("Please enter your phone number");
            etCustomerPhone.requestFocus();
            return;
        }

        if (numberOfPeopleStr.isEmpty()) {
            etNumberOfPeople.setError("Please enter number of people");
            etNumberOfPeople.requestFocus();
            return;
        }

        int numberOfPeople;
        try {
            numberOfPeople = Integer.parseInt(numberOfPeopleStr);
            if (numberOfPeople < 1) {
                etNumberOfPeople.setError("Must be at least 1 person");
                etNumberOfPeople.requestFocus();
                return;
            }
            if (numberOfPeople > 20) {
                etNumberOfPeople.setError("Maximum 20 people per reservation");
                etNumberOfPeople.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etNumberOfPeople.setError("Please enter a valid number");
            etNumberOfPeople.requestFocus();
            return;
        }

        // Create reservation object
        Reservation newReservation = new Reservation(
                generateId(),
                customerName,
                customerPhone,
                "", // email (optional for now)
                numberOfPeople,
                selectedDateTime.getTime(),
                tvTime.getText().toString(),
                specialRequests,
                "pending" // default status
        );

        // TODO: Save to API/database
        // For now, return result

        Intent resultIntent = new Intent();
        resultIntent.putExtra("newReservation", newReservation);
        setResult(RESULT_OK, resultIntent);

        Toast.makeText(this, "Reservation submitted successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private int generateId() {
        // Generate temporary ID (in real app, this comes from API)
        return (int) (System.currentTimeMillis() & Integer.MAX_VALUE);
    }
}