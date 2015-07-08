package com.android.app.mybarcodescn;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by CodeX on 25.06.2015.
 */
public class Stock implements Serializable{
    String name;
    String code;
    ArrayList<ProductDetails> product = new ArrayList<ProductDetails>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ArrayList<ProductDetails> getProduct() {
        return product;
    }

    public void setProduct(ArrayList<ProductDetails> product) {
        this.product = product;
    }
}
