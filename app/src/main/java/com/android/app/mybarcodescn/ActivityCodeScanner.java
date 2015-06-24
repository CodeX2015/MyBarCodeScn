package com.android.app.mybarcodescn;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by Codex on 17.06.2015.
 */
public class ActivityCodeScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private Product product;
    private ZXingScannerView mScannerView;
    public String TAG = "BARCODEScn";

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
        Utils.getData();
        network();
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Toast.makeText(this, "Contents = " + rawResult.getText() +
                ", Format = " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();
        mScannerView.startCamera();
        Log.v(TAG, rawResult.getText()); // Prints scan results
        Log.v(TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
        product.setmBarCode(rawResult.getText());





    }

    private void network() {
        String request = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<magazin>\n" +
                "\t<seller login=\"sm1kassa2\" stock=\"kassa2\" date=\"2013-07-03 15:02:25\" checksum=\"93ddde78c632af5b550f45dd1be4e1d35192\" act=\"17\">\n" +
                "    </seller>\n" +
                "    <product barcode=\"1234567891230\" />\n" +
                "</magazin>";
        NetworkHelper.findProduct(new NetworkHelper.LoadListener() {

            @Override
            public void OnRequestComplete(Object result) {

            }

            @Override
            public void OnRequestError(Exception error) {

            }

        }, request);
    }

}
