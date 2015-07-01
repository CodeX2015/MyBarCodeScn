package com.android.app.mybarcodescn;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Created by CodeX on 24.06.2015.
 */

public class ActivityProductDetails extends AppCompatActivity {

    private Button btnScan;
    private Button btnDetails;

    ProdDet prodDet = new ProdDet();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_product_details);

        btnScan = (Button) findViewById(R.id.btnScan);
        btnDetails = (Button) findViewById(R.id.btnDetails);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(ActivityProductDetails.this, ActivityCodeScanner.class));
                //finish();

                Intent intent = new Intent(ActivityProductDetails.this, ActivityCodeScanner.class);
                startActivityForResult(intent, 1);
            }
        });

        btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData("6913657077940");
            }
        });


        prodDet.tvName = (TextView) findViewById(R.id.tvName);
        prodDet.tvSeason = (TextView) findViewById(R.id.tvSeason);
        prodDet.tvBatch = (TextView) findViewById(R.id.tvBatch);
        prodDet.tvBarCode = (TextView) findViewById(R.id.tvBarcode);
        prodDet.tvPrice = (TextView) findViewById(R.id.tvPrice);
        prodDet.tvTotalPrice = (TextView) findViewById(R.id.tvTotalPrice);
        prodDet.tvDiscountSum = (TextView) findViewById(R.id.tvDiscountSum);
        prodDet.tvDiscountPercent = (TextView) findViewById(R.id.tvDiscountPercent);
        prodDet.mListView = (ListView) findViewById(R.id.lv_activity);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        //getData(data.getStringExtra("barcode"));
        Toast.makeText(this, "get data by: " + data.getStringExtra("barcode"), Toast.LENGTH_LONG).show();
    }

    private void getData(String barcode) {
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
                "<magazin>\n" + "\t<seller login=\"" +
                login +
                "\" stock=\"" +
                password +
                "\" date=\"" +
                date +
                "\" checksum=\"" +
                checksum + "\" act=\"17\">\n" +
                "    </seller>\n" + "    <product barcode=\"" +
                barcode +
                "\" />\n" + "</magazin>";

        NetworkHelper.findProduct(new NetworkHelper.LoadListener() {

            @Override
            public void OnRequestComplete(final Object result) {
                final Product product = Utils.convertJSONtoProduct(Utils.convertXmltoJSON((String) result));
                final ArrayList<Stock> stocks = product.getStock();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setProductDetails(stocks);
                        //Toast.makeText(ActivityProductDetails.this, (String) result, Toast.LENGTH_LONG).show();

                    }
                });
            }

            @Override
            public void OnRequestError(final Exception error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ActivityProductDetails.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }, request);
    }

    private void setProductDetails(ArrayList<Stock> stocks) {
        prodDet.tvName.setText(prodDet.tvName.getText()
                + ": " + stocks.get(0).getProduct().getName());
        prodDet.tvSeason.setText(prodDet.tvSeason.getText()
                + ": " + stocks.get(0).getProduct().getSeason());
        prodDet.tvBatch.setText(prodDet.tvBatch.getText()
                + ": " + stocks.get(0).getProduct().getBatch());
        prodDet.tvBarCode.setText(prodDet.tvBarCode.getText()
                + ": " + stocks.get(0).getProduct().getBarcode());
        prodDet.tvPrice.setText(prodDet.tvPrice.getText()
                + ": " + String.valueOf(stocks.get(0).getProduct().getPrice()));
        prodDet.tvDiscountPercent.setText(prodDet.tvDiscountPercent.getText() +
                ": " + String.valueOf(stocks.get(0).getProduct().getDiscount_percent()));
        prodDet.tvDiscountSum.setText(prodDet.tvDiscountSum.getText() +
                ": " + String.valueOf(stocks.get(0).getProduct().getEconom_sum()));
        prodDet.tvTotalPrice.setText(prodDet.tvTotalPrice.getText() +
                ": " + String.valueOf(stocks.get(0).getProduct().getTotal_price()));

        prodDet.mListView.setAdapter(new MyAdapter(stocks));
    }

    private class MyAdapter extends BaseAdapter {

        ArrayList<Stock> stocks;
        //Todo http://stackoverflow.com/questions/18367522/android-list-view-inside-a-scroll-view

        MyAdapter(ArrayList<Stock> stocks) {
            this.stocks = stocks;
        }

        @Override
        public int getCount() {
            return stocks.size();
        }

        @Override
        public Stock getItem(int position) {
            return stocks.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyRow myRow;
            if (convertView == null) {
                myRow = new MyRow();
                convertView = ActivityProductDetails.this.getLayoutInflater().inflate(R.layout.row_list_size, parent, false);
                myRow.tvSize = (TextView) convertView.findViewById(R.id.tvSize);
                myRow.tvStock = (TextView) convertView.findViewById(R.id.tvStock);
                convertView.setTag(myRow);
            } else {
                myRow = (MyRow) convertView.getTag();
            }


            Log.d("MYDEBUG", "ITEM -" + getItem(position).getProduct().getName()+" position - " + position);
            Log.d("MYDEBUG", "ITEM -" + getItem(position).getName());
            //Log.d("MYDEBUG","TEXTVIEW -" + myRow.tvName.getText());


            myRow.tvSize.setText("Size: " + String.valueOf(getItem(position).getProduct().getSize()));
            myRow.tvStock.setText("Stock: " + String.valueOf(getItem(position).getName()));

            return convertView;
        }
    }

    private class MyRow {
        private TextView tvSize;
        private TextView tvStock;
    }

    private class ProdDet {
        private TextView tvName;
        private TextView tvSeason;
        private TextView tvBatch;
        private TextView tvBarCode;
        private TextView tvPrice;
        private TextView tvDiscountPercent;
        private TextView tvDiscountSum;
        private TextView tvTotalPrice;
        private ListView mListView;
    }
}
