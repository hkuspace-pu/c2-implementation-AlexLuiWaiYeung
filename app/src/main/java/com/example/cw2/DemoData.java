package com.example.cw2;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DemoData extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "restaurant.db";
    private static final int DATABASE_VERSION = 4;

    // Menu table
    private static final String TABLE_MENU = "menu";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_PRICE = "price"; // Changed to TEXT
    private static final String COLUMN_IMAGE_URL = "image_url";

    // === RESERVATION TABLE (new) ===
    public static final String TABLE_RESERVATION = "reservation";
    public static final String COLUMN_RESERVATION_ID = "reservation_id";
    public static final String COLUMN_CUSTOMER_NAME = "customer_name";
    public static final String COLUMN_CUSTOMER_PHONE = "customer_phone";
    public static final String COLUMN_CUSTOMER_EMAIL = "customer_email";
    public static final String COLUMN_NUMBER_OF_PEOPLE = "number_of_people";
    public static final String COLUMN_RESERVATION_DATE = "reservation_date";
    public static final String COLUMN_RESERVATION_TIME = "reservation_time";
    public static final String COLUMN_SPECIAL_REQUESTS = "special_requests";
    public static final String COLUMN_STATUS = "status";
    public DemoData(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create Menu Table
        String CREATE_MENU_TABLE = "CREATE TABLE " + TABLE_MENU + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_PRICE + " TEXT," // Changed to TEXT
                + COLUMN_IMAGE_URL + " TEXT)";
        db.execSQL(CREATE_MENU_TABLE);

        // Create Reservation table
        String CREATE_RESERVATION_TABLE = "CREATE TABLE " + TABLE_RESERVATION + "("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "guest_name TEXT,"      // Simple name to match queries
                + "date TEXT,"            // Store as text (not timestamp)
                + "time TEXT,"
                + "party_size INTEGER,"
                + "status TEXT DEFAULT 'pending'"
                + ")";
        db.execSQL(CREATE_RESERVATION_TABLE);

        // Insert demo data
        insertDemoData(db);
        insertSampleReservations(db);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MENU);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESERVATION);
        onCreate(db);
    }

    private void insertDemoData(SQLiteDatabase db) {
        // Your 6 demo items with string prices
        String[][] demoItems = {
                {"Burger", "Delicious beef burger with cheese", "12.99", "burger_image_url"},
                {"Pizza", "Pepperoni pizza with fresh ingredients", "15.99", "pizza_image_url"},
                {"Pasta", "Creamy carbonara pasta", "11.99", "pasta_image_url"},
                {"Salad", "Fresh garden salad", "8.99", "salad_image_url"},
                {"Steak", "Grilled ribeye steak", "24.99", "steak_image_url"},
                {"Ice Cream", "Vanilla ice cream with toppings", "6.99", "icecream_image_url"}
        };

        for (String[] item : demoItems) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, item[0]);
            values.put(COLUMN_DESCRIPTION, item[1]);
            values.put(COLUMN_PRICE, item[2]); // Already string
            values.put(COLUMN_IMAGE_URL, "drawable://ic_outline_dinner_dining_24");
            db.insert(TABLE_MENU, null, values);
        }

        Log.d("DatabaseHelper", "Inserted " + demoItems.length + " demo menu items");
    }
    public void updateAllItemsWithDinnerDiningIcon() {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            Log.d(TAG, "Updating ALL menu items with dinner_dining icon");

            ContentValues values = new ContentValues();
            values.put(COLUMN_IMAGE_URL, "drawable://ic_outline_dinner_dining_24");

            int rowsUpdated = db.update(TABLE_MENU, values, null, null);

            Log.d(TAG, "Updated " + rowsUpdated + " items with dinner_dining icon");

        } catch (Exception e) {
            Log.e(TAG, "Error updating icons: " + e.getMessage());
        } finally {
            db.close();
        }
    }
    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> menuItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_MENU,
                    null, null, null, null, null, COLUMN_NAME + " ASC");

            Log.d("DatabaseHelper", "Query returned " + cursor.getCount() + " rows");

            if (cursor.moveToFirst()) {
                do {
                    MenuItem item = new MenuItem();
                    item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                    item.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
                    item.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
                    item.setPrice(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRICE)));
                    item.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL)));

                    menuItems.add(item);
                    Log.d("DatabaseHelper", "Loaded item: " + item.getName());
                } while (cursor.moveToNext());
            } else {
                Log.d("DatabaseHelper", "No data found in menu table");
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting menu items: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return menuItems;
    }

    public MenuItem getMenuItemById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        MenuItem item = null;

        Log.d(TAG, "Getting menu item with ID: " + id);

        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_MENU,
                    null, // all columns
                    COLUMN_ID + " = ?", // where clause
                    new String[]{String.valueOf(id)}, // where args
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                item = new MenuItem();
                item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                item.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
                item.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
                item.setPrice(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRICE)));
                item.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL)));

                Log.d(TAG, "Found item: " + item.getName() + " (ID: " + item.getId() + ")");
            } else {
                Log.e(TAG, "No menu item found with ID: " + id);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting menu item by ID: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return item;
    }

    public int updateMenuItem(MenuItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = 0;

        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, item.getName());
            values.put(COLUMN_DESCRIPTION, item.getDescription());
            values.put(COLUMN_PRICE, item.getPrice());
            values.put(COLUMN_IMAGE_URL, item.getImageUrl());

            rowsAffected = db.update(TABLE_MENU, values,
                    COLUMN_ID + " = ?",
                    new String[]{String.valueOf(item.getId())});

            Log.d(TAG, "Update affected " + rowsAffected + " rows for item ID: " + item.getId());

        } catch (Exception e) {
            Log.e(TAG, "Error updating menu item: " + e.getMessage());
        } finally {
            db.close();
        }

        return rowsAffected;
    }

    public int deleteMenuItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = 0;

        try {
            rowsDeleted = db.delete(TABLE_MENU,
                    COLUMN_ID + " = ?",
                    new String[]{String.valueOf(id)});

            Log.d(TAG, "Deleted " + rowsDeleted + " rows with ID: " + id);

        } catch (Exception e) {
            Log.e(TAG, "Error deleting menu item: " + e.getMessage());
        } finally {
            db.close();
        }

        return rowsDeleted;
    }

    public long addMenuItem(MenuItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = -1;

        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, item.getName());
            values.put(COLUMN_DESCRIPTION, item.getDescription());
            values.put(COLUMN_PRICE, item.getPrice());
            values.put(COLUMN_IMAGE_URL, item.getImageUrl());

            result = db.insert(TABLE_MENU, null, values);

            Log.d(TAG, "Insert result: " + result +
                    " | Item: " + item.getName() + " | Price: " + item.getPrice());

            if (result == -1) {
                Log.e(TAG, "Failed to insert menu item");
            } else {
                Log.d(TAG, "Successfully inserted menu item with ID: " + result);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error inserting menu item: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.close();
        }

        return result;
    }


    // Add a new reservation
    public long addReservation(Reservation reservation) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = -1;

        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CUSTOMER_NAME, reservation.getCustomerName());
            values.put(COLUMN_CUSTOMER_PHONE, reservation.getCustomerPhone());
            values.put(COLUMN_CUSTOMER_EMAIL, reservation.getCustomerEmail());
            values.put(COLUMN_NUMBER_OF_PEOPLE, reservation.getNumberOfPeople());
            values.put(COLUMN_RESERVATION_DATE, reservation.getReservationDate().getTime()); // Store as timestamp
            values.put(COLUMN_RESERVATION_TIME, reservation.getReservationTime());
            values.put(COLUMN_SPECIAL_REQUESTS, reservation.getSpecialRequests());
            values.put(COLUMN_STATUS, reservation.getStatus());

            id = db.insert(TABLE_RESERVATION, null, values);

            Log.d(TAG, "Added reservation with ID: " + id +
                    " for: " + reservation.getCustomerName());

        } catch (Exception e) {
            Log.e(TAG, "Error adding reservation: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.close();
        }

        return id;
    }

    // Get all reservations
    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_RESERVATION,
                    null, null, null, null, null, "date DESC, time DESC");

            if (cursor.moveToFirst()) {
                do {
                    Reservation reservation = new Reservation();
                    reservation.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                    reservation.setCustomerName(cursor.getString(cursor.getColumnIndexOrThrow("guest_name")));

                    // Parse date (you'll need to adjust this based on your date format)
                    String dateStr = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                    reservation.setReservationDate(parseDate(dateStr)); // You need to implement parseDate

                    reservation.setReservationTime(cursor.getString(cursor.getColumnIndexOrThrow("time")));
                    reservation.setNumberOfPeople(cursor.getInt(cursor.getColumnIndexOrThrow("party_size")));
                    reservation.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));

                    // Set default values for other fields if needed
                    reservation.setCustomerPhone("555-0000");
                    reservation.setCustomerEmail("guest@email.com");
                    reservation.setSpecialRequests("");

                    reservations.add(reservation);

                } while (cursor.moveToNext());
            }

            Log.d(TAG, "Loaded " + reservations.size() + " reservations");

        } catch (Exception e) {
            Log.e(TAG, "Error getting reservations: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return reservations;
    }

    // Get reservations by guest name
    public List<Reservation> getReservationsByGuest(String guestName) {
        List<Reservation> reservations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        try {
            // Use correct column name: customer_name, not guest_name
            cursor = db.query(TABLE_RESERVATION,
                    null,
                    COLUMN_CUSTOMER_NAME + " = ?",  // Use constant!
                    new String[]{guestName},
                    null, null,
                    COLUMN_RESERVATION_DATE + " DESC, " + COLUMN_RESERVATION_TIME + " DESC");

            if (cursor.moveToFirst()) {
                do {
                    Reservation reservation = new Reservation();

                    // Use column constants, NOT hardcoded strings!
                    reservation.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RESERVATION_ID)));
                    reservation.setCustomerName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CUSTOMER_NAME)));
                    reservation.setCustomerPhone(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CUSTOMER_PHONE)));
                    reservation.setCustomerEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CUSTOMER_EMAIL)));
                    reservation.setNumberOfPeople(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NUMBER_OF_PEOPLE)));

                    // reservation_date is stored as INTEGER timestamp!
                    long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_RESERVATION_DATE));
                    reservation.setReservationDate(new Date(timestamp));

                    reservation.setReservationTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESERVATION_TIME)));
                    reservation.setSpecialRequests(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SPECIAL_REQUESTS)));
                    reservation.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));

                    reservations.add(reservation);

                    Log.d(TAG, "Loaded reservation for: " + reservation.getCustomerName() +
                            " | Date: " + reservation.getFormattedDate());

                } while (cursor.moveToNext());
            }

            Log.d(TAG, "Found " + reservations.size() + " reservations for guest: " + guestName);

        } catch (Exception e) {
            Log.e(TAG, "Error getting guest reservations: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return reservations;
    }

    // Get reservation by ID
    public Reservation getReservationById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Reservation reservation = null;

        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_RESERVATION,
                    null,
                    "id = ?",
                    new String[]{String.valueOf(id)},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                reservation = new Reservation();
                reservation.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                reservation.setCustomerName(cursor.getString(cursor.getColumnIndexOrThrow("guest_name")));

                String dateStr = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                reservation.setReservationDate(parseDate(dateStr));

                reservation.setReservationTime(cursor.getString(cursor.getColumnIndexOrThrow("time")));
                reservation.setNumberOfPeople(cursor.getInt(cursor.getColumnIndexOrThrow("party_size")));
                reservation.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));

                reservation.setCustomerPhone("555-0000");
                reservation.setCustomerEmail("guest@email.com");
                reservation.setSpecialRequests("");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error getting reservation by ID: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return reservation;
    }

    // Update reservation
    public int updateReservation(Reservation reservation) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = 0;

        try {
            ContentValues values = new ContentValues();
            values.put("guest_name", reservation.getCustomerName());
            values.put("date", reservation.getFormattedDate());
            values.put("time", reservation.getReservationTime());
            values.put("party_size", reservation.getNumberOfPeople());
            values.put("status", reservation.getStatus());

            rowsAffected = db.update(TABLE_RESERVATION, values,
                    "id = ?",
                    new String[]{String.valueOf(reservation.getId())});

            Log.d(TAG, "Updated " + rowsAffected + " reservation(s) for ID: " + reservation.getId());

        } catch (Exception e) {
            Log.e(TAG, "Error updating reservation: " + e.getMessage());
        } finally {
            db.close();
        }

        return rowsAffected;
    }

    // Delete reservation
    public int deleteReservation(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = 0;

        try {
            rowsDeleted = db.delete(TABLE_RESERVATION,
                    "id = ?",
                    new String[]{String.valueOf(id)});

            Log.d(TAG, "Deleted " + rowsDeleted + " reservation(s) with ID: " + id);

        } catch (Exception e) {
            Log.e(TAG, "Error deleting reservation: " + e.getMessage());
        } finally {
            db.close();
        }

        return rowsDeleted;
    }

    // Helper method to parse date string
    private Date parseDate(String dateStr) {
        try {
            // Assuming format like "Wed, Jan 29, 2025"
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault());
            return sdf.parse(dateStr);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing date: " + dateStr);
            return new Date(); // Return current date as fallback
        }
    }

    // Insert sample reservation data
    private void insertSampleReservations(SQLiteDatabase db) {
        try {
            Calendar calendar = Calendar.getInstance();
            // Sample reservations
            String[][] sampleReservations = {
                    {"John Smith", "Wed, Jan 29, 2025", "19:30", "4", "confirmed"},
                    {"Jane Doe", "Thu, Jan 30, 2025", "20:00", "2", "confirmed"},
                    {"Robert Johnson", "Fri, Jan 31, 2025", "18:00", "6", "pending"},
                    {"Mary Williams", "Sat, Feb 1, 2025", "21:00", "3", "pending"},
                    {"David Brown", "Sun, Feb 2, 2025", "19:00", "5", "confirmed"},
                    {"Lisa Wilson", "Mon, Feb 3, 2025", "20:30", "4", "cancelled"}
            };

            for (int i = 1; i <= 6; i++) {
                ContentValues values = new ContentValues();

                values.put(COLUMN_CUSTOMER_NAME, "Guest " + i);
                values.put(COLUMN_CUSTOMER_PHONE, "555-010" + i);
                values.put(COLUMN_CUSTOMER_EMAIL, "guest" + i + "@email.com");
                values.put(COLUMN_NUMBER_OF_PEOPLE, i + 1);

                // Set date to today + i days
                calendar.add(Calendar.DAY_OF_MONTH, i);
                values.put(COLUMN_RESERVATION_DATE, calendar.getTimeInMillis());

                values.put(COLUMN_RESERVATION_TIME, "19:" + (i < 10 ? "0" + i : i));
                values.put(COLUMN_SPECIAL_REQUESTS, "Sample request " + i);
                values.put(COLUMN_STATUS, i % 2 == 0 ? "confirmed" : "pending");

                db.insert(TABLE_RESERVATION, null, values);
            }

            Log.d(TAG, "Inserted " + sampleReservations.length + " sample reservations");

        } catch (Exception e) {
            Log.e(TAG, "Error inserting sample reservations: " + e.getMessage());
        }
    }
    public void debugTable() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("PRAGMA table_info(reservation)", null);

        Log.d("DEBUG", "=== Table Schema ===");
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(1);
                String type = cursor.getString(2);
                Log.d("DEBUG", "Column: " + name + " (" + type + ")");
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }
}