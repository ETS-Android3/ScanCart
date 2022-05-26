package com.scan.pay.shop.scancart.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scan.pay.shop.scancart.Adapters.ProductListAdapter;
import com.scan.pay.shop.scancart.DataDeclaration;
import com.scan.pay.shop.scancart.Database.RoomDBMaster;
import com.scan.pay.shop.scancart.Models.ProductModel;
import com.scan.pay.shop.scancart.R;
import com.scan.pay.shop.scancart.View.ProductViewModel;

import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private static final String[] PERMISSION = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int PERMISSION_REQUEST_CODE = 10;
    public static List<ProductModel> productModels = null;
    public static ProductViewModel productViewModel;
    private static Button btnScan = null;
    private static Button btnCheckCart = null;
    private static RecyclerView recyclerView;
    private static ProductListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initUI();

        if (!hasCameraPermission()) {
            ActivityCompat.requestPermissions(HomeActivity.this, PERMISSION, PERMISSION_REQUEST_CODE);
        } else {
            btnScan.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, QRScannerActivity.class)));
            btnCheckCart.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, CartActivity.class)));
        }
    }


    /**
     * Check if application have the required permissions, if yes app will continue,
     * if permissions are denied, then app will exit.
     * The reason behind exiting, is this application has very basic minimal functionality,
     * which won't work in case these permissions aren't granted.
     */
    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            // Checking whether user granted the permission or not.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                btnScan.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, QRScannerActivity.class)));
                btnCheckCart.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, CartActivity.class)));
            } else {
                Toast.makeText(HomeActivity.this, "Please grant permission to access application.", Toast.LENGTH_SHORT).show();
                finishAffinity();
            }
        }
    }


    /**
     * Closing the app is user presses back button from home screen.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        finishAffinity();
    }


    /**
     * To handle basic UI components.
     */
    private void initUI() {

        btnScan = findViewById(R.id.btn_scan_qr);
        btnCheckCart = findViewById(R.id.btn_check_cart);

        // Initializing DB.
        if (DataDeclaration.dbMaster == null) {
            DataDeclaration.dbMaster = RoomDBMaster.getDatabase(getApplicationContext());
        }

        // Updating product list recycler view.
        RecyclerView recyclerView = findViewById(R.id.recyclerView_product_list);
        final ProductListAdapter adapter = new ProductListAdapter(getApplicationContext(), new ProductListAdapter.ProductDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        try {
            productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
            productViewModel.getAllProducts().observe(this, products -> {
                // Update the cached copy of the products in the adapter.
                adapter.submitList(products);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}