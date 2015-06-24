package com.android.app.mybarcodescn;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by CodeX on 24.06.2015.
 *
 */

public class NetworkHelper {
    private static ExecutorService mExecService = Executors.newCachedThreadPool();
    private static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";

    public static void getProductsDetailsByCode (){}


    public static void getImageFromUrl(final LoadListener listener, final String get_avatar_api) {
        mExecService.execute(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                try {
                    String temp;
                    if (get_avatar_api.substring(0, 6).equalsIgnoreCase("https:")) {
                        temp = get_avatar_api;
                    } else {
                        temp = "https://joby.su" + get_avatar_api;
                    }
                    String urlEncoded = Uri.encode(temp, ALLOWED_URI_CHARS);
                    url = new URL(urlEncoded);
                    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                    int responseCode = connection.getResponseCode();
                    Log.d("Response Code ", String.valueOf(responseCode));
                    if (responseCode != HttpsURLConnection.HTTP_OK) {
                        throw new Exception(responseCode + " Bad Response Code");
                    }
                    Bitmap bmp = BitmapFactory.decodeStream(connection.getInputStream());
                    listener.OnLoadComplete(bmp);
                } catch (Exception ex) {
                    listener.OnLoadError(ex);
                    Log.d("Error Connection, url: " + url, ex.getMessage());
                }
            }
        });
    }

    private static BufferedReader openConnection(String url) throws Exception {
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setConnectTimeout(5000);
        int responseCode = connection.getResponseCode();

        //query is your body
        String query = "151512135";
        connection.addRequestProperty("Content-Type", "application/" + "POST");
        if (query != null) {
            connection.setRequestProperty("Content-Length", Integer.toString(query.length()));
            connection.getOutputStream().write(query.getBytes("UTF8"));
        }

        Log.d("Response Code ", String.valueOf(responseCode));
        if (responseCode != HttpsURLConnection.HTTP_OK) {
            throw new Exception(responseCode + " Bad Response Code");
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        return in;
    }

    public interface LoadListener {
        void OnLoadComplete(Object result);
        void OnLoadError(Exception error);
    }

}
