package com.example.sanketh.storekeep.ProductData;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.sanketh.storekeep.ProductData.ProductContract.ProductEntry;

/** Database helper for store app. Manages database creation and version management */
public class ProductDbHelper extends SQLiteOpenHelper {

    /** LOG_TAG  for debugging*/
    private static final String LOG_TAG = ProductDbHelper.class.getSimpleName();

    /** Name of the Database file*/
    private static final String DATABASE_NAME = "store.db";

    /** Database Version. If you change the database schema, then you must increment the version by 1 */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link ProductDbHelper}.
     *
     * @param context of the app
     */
    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.i(LOG_TAG, "Creating database...");

        // Create a String that contains the SQL statement to create the pets table
      String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE " + ProductEntry.TABLE_NAME +" ("
                + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + ProductEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " BIGINT NOT NULL);";

        // Execute SQL Statement
        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);



    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
