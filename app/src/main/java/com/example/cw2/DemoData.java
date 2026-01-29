package com.example.cw2;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DemoData extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "restaurant.db";
    private static final int DATABASE_VERSION = 1;

    // Menu table
    private static final String TABLE_MENU = "menu";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_PRICE = "price"; // Changed to TEXT
    private static final String COLUMN_IMAGE_URL = "image_url";

    // TODO: Add reservation table later
    // private static final String TABLE_RESERVATION = "reservation";

    public DemoData(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MENU_TABLE = "CREATE TABLE " + TABLE_MENU + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_PRICE + " TEXT," // Changed to TEXT
                + COLUMN_IMAGE_URL + " TEXT)";
        db.execSQL(CREATE_MENU_TABLE);

        // Insert demo data
        insertDemoData(db);

        // TODO: Create reservation table later
        /*
        String CREATE_RESERVATION_TABLE = "CREATE TABLE " + TABLE_RESERVATION + "("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "guest_name TEXT,"
                + "date TEXT,"
                + "time TEXT,"
                + "party_size INTEGER,"
                + "status TEXT)";
        db.execSQL(CREATE_RESERVATION_TABLE);
        */
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MENU);
        // TODO: Drop reservation table later
        // db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESERVATION);
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
            values.put(COLUMN_IMAGE_URL, item[3]);
            db.insert(TABLE_MENU, null, values);
        }

        Log.d("DatabaseHelper", "Inserted " + demoItems.length + " demo menu items");
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

        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_MENU,
                    null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)},
                    null, null, null);

            if (cursor.moveToFirst()) {
                item = new MenuItem();
                item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                item.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
                item.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
                item.setPrice(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRICE)));
                item.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL)));
            } else {
                Log.d("DatabaseHelper", "No menu item found with id: " + id);
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error getting menu item: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return item;
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
    // Rest of CRUD methods (addMenuItem, updateMenuItem, deleteMenuItem) remain similar
    // Just ensure price is handled as String
}