package com.example.sanketh.storekeep;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.sanketh.storekeep.ProductData.ProductContract.ProductEntry;
import com.example.sanketh.storekeep.ProductData.ProductDbHelper;

/**
 * Displays list of products that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    private static final String LOG_TAG = CatalogActivity.class.getSimpleName();

    /**
     * Database helper that will provide us access to the database
     */
    private ProductDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new ProductDbHelper(this);

        insertProduct();
        queryData();
    }

    /**
     * Helper method to insert hardcoded product data into the database. For debugging purposes only.
     */
    private void insertProduct() {

        Log.i(LOG_TAG, "inserting Dummy Data ....");

        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and google pixel attributes are the values.
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "google pixel 2");
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, "599");
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, 1);
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, "Google");
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "855-836-3987");

        // Insert a new row for google pixel in the database, returning the ID of that new row.
        long rowId = db.insert(ProductEntry.TABLE_NAME, null, values);
        Log.i(LOG_TAG, "Number of times dummy data was inserted: " + String.valueOf(rowId));
    }

    /**
     * Query the database.
     * Always close the cursor when you're done reading from it.
     * This releases all its resources and makes it invalid.
     */
    private void queryData() {

        Log.i(LOG_TAG, "Viewing the data ...");


        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] project = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER
        };

        Log.i(LOG_TAG, ProductEntry._ID + " - " +
                ProductEntry.COLUMN_PRODUCT_NAME + " - " +
                ProductEntry.COLUMN_PRODUCT_PRICE + " - " +
                ProductEntry.COLUMN_PRODUCT_QUANTITY + " - " +
                ProductEntry.COLUMN_SUPPLIER_NAME + " - " +
                ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
        try (Cursor cursor = db.query(ProductEntry.TABLE_NAME, project, null, null, null, null, null, null)) {

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(ProductEntry._ID);
            int productNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME);
            int phoneNumberColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            // Iterate through all the returned rows in the cursor

            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String productName = cursor.getString(productNameColumnIndex);
                int productPrice = cursor.getInt(priceColumnIndex);
                int productQuantity = cursor.getInt(quantityColumnIndex);
                String supplierName = cursor.getString(supplierNameColumnIndex);
                String supplierPhoneNumber = cursor.getString(phoneNumberColumnIndex);

                // Display the values from each column of the current row in the cursor in the log
                Log.i(LOG_TAG, "\n" + currentID + " - " +
                        productName + " - " + productPrice + " - " + productQuantity + " - " + supplierName + " - " + supplierPhoneNumber + "\n");

            }
        } catch (IllegalStateException e) {
            Log.e(LOG_TAG, "Error viewing rows from database");
        }


    }
}
