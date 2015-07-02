package com.android.app.mybarcodescn;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Codex on 25.06.2015.
 */
public class ProductDetails implements Serializable {
    double price;
    String season;
    String batch;
    String name;
    double econom_sum;
    String barcode;
    double count;
    String image;
    double total_price;
    double size;
    int discount_percent;

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getDiscount_percent() {
        return discount_percent;
    }

    public void setDiscount_percent(int discount_percent) {
        this.discount_percent = discount_percent;
    }

    public double getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(long total_price) {
        this.total_price = total_price;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public double getEconom_sum() {
        return econom_sum;
    }

    public void setEconom_sum(long econom_sum) {
        this.econom_sum = econom_sum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(long price) {

    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }
}
