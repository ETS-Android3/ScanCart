package com.scan.pay.shop.scancart.Models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class ProductModel implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int productID;
    private String productName;
    @NonNull
    private String productQRTag;
    private long productPrice;
    private String productImageUri;

    public ProductModel() {

    }

    public ProductModel(String productName, long productPrice) {
        this.productName = productName;
        this.productPrice = productPrice;
    }

    public ProductModel(String productName, long productPrice, String productQRTag, String productImageUri) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.productQRTag = productQRTag;
        this.productImageUri = productImageUri;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductQRTag() {
        return productQRTag;
    }

    public void setProductQRTag(String productQRTag) {
        this.productQRTag = productQRTag;
    }

    public long getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(long productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductImageUri() {
        return productImageUri;
    }

    public void setProductImageUri(String productImageUri) {
        this.productImageUri = productImageUri;
    }
}
