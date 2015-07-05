package com.android.app.mybarcodescn;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
                quit("end");
            }
        });

    }

    private void quit(String cardcode) {
        Intent intent = new Intent();
        intent.putExtra("cardcode", cardcode);
        setResult(RESULT_OK, intent);
        finish();
    }

}
