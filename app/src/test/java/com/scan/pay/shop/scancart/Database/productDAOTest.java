package com.scan.pay.shop.scancart.Database;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;

import com.scan.pay.shop.scancart.Models.ProductModel;

import org.junit.Before;
import org.junit.Test;

public class productDAOTest {
    RoomDBMaster dbMaster;
    productDAO productDAO;

    @Before
    public void setUp() throws Exception {
        Context context = Application.class.newInstance().getApplicationContext();
        dbMaster = Room.inMemoryDatabaseBuilder(context, RoomDBMaster.class).build();
    }

    @Test
    public void insertProduct() {
        ProductModel productModel = new ProductModel("test", 100,
                 "testQR", "imagePath");
        dbMaster.productDAO().insertProduct(productModel);
        ProductModel productModel1 = dbMaster.productDAO().getProductByQR("testQR");

        assert (productModel1 != null);
    }

    @Test
    public void deleteAll() {
        dbMaster.productDAO().deleteAll();
        assert (dbMaster.productDAO().getAllProducts() == null);
    }

    @Test
    public void getAllProducts() {
    }
}