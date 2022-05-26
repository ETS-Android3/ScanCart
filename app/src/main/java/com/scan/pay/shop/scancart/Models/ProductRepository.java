package com.scan.pay.shop.scancart.Models;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.scan.pay.shop.scancart.Database.RoomDBMaster;
import com.scan.pay.shop.scancart.Database.productDAO;

import java.util.List;

/**
 * This repo can be use when data needs to be added from multiple sources like API calls, other data storage options.
 * */
public class ProductRepository {
    private final productDAO mProductDao;
    private LiveData<List<ProductModel>> mAllProductsList;

    public ProductRepository(Application application) {
        RoomDBMaster db = RoomDBMaster.getDatabase(application);
        mProductDao = db.productDAO();
        try {
            mAllProductsList = (LiveData<List<ProductModel>>) mProductDao.getAllProducts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LiveData<List<ProductModel>> getAllProducts() {
        return mAllProductsList;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(ProductModel productModel) {
        RoomDBMaster.databaseWriteExecutor.execute(() -> {
            mProductDao.insertProduct(productModel);
        });
    }

}
