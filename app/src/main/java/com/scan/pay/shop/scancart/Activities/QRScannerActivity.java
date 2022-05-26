package com.scan.pay.shop.scancart.Activities;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.util.Size;
import android.view.MenuItem;
import android.view.OrientationEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.scan.pay.shop.scancart.DataDeclaration;
import com.scan.pay.shop.scancart.Models.ProductModel;
import com.scan.pay.shop.scancart.R;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class QRScannerActivity extends AppCompatActivity {


    private static final String TAG = "Barcode";
    private static ProcessCameraProvider cameraProvider;
    private PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("QR Scanner");
        actionBar.setDisplayHomeAsUpEnabled(true);

        previewView = findViewById(R.id.viewFinder);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        enableCamera();

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

    /**
     * Enable camera to start scanning for barcode.
     */
    @androidx.camera.core.ExperimentalGetImage
    private void enableCamera() {
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindImageAnalysis(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @androidx.camera.core.ExperimentalGetImage
    private void bindImageAnalysis(@NonNull ProcessCameraProvider cameraProvider) {
        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder().setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this),
                imageProxy -> {
                    Image mediaImage = imageProxy.getImage();
                    if (mediaImage != null) {
                        InputImage inputImage =
                                InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
                        QRScannerActivity.this.scanBarcodes(inputImage);
                        // Adding some delay, so that system can check the image for barcode before
                        // scanning for next image, this won't show any lag in UI, but will increase
                        // efficiency for performance.
                        //
                        // This can be done in more dynamic way.
                        SystemClock.sleep(5);
                        imageProxy.close();
                        mediaImage.close();
                    }

                });
        OrientationEventListener orientationEventListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
            }
        };
        orientationEventListener.enable();
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        cameraProvider.bindToLifecycle(this, cameraSelector,
                imageAnalysis, preview);
    }

    private void scanBarcodes(InputImage image) {

        // [START get_detector]
        BarcodeScanner scanner = BarcodeScanning.getClient();
        // Or, to specify the formats to recognize:
        // BarcodeScanner scanner = BarcodeScanning.getClient(options);
        // [END get_detector]

        // [START run_detector]
        scanner.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {
                        // Task completed successfully
                        // [START_EXCLUDE]
                        // [START get_barcodes]
                        for (Barcode barcode : barcodes) {
                            Rect bounds = barcode.getBoundingBox();
                            Point[] corners = barcode.getCornerPoints();

                            String rawValue = barcode.getRawValue();

                            // Separate thread has been used to perform DB operations so it won't lag in UI.
                            new Thread(() -> {
                                ProductModel productModel = DataDeclaration.dbMaster.productDAO().getProductByQR(rawValue);

                                Intent nextActivity = null;
                                if (productModel == null) {
                                    nextActivity = new Intent(QRScannerActivity.this, AddProductActivity.class);
                                } else {
                                    nextActivity = new Intent(QRScannerActivity.this, CartActivity.class);
                                }
                                nextActivity.putExtra(DataDeclaration.QR_TAG, rawValue);
                                startActivity(nextActivity);
                                finish();
                            }).start();

                            cameraProvider.unbindAll();
                            finish();
                        }
                        // [END get_barcodes]
                        // [END_EXCLUDE]


                    }
                })
                .addOnFailureListener(e -> {
                    // Task failed with an exception
                    // ...
                    Log.e(TAG, "Barcode scanning failed." + e.getCause());

                });
        // [END run_detector]
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(QRScannerActivity.this, HomeActivity.class));
        this.finish();
    }
}