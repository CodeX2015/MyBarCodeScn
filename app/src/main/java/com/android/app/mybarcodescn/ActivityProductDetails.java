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
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mListView = (ListView) findViewById(R.id.lv_activity);

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
                        mListView.setAdapter(new MyAdapter(stocks));
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

    private class MyAdapter extends BaseAdapter {

        ArrayList<Stock> stocks;

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
                convertView = ActivityProductDetails.this.getLayoutInflater().inflate(R.layout.row_product_details, parent, false);
                myRow.tvName = (TextView) convertView.findViewById(R.id.tvName);
                myRow.tvSeason = (TextView) convertView.findViewById(R.id.tvSeason);
                myRow.tvBatch = (TextView) convertView.findViewById(R.id.tvBatch);
                myRow.tvSize = (TextView) convertView.findViewById(R.id.tvSize);
                myRow.tvBarCode = (TextView) convertView.findViewById(R.id.tvBarcode);
                myRow.tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);
                myRow.tvDiscountPercent = (TextView) convertView.findViewById(R.id.tvDiscountPercent);
                myRow.tvEconomSum = (TextView) convertView.findViewById(R.id.tvEconomSum);
                myRow.tvTotalPrice = (TextView) convertView.findViewById(R.id.tvTotalPrice);
                convertView.setTag(myRow);
            } else {
                myRow = (MyRow) convertView.getTag();
            }


            Log.d("MYDEBUG", "ITEM -" + getItem(position).getProduct().getName());
            Log.d("MYDEBUG","TEXTVIEW -" + myRow.tvName.getText());


            myRow.tvName.setText(myRow.tvName.getText()
                    + ": " + getItem(position).getProduct().getName());



            myRow.tvSeason.setText(myRow.tvSeason.getText()
                    + ": " + getItem(position).getProduct().getSeason());
            myRow.tvBatch.setText(myRow.tvBatch.getText()
                    + ": " + getItem(position).getProduct().getBatch());
            myRow. tvSize.setText(myRow.tvSize.getText()
                    + ": " + String.valueOf(getItem(position).getProduct().getSize()));
            myRow.tvBarCode.setText(myRow.tvBarCode.getText()
                    + ": " + getItem(position).getProduct().getBarcode());
            myRow.tvPrice.setText(myRow.tvPrice.getText()
                    + ": " + String.valueOf(getItem(position).getProduct().getPrice()));
            myRow.tvDiscountPercent.setText(myRow.tvDiscountPercent.getText() +
                    ": " + String.valueOf(getItem(position).getProduct().getDiscount_percent()));
            myRow.tvEconomSum.setText(myRow.tvEconomSum.getText() +
                    ": " + String.valueOf(getItem(position).getProduct().getEconom_sum()));
            myRow.tvTotalPrice.setText(myRow.tvTotalPrice.getText() +
                    ": " + String.valueOf(getItem(position).getProduct().getTotal_price()));
            return convertView;
        }
    }

    private class MyRow {
        TextView tvName;
        TextView tvSeason;
        TextView tvBatch;
        TextView tvSize;
        private TextView tvBarCode;
        private TextView tvPrice;
        private TextView tvDiscountPercent;
        private TextView tvEconomSum;
        private TextView tvTotalPrice;
    }
}
