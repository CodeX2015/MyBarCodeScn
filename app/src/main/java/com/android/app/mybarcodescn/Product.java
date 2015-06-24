package com.android.app.mybarcodescn;

import java.io.Serializable;

/**
 * Created by Yakovlev on 24.06.2015.
 */
public class Product implements Serializable{

    String mBarCode;

    public void setmBarCode(String mBarCode) {
        this.mBarCode = mBarCode;
    }

    public String getmBarCode() {
        return mBarCode;
    }
}
