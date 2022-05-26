package com.scan.pay.shop.scancart.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scan.pay.shop.scancart.Adapters.CartListAdapter;
import com.scan.pay.shop.scancart.DataDeclaration;
import com.scan.pay.shop.scancart.Models.ProductModel;
import com.scan.pay.shop.scancart.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Author: Kiran M.
 * Assumptions: User would add single quantity of each product.
 */
public class CartActivity extends AppCompatActivity {

    private static final String TAG = "Cart";
    private static SharedPreferences.Editor editor = null;
    private static ProductModel newProductToCardModel = null;
    private static List<ProductModel> productModels = null;
    private static String strQRTag = "";
    private static Button btnScan, btnCheckOut;
    private static SharedPreferences cartSharedPref = null;
    private CartListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("User Cart");
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Adding cart data id's to the shared preferences.
        cartSharedPref = getSharedPreferences(DataDeclaration.CART_PREF, MODE_PRIVATE);
        btnScan = findViewById(R.id.btn_new_scan_qr);
        btnCheckOut = findViewById(R.id.btn_checkout);

        btnScan.setOnClickListener(v -> startActivity(new Intent(CartActivity.this, QRScannerActivity.class)));
        btnCheckOut.setOnClickListener(v -> {
            Intent invoiceActivityIntent = new Intent(CartActivity.this, InvoiceActivity.class);
            invoiceActivityIntent.putExtra(DataDeclaration.PRODUCT_LIST, (Serializable) productModels);
            startActivity(new Intent(CartActivity.this, InvoiceActivity.class));
        });

        try {
            strQRTag = getIntent().getStringExtra(DataDeclaration.QR_TAG);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // managing shared pref on separate thread.
        new Thread(() -> {
            newProductToCardModel = DataDeclaration.dbMaster.productDAO().getProductByQR(strQRTag);
            Set<String> stringSet = new HashSet<>();
            Set<String> stringSetTemp = cartSharedPref.getStringSet(DataDeclaration.ITEM_LIST, null);
            if (stringSetTemp != null) {
                stringSet.addAll(stringSetTemp);
            }
            stringSet.add(strQRTag);
            editor = cartSharedPref.edit();
            editor.putStringSet(DataDeclaration.ITEM_LIST, stringSet);
            editor.apply();
            editor.commit();
            List<String> listQR = new ArrayList<>();
            listQR.addAll(stringSet);
            productModels = DataDeclaration.dbMaster.productDAO().getProductsInCart(listQR);
        }).start();

        // Hold the process till data is fetched from db
        SystemClock.sleep(100);

        if (productModels != null) {
            RecyclerView recyclerView = findViewById(R.id.recyclerView_cart_list);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new CartListAdapter(this, productModels);
            recyclerView.setAdapter(adapter);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(CartActivity.this, HomeActivity.class));
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}