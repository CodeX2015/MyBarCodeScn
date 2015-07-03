package com.android.app.mybarcodescn;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.app.mybarcodescn.adapters.StickyListHeaderAdapter;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;

/**
 * Created by CodeX on 24.06.2015.
 */


//Todo create button and method for scan discont card from product details activity

public class ActivityProductDetails extends AppCompatActivity {

    private Button btnScanProduct;
    private Button btnScanCard;
    private Button btnDetails;
    private String mCardCode = "000000000000";
    private String mBarCode = "6913657077940";


    ProdDet prodDet = new ProdDet();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_product_details);

        btnScanProduct = (Button) findViewById(R.id.btnScanProduct);
        btnScanCard = (Button) findViewById(R.id.btnScanCard);

        btnDetails = (Button) findViewById(R.id.btnDetails);

        btnScanProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityProductDetails.this, ActivityCodeScanner.class);
                startActivityForResult(intent, 1);
            }
        });

        btnScanCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityProductDetails.this, ActivityCardScanner.class);
                startActivityForResult(intent, 2);
            }
        });

        btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData(mBarCode, mCardCode);
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
        prodDet.mListView = (ExpandableStickyListHeadersListView) findViewById(R.id.lv_activity);

//        prodDet.mListView.setOnTouchListener(new View.OnTouchListener() {
//            // Setting on Touch Listener for handling the touch inside ScrollView
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                // Disallow the touch request for parent scroll on touch of child view
//                v.getParent().requestDisallowInterceptTouchEvent(true);
//                return false;
//            }
//        });

        prodDet.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String sendStock = ((Stock) parent.getAdapter().getItem(position)).getName();
                Toast.makeText(ActivityProductDetails.this, sendStock, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null || resultCode != RESULT_OK) {return;}
        switch (requestCode) {
            case 1:
                Toast.makeText(this, "barcode: " + data.getStringExtra("barcode"), Toast.LENGTH_LONG).show();
                Log.d("ActivityResult", data.getStringExtra("barcode"));
                setmBarCode(data.getStringExtra("barcode"));
                setmBarCode(data.getStringExtra("barcode"));
                getData(mBarCode, mCardCode);
                break;
            case 2:
                Toast.makeText(this, "cardcode: " + data.getStringExtra("cardcode"), Toast.LENGTH_LONG).show();
                Log.d("ActivityResult", data.getStringExtra("cardcode"));
                setmCardCode(data.getStringExtra("cardcode"));
                getData(mBarCode, mCardCode);
                break;
        }
    }

    private void getData(String barcode, String cardcode) {

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
                "    </seller>\n" +
                "    <product barcode=\"" +
                barcode +
                "\" />\n" +
                "    <client barcode=\"" +
                cardcode +
                "\" />\n" +
                "</magazin>";

        NetworkHelper.findProduct(new NetworkHelper.LoadListener() {

            @Override
            public void OnRequestComplete(final Object result) {
                //Utils.deserializeXML((String) result);
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
        if (stocks != null) {
            prodDet.tvName.setText("Product name: " + stocks.get(0).getProduct().get(0).getName());
            prodDet.tvSeason.setText("Season: " + stocks.get(0).getProduct().get(0).getSeason());
            prodDet.tvBatch.setText("Batch: " + stocks.get(0).getProduct().get(0).getBatch());
            prodDet.tvBarCode.setText("BarCode: " + stocks.get(0).getProduct().get(0).getBarcode());
            prodDet.tvPrice.setText("Price: " + String.valueOf(stocks.get(0).getProduct().get(0).getPrice()));
            prodDet.tvDiscountPercent.setText("DiscountPercent: " + String.valueOf(stocks.get(0).getProduct().get(0).getDiscount_percent()));
            prodDet.tvDiscountSum.setText("DiscountSum: " + String.valueOf(stocks.get(0).getProduct().get(0).getEconom_sum()));
            prodDet.tvTotalPrice.setText("TotalPrice: " + String.valueOf(stocks.get(0).getProduct().get(0).getTotal_price()));

            prodDet.mListView.setAdapter(new StickyListHeaderAdapter(this, stocks));

            //prodDet.mListView.setAdapter(new MyAdapter(stocks));
            //Todo http://stackoverflow.com/questions/18367522/android-list-view-inside-a-scroll-view
            Utils.setMyList(prodDet.mListView);
        }
    }

    public String getmCardCode() {
        if (mCardCode == null) {
            return "";
        }
        return mCardCode;
    }

    public void setmCardCode(String mCardCode) {
        this.mCardCode = mCardCode;
    }

    public String getmBarCode() {
        if (mBarCode == null) {
            return "";
        }
        return mBarCode;
    }

    public void setmBarCode(String mBarCode) {
        this.mBarCode = mBarCode;
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
        private ExpandableStickyListHeadersListView mListView;
    }

    private void network(String barcode) {
        String request = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<Product>\n" +
                "\t<seller login=\"__Said__\" stock=\"cash_lining\" date=\"2015-06-18 16:02:25\" checksum=\"f723d56ed587de28f869ebb73e817696188aa921\" act=\"17\">\n" +
                "    </seller>\n" +
                "    <Product barcode=\"" +
                barcode +
                "\" />\n" +
                "</Product>";

        //query is your body
        String query = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<Product>\n" +
                " <seller login=\"__Said__\" stock=\"cash_lining\" date=\"2015-06-18 16:02:25\" checksum=\"f723d56ed587de28f869ebb73e817696188aa921\" act=\"4\">\n" +
                "    </seller>\n" +
                "    <discount \n" +
                "  first_name=\"ТЕСТ\" \n" +
                "  last_name=\"ТЕСТ\" \n" +
                "        patronymic=\"ТЕСТ\"\n" +
                "        phone=\"123456789\"\n" +
                "        discount_code=\"987654321\"\n" +
                "  email=\"someemail@mail.com\"\n" +
                "  birthday=\"1980-01-01\"\n" +
                "  wear_size=\"M\"\n" +
                "  shoes_size=\"7\"\n" +
                "  photo=\"фото клиента в формате hex\"/>\n" +
                "</Product>";

        NetworkHelper.findProduct(new NetworkHelper.LoadListener() {

            @Override
            public void OnRequestComplete(final Object result) {
                Utils.convertXmltoJSON((String) result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ActivityProductDetails.this, (String) result, Toast.LENGTH_LONG).show();
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
}
