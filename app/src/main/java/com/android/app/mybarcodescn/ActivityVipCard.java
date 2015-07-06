package com.android.app.mybarcodescn;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

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
    private Button btnAddPhoto;


    File directory;
    final int TYPE_PHOTO = 1;
    final int REQUEST_CODE_PHOTO = 1;
    final String TAG = "myLogs";

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
        etWear = (EditText) findViewById(R.id.etWear);
        etShoes = (EditText) findViewById(R.id.etShoes);
        ivPhoto = (ImageView) findViewById(R.id.ivPhoto);

        btnAddPhoto = (Button) findViewById(R.id.btnAddPhoto);

        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //intent.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri(TYPE_PHOTO));
                startActivityForResult(intent, REQUEST_CODE_PHOTO);
            }
        });
    }

    private Uri generateFileUri(int type) {
        File file = null;
        switch (type) {
            case TYPE_PHOTO:
                file = new File(directory.getPath() + "/" + "photo_"
                        + System.currentTimeMillis() + ".jpg");
                break;
        }
        Log.d(TAG, "fileName = " + file);
        return Uri.fromFile(file);
    }

    private void checkFields() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_CODE_PHOTO) {
            if (resultCode == RESULT_OK) {
                if (intent == null) {
                    Log.d(TAG, "Intent is null");
                } else {
                    Log.d(TAG, "Photo uri: " + intent.getData());
                    Bundle bndl = intent.getExtras();
                    if (bndl != null) {
                        Object obj = intent.getExtras().get("data");
                        if (obj instanceof Bitmap) {
                            Bitmap bitmap = (Bitmap) obj;
                            Log.d(TAG, "bitmap " + bitmap.getWidth() + " x "
                                    + bitmap.getHeight());
                            ivPhoto.setImageBitmap(bitmap);
                        }
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "Canceled");
            }
        }
    }

    private void createDirectory() {
        directory = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MyFolder");
        if (!directory.exists())
            directory.mkdirs();
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
                "\" act=\"4\">\n" +
                "</seller>\n" + "<discount first_name=\"" +
                etFName.getText().toString() +
                "\"\n" + "last_name=\"" +
                etLName.getText().toString() +
                "\"\n" + "patronymic=\"" +
                etMName.getText().toString() +
                "\"\n" + "phone=\"" +
                etPhone.getText().toString() +
                "\"\n" + "discount_code=\"" +
                etDiscountCode.getText().toString() +
                "\"\n" + "email=\"" +
                etEmail.getText().toString() +
                "\"\n" + "birthday=\"" +
                etBirthday.getText().toString() +
                "\"\n" + "wear_size=\"" +
                etWear.getText().toString() +
                "\"\n" + "shoes_size=\"" +
                //etShoes.getText().toString() +
                "\"\n" + "photo=\"" +
                Utils.bytesToHex(
                        Utils.getBytes(
                                ((BitmapDrawable) ivPhoto.getDrawable()).getBitmap())) +
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
