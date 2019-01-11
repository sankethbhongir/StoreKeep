package com.example.sanketh.storekeep;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sanketh.storekeep.ProductData.ProductContract.ProductEntry;

/**
 * Allows user to create a new product or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = CatalogActivity.class.getSimpleName();

    private static final int EXISTING_PRODUCT_LOADER = 0;

    private EditText mProductEditText, mPriceEditText, mQuantityEditText, mSupplierEditText, mPhoneEditText;

    private Uri currentUri;
    private boolean shouldProceed = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Depending upon the current uri, change the app bar to "add" or "edit"
        currentUri = getIntent().getData();
        if (currentUri == null)
            setTitle("Add Product");
        else {
            setTitle("Edit Product");
            // Kick of the Loader
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);

        }

        mProductEditText = findViewById(R.id.edit_product_name_view);
        mPriceEditText = findViewById(R.id.edit_price_view);
        mQuantityEditText = findViewById(R.id.edit_quantity_view);
        mSupplierEditText = findViewById(R.id.edit_supplier_view);
        mPhoneEditText = findViewById(R.id.edit_phone_view);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * Get user input from editor and save new pet into database.
     */
    private void saveProduct() {

        // Read from input fields
        String productName = mProductEditText.getText().toString().trim();
        String productPrice = mPriceEditText.getText().toString().trim();
        String productQuantity = mQuantityEditText.getText().toString().trim();
        String supplierName = mSupplierEditText.getText().toString().trim();
        String supplierPhone = mPhoneEditText.getText().toString().trim();

        //checking all if all the input fields are filled of not
        if (currentUri == null || TextUtils.isEmpty(productName)
                || TextUtils.isEmpty(productPrice)
                || TextUtils.isEmpty(productQuantity)
                || TextUtils.isEmpty(supplierName)
                || TextUtils.isEmpty(supplierPhone)
                || supplierPhone.length() != 10) {
            if (TextUtils.isEmpty(productName)) {
                Toast.makeText(this, R.string.product_name_toast, Toast.LENGTH_SHORT).show();
                shouldProceed = false;
                return;
            } else {
                shouldProceed = true;
            }

            if (TextUtils.isEmpty(String.valueOf(productPrice))) {
                Toast.makeText(this, R.string.product_price_toast, Toast.LENGTH_SHORT).show();
                shouldProceed = false;
                return;
            } else {
                shouldProceed = true;
            }

            if (TextUtils.isEmpty(String.valueOf(productQuantity))) {
                Toast.makeText(this, R.string.product_quantity_toast, Toast.LENGTH_SHORT).show();
                shouldProceed = false;
                return;
            } else {
                shouldProceed = true;
            }


            if (TextUtils.isEmpty(supplierName)) {
                Toast.makeText(this, R.string.product_supplier_name_toast, Toast.LENGTH_SHORT).show();
                shouldProceed = false;
                return;
            } else {
                shouldProceed = true;
            }

            if (TextUtils.isEmpty(supplierPhone) || supplierPhone.length() != 10) {
                if (supplierPhone.length() != 10) {
                    Toast.makeText(this, R.string.product_supplier_phone_toast_2, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.product_supplier_phone_toast, Toast.LENGTH_SHORT).show();
                }
                shouldProceed = false;
                return;
            } else {
                shouldProceed = true;
            }
        }

        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, productName);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, Integer.parseInt(mPriceEditText.getText().toString().trim()));
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, Integer.parseInt(mQuantityEditText.getText().toString().trim()));
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, supplierName);
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhone);

        if (currentUri == null) {
            // Insert a new product into the provider, returning the content URI for the new pet.
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
            // Show a toast message depending on whether or not the insertion was successful
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),

                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        else{

            // Otherwise this is an EXISTING product, so update the pet with content URI: currentUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because currentUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(currentUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save product to database
                saveProduct();
                if(shouldProceed)
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);


    }

    // Creating loader for single product uri
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER
        };

        return new CursorLoader(this, currentUri, projection, null,
                null, null);
    }

    // updating the edit text views on the screen through the cursor
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()){
            // Find the columns of product attributes that we're interested in
            int productColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME);
            int phoneColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            // Read the pet attributes from the Cursor for the current product
            String productName = cursor.getString(productColumnIndex);
            int productPrice = cursor.getInt(priceColumnIndex);
            int productQuantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierColumnIndex);
            String supplierPhone = cursor.getString(phoneColumnIndex);

            mProductEditText.setText(productName);
            mPriceEditText.setText(productPrice);
            mQuantityEditText.setText(productQuantity);
            mSupplierEditText.setText(supplierName);
            mPhoneEditText.setText(supplierPhone);



        }
    }

    // Clearing out the views if the loader is invalidated
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mProductEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierEditText.setText("");
        mPhoneEditText.setText("");


    }
}
