package com.example.sanketh.storekeep;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
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
    private MaterialButton mPlusButton, mMinusButton, mOrderButton;

    private Uri mCurrentUri;

    private boolean mProductHasChanged = false;

    private boolean mShouldProceed = true;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Depending upon the current uri, change the app bar to "add" or "edit"
        mCurrentUri = getIntent().getData();
        if (mCurrentUri == null) {
            setTitle(R.string.add_product);
            invalidateOptionsMenu();
        } else {
            setTitle(R.string.edit_product);

            // Kick of the Loader
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);

        }

        mProductEditText = findViewById(R.id.edit_product_name_view);
        mPriceEditText = findViewById(R.id.edit_price_view);
        mQuantityEditText = findViewById(R.id.edit_quantity_view);
        mSupplierEditText = findViewById(R.id.edit_supplier_view);
        mPhoneEditText = findViewById(R.id.edit_phone_view);
        mPlusButton = findViewById(R.id.plus_button);
        mMinusButton = findViewById(R.id.minus_button);
        mOrderButton = findViewById(R.id.editor_order_button);

        // decrease quantity by 1
        mMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((!TextUtils.isEmpty(mQuantityEditText.getText()) && !mQuantityEditText.getText().toString().equals("0"))) {

                    mQuantityEditText.setText(String.valueOf(Integer.parseInt(mQuantityEditText.getText().toString()) - 1));

                } else {
                    Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.quantity_toast_plu_minus), Toast.LENGTH_SHORT).show();
                }

            }
        });

        // increase the quantity by 1
        mPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mQuantityEditText.getText())) {

                    mQuantityEditText.setText(String.valueOf(Integer.parseInt(mQuantityEditText.getText().toString()) + 1));

                } else {

                    Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.quantity_toast_plu_minus), Toast.LENGTH_SHORT).show();

                }
            }
        });

        // make a call to supplier
        mOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mPhoneEditText.getText())));
            }
        });

        mPhoneEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mPhoneEditText.setOnTouchListener(mTouchListener);
        mPlusButton.setOnTouchListener(mTouchListener);
        mMinusButton.setOnTouchListener(mTouchListener);

    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new product, hide the "Delete" menu item.
        if (mCurrentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save product to database
                saveProduct();
                if (mShouldProceed)
                    // Exit activity
                    finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Triggering dialog box
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:

                if (mProductHasChanged) {
                    // Navigate back to parent activity (CatalogActivity)
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise setup the dialog to warn the user about unsaved changes
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);

                    }
                };

                // show the dialog for notifying the user about unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);


    }

    /**
     * Get user input from editor and save new product into database.
     */
    private void saveProduct() {

        // Read from input fields
        String productName = mProductEditText.getText().toString().trim();
        String productPrice = mPriceEditText.getText().toString().trim();
        String productQuantity = mQuantityEditText.getText().toString().trim();
        String supplierName = mSupplierEditText.getText().toString().trim();
        String supplierPhone = mPhoneEditText.getText().toString().trim();

        //checking all if all the input fields are filled of not
        if (mCurrentUri == null || TextUtils.isEmpty(productName)
                || TextUtils.isEmpty(productPrice)
                || TextUtils.isEmpty(productQuantity)
                || TextUtils.isEmpty(supplierName)
                || TextUtils.isEmpty(supplierPhone)
                || supplierPhone.length() != 10) {
            if (TextUtils.isEmpty(productName)) {
                Toast.makeText(this, R.string.product_name_toast, Toast.LENGTH_SHORT).show();
                mShouldProceed = false;
                return;
            } else {
                mShouldProceed = true;
            }

            if (TextUtils.isEmpty(String.valueOf(productPrice))) {
                Toast.makeText(this, R.string.product_price_toast, Toast.LENGTH_SHORT).show();
                mShouldProceed = false;
                return;
            } else {
                mShouldProceed = true;
            }

            if (TextUtils.isEmpty(String.valueOf(productQuantity))) {
                Toast.makeText(this, R.string.product_quantity_toast, Toast.LENGTH_SHORT).show();
                mShouldProceed = false;
                return;
            } else {
                mShouldProceed = true;
            }


            if (TextUtils.isEmpty(supplierName)) {
                Toast.makeText(this, R.string.product_supplier_name_toast, Toast.LENGTH_SHORT).show();
                mShouldProceed = false;
                return;
            } else {
                mShouldProceed = true;
            }

            if (TextUtils.isEmpty(supplierPhone) || supplierPhone.length() != 10) {
                if (supplierPhone.length() != 10) {
                    Toast.makeText(this, R.string.product_supplier_phone_toast_2, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.product_supplier_phone_toast, Toast.LENGTH_SHORT).show();
                }
                mShouldProceed = false;
                return;
            } else {
                mShouldProceed = true;
            }
        }

        // Create a ContentValues object where column names are the keys,
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, productName);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, Integer.parseInt(mPriceEditText.getText().toString().trim()));
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, Integer.parseInt(mQuantityEditText.getText().toString().trim()));
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, supplierName);
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhone);

        if (mCurrentUri == null) {
            // Insert a new product into the provider, returning the content URI for the new product.
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
        } else {

            // Otherwise this is an EXISTING product, so update the product with content URI: mCurrentUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentUri, values, null, null);

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

    /**
     * Perform the deletion of the product in the database.
     */
    private void deleteProduct() {
        // Only perform the delete if this is an existing product.
        if (mCurrentUri != null) {
            // Call the ContentResolver to delete the product at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentUri
            // content URI already identifies the product that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
            // Close the activity
            finish();

        }
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

        return new CursorLoader(this, mCurrentUri, projection, null,
                null, null);
    }

    // updating the edit text views on the screen through the cursor
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            // Find the columns of product attributes that we're interested in
            int productColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME);
            int phoneColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            // Read the product attributes from the Cursor for the current product
            String productName = cursor.getString(productColumnIndex);
            int productPrice = cursor.getInt(priceColumnIndex);
            int productQuantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierColumnIndex);
            String supplierPhone = cursor.getString(phoneColumnIndex);

            mProductEditText.setText(productName);
            mPriceEditText.setText(String.valueOf(productPrice));
            mQuantityEditText.setText(String.valueOf(productQuantity));
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

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener
                                                  discardButtonClickListener) {

        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.ThemeOverlay_MaterialComponents_Dialog);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null)
                    dialog.dismiss();
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }


    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.ThemeOverlay_MaterialComponents_Dialog);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}
