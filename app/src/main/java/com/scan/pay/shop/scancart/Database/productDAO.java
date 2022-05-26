package com.scan.pay.shop.scancart.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.scan.pay.shop.scancart.Models.ProductModel;

import java.util.List;
import java.util.Set;

@Dao
public interface productDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertProduct(ProductModel productModel);

    @Query("DELETE FROM productmodel")
    void deleteAll();

    @Query("SELECT * FROM productmodel ORDER BY productID ASC")
    LiveData<List<ProductModel>> getAllProducts();

    @Query("SELECT * FROM productmodel WHERE productQRTag IN (:productQrTags)")
    List<ProductModel> getProductsInCart(List<String> productQrTags);

    @Query("select * from ProductModel where productQRTag = :qrTag")
    ProductModel getProductByQR(String qrTag);

    @Query("SELECT SUM(productPrice) FROM productmodel WHERE productQRTag IN (:productQrTags)")
    long getTotalProductsInCart(List<String> productQrTags);


}
