package com.scan.pay.shop.scancart.Activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.scan.pay.shop.scancart.DataDeclaration;
import com.scan.pay.shop.scancart.Models.ProductModel;
import com.scan.pay.shop.scancart.R;

public class AddProductActivity extends AppCompatActivity {

    private static final String TAG = "AddProduct";
    private static final int PICK_IMAGE_GALLERY = 1;
    private static String strQRTag;
    private static String picturePath = "";
    private Button btnAddImage, btnSaveProduct;
    private EditText edProductName, edProductAmount, edProductQRCode;
    private ImageView imageViewProduct;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        context = this;
        initUI();

        btnSaveProduct.setOnClickListener(view -> {
            if (validateInput()) {

                // DB call on separate thread.
                new Thread(() -> {

                    HomeActivity.productViewModel.insert(new ProductModel(edProductName.getText().toString(),
                            Long.parseLong(edProductAmount.getText().toString()),
                            edProductQRCode.getText().toString(),
                            picturePath));
                }).start();

                // Once data is added to db , add the product to the cart list.
                Intent newActivityIntent = new Intent(AddProductActivity.this, CartActivity.class);
                newActivityIntent.putExtra(DataDeclaration.QR_TAG, strQRTag);
                startActivity(newActivityIntent);
                finish();
            } else {
                Toast.makeText(context, "Enter Valid Inputs.", Toast.LENGTH_SHORT).show();
            }
        });

        // Pick the image from the gallery for the product. Here we can provide multiple options to
        // add images like click pciture from camera, get from internet, web API or smart icons etc.
        btnAddImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_GALLERY);
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case PICK_IMAGE_GALLERY:
                    Uri selectedImage = data.getData();
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                    c.moveToFirst();
                    int columnIndex = c.getColumnIndex(filePath[0]);
                    picturePath = c.getString(columnIndex);
                    c.close();

                    Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                    imageViewProduct.setImageBitmap(thumbnail);
                    break;
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AddProductActivity.this, HomeActivity.class));
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

    private void initUI() {

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Add Product");
        actionBar.setDisplayHomeAsUpEnabled(true);

        btnAddImage = findViewById(R.id.btn_add_image);
        btnSaveProduct = findViewById(R.id.btn_save_product);
        edProductName = findViewById(R.id.ed_product_name);
        edProductAmount = findViewById(R.id.ed_amount);
        edProductQRCode = findViewById(R.id.ed_qr_code);

        imageViewProduct = findViewById(R.id.image_item);

        try {
            strQRTag = getIntent().getStringExtra(DataDeclaration.QR_TAG);
            edProductQRCode.setText(strQRTag);
        } catch (Exception e) {
            e.printStackTrace();
        }

        picturePath = "";
    }

    private boolean validateInput() {
        if (edProductName.getText().equals("")) {
            return false;
        }
        return !edProductAmount.getText().equals("");
    }
}