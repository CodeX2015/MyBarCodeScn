package com.android.app.mybarcodescn;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by CodeX on 05.07.2015.
 */
public class ActivityVipCard extends AppCompatActivity {

    private EditText etFName;
    private EditText etLName;
    private EditText etMName;
    private EditText etPhone;
    private EditText etDiscountCode;
    private EditText etEmail;
    private EditText etBirthday;
    private EditText etWear;
    private EditText etShoes;
    private ImageView ivPhoto;
    private Button btnAddCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vipcard);

        btnAddCard = (Button) findViewById(R.id.btnAddCard);
        btnAddCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ActivityVipCard.this, "vip card add", Toast.LENGTH_SHORT).show();
                sendRequest();
                quit("end");
            }
        });

        etFName = (EditText) findViewById(R.id.etFirstName);
        etLName = (EditText) findViewById(R.id.etLastName);
        etMName = (EditText) findViewById(R.id.etPatronymic);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etDiscountCode = (EditText) findViewById(R.id.etDiscountCode);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etBirthday = (EditText) findViewById(R.id.etBirthday);
        etFName = (EditText) findViewById(R.id.etWear);
        etFName = (EditText) findViewById(R.id.etShoes);
        ivPhoto = (ImageView) findViewById(R.id.ivPhoto);

        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void checkFields () {

    }

    private void sendRequest() {

        String date = Utils.getCurrentTimeStamp();
        String login = "__Said__";
        String password = "cash_lining";
        String checksum = "";
        try {
            checksum = Utils.SHA1(date + "#" + login + "#" + password);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String request = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<magazin>\n" +
                "\t<seller login=\"" +
                login +
                "\" stock=\"" +
                password +
                "\" date=\"" +
                date +
                "\" checksum=\"" +
                checksum +
                "\" act=\"17\">\n" +
                "</seller>\n" + "<discount first_name=\"" +
                //etFName.getText().toString() +
                "\"\n" + "last_name=\"" +
                //etLName.getText().toString() +
                "\"\n" + "patronymic=\"" +
                //etMName.getText().toString() +
                "\"\n" + "phone=\"" +
                //etPhone.getText().toString() +
                "\"\n" + "discount_code=\"" +
                //etDiscountCode.getText().toString() +
                "\"\n" + "email=\"" +
                //etEmail.getText().toString() +
                "\"\n" + "birthday=\"" +
                //etBirthday.getText().toString() +
                "\"\n" + "wear_size=\"" +
                //etWear.getText().toString() +
                "\"\n" + "shoes_size=\"" +
                //etShoes.getText().toString() +
                "\"\n" + "photo=\"" +
                //Utils.bytesToHex(
                //        Utils.getBytes(
                //                ((BitmapDrawable) ivPhoto.getDrawable()).getBitmap())) +
                "\"/>\n" + "</magazin>";


        NetworkHelper.postRequest(new NetworkHelper.RequestListener() {
            @Override
            public void OnRequestComplete(Object result) {
                final Product product = Utils.convertJSONtoProduct(Utils.convertXmltoJSON((String) result));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ActivityVipCard.this, product.getDescription() + ", status: " + product.getStatus(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void OnRequestError(final Exception error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ActivityVipCard.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }, request);
    }

    private void quit(String cardcode) {
        Intent intent = new Intent();
        intent.putExtra("cardcode", cardcode);
        setResult(RESULT_OK, intent);
        finish();
    }

}