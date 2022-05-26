package com.scan.pay.shop.scancart.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scan.pay.shop.scancart.Adapters.InvoiceListAdapter;
import com.scan.pay.shop.scancart.DataDeclaration;
import com.scan.pay.shop.scancart.Models.ProductModel;
import com.scan.pay.shop.scancart.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

/**
 * This invoice is created with lot of static and only few dynamic fields.
 * Dynamic fields mainly includes, date, time, cart items, total amount.
 */
public class InvoiceActivity extends AppCompatActivity {

    private static List<ProductModel> productModels = null;
    private static SharedPreferences invoiceSharedPref = null;
    private static long totalAmount = 0;
    private TextView txtTime, txtDate, txtAmount;
    private InvoiceListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        txtDate = findViewById(R.id.txt_date);
        txtTime = findViewById(R.id.txt_Time);
        txtAmount = findViewById(R.id.txt_amt);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        txtDate.setText(simpleDateFormat.format(calendar.getTime()));
        simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        txtTime.setText(simpleDateFormat.format(calendar.getTime()));


        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Invoice");
        actionBar.setDisplayHomeAsUpEnabled(true);

        new Thread(() -> {
            try {
                // productModels = (List<ProductModel>) getIntent().getSerializableExtra(DataDeclaration.PRODUCT_LIST);
                invoiceSharedPref = getSharedPreferences(DataDeclaration.CART_PREF, MODE_PRIVATE);
                Set<String> stringSet = invoiceSharedPref.getStringSet(DataDeclaration.ITEM_LIST, null);
                List<String> listQR = new ArrayList<>();
                listQR.addAll(stringSet);
                productModels = DataDeclaration.dbMaster.productDAO().getProductsInCart(listQR);
                totalAmount = DataDeclaration.dbMaster.productDAO().getTotalProductsInCart(listQR);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // Hold the process till data is fetched from db
        SystemClock.sleep(100);

        if (productModels != null) {
            RecyclerView recyclerView = findViewById(R.id.recyclerview_invoice_product_list);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new InvoiceListAdapter(this, productModels);
            recyclerView.setAdapter(adapter);
            txtAmount.setText(totalAmount + "");
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        invoiceSharedPref.edit().clear().apply();
        startActivity(new Intent(InvoiceActivity.this, HomeActivity.class));
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