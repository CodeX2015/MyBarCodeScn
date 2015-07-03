package com.android.app.mybarcodescn;

import java.io.Serializable;

/**
 * Created by CodeX on 24.06.2015.
 */
public class VipCard implements Serializable{
    Seller seller;
    Discount discount;

    private class Seller {
        String login;
        String stock;
        String date;
        String checksum;
        String act;
    }

    private class Discount {
        String first_name;
        String last_name;
        String patronymic;
        String phone;
        String discount_code;
        String email;
        String birthday;
        String wear_size;
        String shoes_size;
        String photo;
    }
}
