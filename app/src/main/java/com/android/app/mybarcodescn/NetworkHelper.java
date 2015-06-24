package com.android.app.mybarcodescn;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
                    listener.OnRequestComplete(bmp);
                } catch (Exception ex) {
                    listener.OnRequestError(ex);
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
                    BufferedReader in = openConnection(urljson);
                    JsonObject jRequest = mGson.fromJson(in.readLine(), JsonObject.class);
                    in.close();
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

    public static void findProduct(final LoadListener listener, final String request) {
        mExecService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    BufferedReader in = openConnection("http://vvmarket.cloudapp.net/pos_client/api/");
                    JsonObject jRequest = mGson.fromJson(in.readLine(), JsonObject.class);
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private static BufferedReader openConnection(String url) throws Exception {

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();


        //https://www.hurl.it/
        //query is your body
        String query = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<magazin>\n" +
                "    <seller login=\"sm1kassa2\" stock=\"kassa2\" date=\"2013-07-03 15:02:25\" checksum=\"93ddde78c632af5b550f45dd1be4e1d35192\" act=\"4\">\n" +
                "    </seller>\n" +
                "    <discount \n" +
                "        first_name=\"Имя\"    \n" +
                "        last_name=\"Фамилия\" \n" +
                "        patronymic=\"Отчество\"\n" +
                "        phone=\"927777777\"\n" +
                "        discount_code=\"10002355\"\n" +
                "        email=\"someemail@mail.com\"\n" +
                "        birthday=\"1980-01-01\"\n" +
                "        wear_size=\"M\"\n" +
                "        shoes_size=\"7\"\n" +
                "        photo=\"фото клиента в формате hex\"/> \n" +
                "</magazin>";

        //connection.addRequestProperty("Content-Type", "application/" + "POST");

        // Modify connection settings
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");

        // Enable reading and writing through this connection
        connection.setDoInput(true);
        connection.setDoOutput(true);
        //connection.setChunkedStreamingMode(0);
        connection.setFixedLengthStreamingMode(2000);
        connection.setConnectTimeout(5000);



        if (query != null) {
/**
            byte[] message = query.getBytes("UTF8");
            int lngth = message.length;
            connection.setRequestProperty("Content-Length", (""+lngth));
            connection.getOutputStream().write(message);

 */

            connection.setRequestProperty("Query", query);
            connection.setRequestProperty("Content-Length", Integer.toString(query.length()));

            // Connect to server
            connection.connect();

            connection.getOutputStream().write(query.getBytes("UTF8"));

        }



        int responseCode = connection.getResponseCode();
        Log.d("Response Code ", String.valueOf(responseCode));
        if (responseCode != HttpsURLConnection.HTTP_OK) {
            throw new Exception(responseCode + " Bad Response Code");
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        return in;
    }



    public interface LoadListener {
        void OnRequestComplete(Object result);
        void OnRequestError(Exception error);
    }

}
