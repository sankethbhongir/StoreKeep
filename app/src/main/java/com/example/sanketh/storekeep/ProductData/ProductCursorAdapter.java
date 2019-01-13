package com.example.sanketh.storekeep.ProductData;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sanketh.storekeep.ProductData.ProductContract.ProductEntry;
import com.example.sanketh.storekeep.R;

/**
 * {@link ProductCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of product data as its data source. This adapter knows
 * how to create list items for each row of product data in the {@link Cursor}.
 */
public class ProductCursorAdapter extends CursorAdapter {

    final private Context mContext;

    /**
     * Constructs a new {@link ProductCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        mContext = context;
    }


    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }


    /**
     * This method binds the product data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current product can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {


        // Find individual views that we want to modify in the list item layout
        final TextView productTextView = view.findViewById(R.id.product_text_view);
        TextView priceTextView = view.findViewById(R.id.price_text_view);
        TextView quantityTextView = view.findViewById(R.id.quantity_text_view);
        TextView supplierTextView = view.findViewById(R.id.supplier_text_view);
        Button phoneButtonView = view.findViewById(R.id.supplier_phone_button);
        Button sellButton = view.findViewById(R.id.sell_button);

        // Find the columns of product attributes that we're interested in
        int productColumnIndex = cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int supplierColumnIndex = cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_SUPPLIER_NAME);
        int phoneColumnIndex = cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

        // Read the product attributes from the Cursor for the current product
        String productName = cursor.getString(productColumnIndex);
        int productPrice = cursor.getInt(priceColumnIndex);
        final int productQuantity = cursor.getInt(quantityColumnIndex);
        String supplierName = cursor.getString(supplierColumnIndex);
        final String supplierPhone = cursor.getString(phoneColumnIndex);

        final int rowId = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry._ID));

        // To sell products by reducing quantity by 1 and updating the database with new uri
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri newUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, rowId);
                if (productQuantity > 0) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuantity - 1);
                    context.getContentResolver().update(newUri, contentValues, null, null);
                } else {
                    Toast.makeText(context, context.getString(R.string.last_item), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // To call the supplier for ordering
        phoneButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + supplierPhone)));
            }
        });


        // Update the TextViews with the attributes for the current product
        productTextView.setText(productName);
        priceTextView.setText(String.valueOf(productPrice));
        quantityTextView.setText(String.valueOf(productQuantity));
        supplierTextView.setText(supplierName);

    }
}
