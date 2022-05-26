package com.scan.pay.shop.scancart.View;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.scan.pay.shop.scancart.Models.ProductModel;
import com.scan.pay.shop.scancart.Models.ProductRepository;

import java.util.List;

public class ProductViewModel extends AndroidViewModel {
    private ProductRepository productRepository;
    private LiveData<List<ProductModel>> mAllProductsList;

    public ProductViewModel(@NonNull Application application) {
        super(application);
        productRepository = new ProductRepository(application);
        mAllProductsList = productRepository.getAllProducts();
    }

    public LiveData<List<ProductModel>> getAllProducts() { return mAllProductsList; }

    public void insert(ProductModel productModel) { productRepository.insert(productModel); }

}
