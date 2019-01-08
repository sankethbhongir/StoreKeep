package com.example.sanketh.storekeep.ProductData;

import android.provider.BaseColumns;

/** API  contract for store app */
public class ProductContract {

    // To prevent someone from accidentally instantiating contract class
    // by giving empty constructor
    ProductContract(){}

    /** Inner class that defines the constant values for the store database
     * Each entry in the table represents a single Product
     */
    public static final class ProductEntry implements BaseColumns {

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
         * Type: BIGINT
         */
        public static final String COLUMN_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";


    }
}
