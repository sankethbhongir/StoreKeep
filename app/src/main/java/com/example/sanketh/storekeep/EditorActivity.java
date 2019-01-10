package com.example.sanketh.storekeep;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

public class EditorActivity extends AppCompatActivity {

    private EditText mProductEditText, mPriceEditText, mQuantityEditText, mSupplierEditText, mPhoneEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mProductEditText = findViewById(R.id.edit_product_name_view);
        mPriceEditText = findViewById(R.id.edit_price_view);
        mQuantityEditText = findViewById(R.id.edit_quantity_view);
        mSupplierEditText = findViewById(R.id.edit_supplier_view);
        mPhoneEditText = findViewById(R.id.edit_phone_view);
    }
    
}
