package com.android.app.mybarcodescn;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by CodeX on 24.06.2015.
 */
public class Product implements Serializable{
    int status;
    String description;

    @XStreamImplicit
    ArrayList<Stock> stock = new ArrayList<Stock>();

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStock(ArrayList<Stock> stock) {
        this.stock = stock;
    }

    public ArrayList<Stock> getStock() {
        return stock;
    }

}
