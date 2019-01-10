package com.example.sanketh.storekeep.ProductData;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/** API  contract for store app */
public class ProductContract {

    // To prevent someone from accidentally instantiating contract class
    // by giving empty constructor
    ProductContract(){}


    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.products";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.pets/pets/ is a valid path for
     * looking at pet data. content://com.example.android.pets/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_PRODUCTS = "products";

    /** Inner class that defines the constant values for the store database
     * Each entry in the table represents a single Product
     */
    public static final class ProductEntry implements BaseColumns {

        /** The content URI to access the pet data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of products.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single product.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /** Name of database table for Products */
        public static final String TABLE_NAME = "Products";

        /** Unique ID number for Product
         *
         * Type: INTEGER
         * **/
        public static final String _ID = BaseColumns._ID;

        /** Name of the Product
         *
         * Type: TEXT
         */
        public static final String COLUMN_PRODUCT_NAME = "product_name";

        /** Price of the Product
         *
         * Type: INTEGER
         */
        public static final String COLUMN_PRODUCT_PRICE = "price";

        /** Quantity of the Product
         *
         * Type: INTEGER
         */
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";

        /** Name of the Supplier
         *
         * Type: TEXT
         */
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";

        /** Phone Number of the Supplier
         *
         * Type: Text
         */
        public static final String COLUMN_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";


    }
}
