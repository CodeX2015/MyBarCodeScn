package com.android.app.mybarcodescn;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
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
    private static Gson mGson = new Gson();

    public static void getProductsDetailsByCode (){}

    public static void getImageFromUrl(final LoadListener listener, final String image) {
        mExecService.execute(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                try {
                    String temp;
                    if (image.substring(0, 5).equalsIgnoreCase("http:")) {
                        temp = image;
                    } else {
                        temp = "http://vvmarket.cloudapp.net/" + image;
                    }
                    String urlEncoded = Uri.encode(temp, ALLOWED_URI_CHARS);
                    url = new URL(urlEncoded);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    int responseCode = connection.getResponseCode();
                    Log.d("Response Code ", String.valueOf(responseCode));
                    if (responseCode != HttpURLConnection.HTTP_OK) {
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

    public static void getUserDetails(final LoadListener listener, final String username) {
        mExecService.submit(new Runnable() {

            @Override
            public void run() {
                String urljson = "https://joby.su/api/users/" + username + "/";
                try {
                    //BufferedReader in = openConnection(urljson);
                    //JsonObject jRequest = mGson.fromJson(in.readLine(), JsonObject.class);
                    //in.close();
                    //UserDetails userDetails = mGson.fromJson(jRequest,
                    //        new TypeToken<UserDetails>() {}.getType());
                    //listener.OnLoadComplete(userDetails);
                } catch (Exception ex) {
                    //listener.OnLoadError(ex);
                    //log.error("Error Connection, url: " + urljson, ex);
                }
            }
        });
    }

    public static void postRequest(final RequestListener listener, final String query) {
        mExecService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    BufferedReader in = openPostConnection("http://vvmarket.cloudapp.net/pos_client/api/", query);
                    String jRequest = Utils.convertStreamToString(in);
                    in.close();
                    listener.OnRequestComplete(jRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.OnRequestError(e);
                }
            }
        });
    }

    private static BufferedReader openPostConnection(String url, String query) throws Exception {
            //https://www.hurl.it/

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

        // Modify connection settings
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("POST");
        //conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");

        // Enable reading and writing through this connection
        conn.setDoInput(true);
        conn.setDoOutput(true);

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(query);
        writer.flush();
        writer.close();
        os.close();
        conn.connect();

        int responseCode = conn.getResponseCode();
        Log.d("Response Code ", String.valueOf(responseCode));
        if (responseCode != HttpsURLConnection.HTTP_OK) {
            throw new Exception(responseCode + " Bad Response Code");
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        return in;
    }

    public interface RequestListener {
        void OnRequestComplete(Object result);
        void OnRequestError(Exception error);
    }

    public interface LoadListener {
        void OnLoadComplete(Object result);
        void OnLoadError(Exception error);
    }

}
