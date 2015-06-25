package com.android.app.mybarcodescn;

import java.io.Serializable;

/**
 * Created by Yakovlev on 25.06.2015.
 */
public class Stock implements Serializable{
    String name;
    ProductDetails product;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProductDetails getProduct() {
        return product;
    }

    public void setProduct(ProductDetails product) {
        this.product = product;
    }
}
