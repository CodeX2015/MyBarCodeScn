package com.android.app.mybarcodescn;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.thoughtworks.xstream.XStream;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;


/**
 * Created by CodeX on 24.06.2015.
 */

public class Utils {

    private static Gson mGson = new Gson();
    private static int mHeadersCount;
    private static int mHeaderHeight;
    private static SharedPreferences mPrefs = null;
    private static ExecutorService mExecService = Executors.newCachedThreadPool();
    private static ArrayList<ProductDetails> mProductsDBPrefs;
    final static String PREF_TAG = "Products";

    public static int getHeaderHeight() {
        return mHeaderHeight;
    }

    public static void setHeaderHeight(int mHeaderHeight) {
        Utils.mHeaderHeight = mHeaderHeight;
    }

    public int getmHeadersCount() {
        return mHeadersCount;
    }

    public static void setmHeadersCount(int count) {
        mHeadersCount = count;
    }

    public Utils(Context context) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREF_TAG, 0);
    }

    public static ArrayList<ProductDetails> getProductsDBPrefs() {
        return mProductsDBPrefs;
    }

    public static void setProductsDBPrefs(ArrayList<ProductDetails> mProductsDBPrefs) {
        Utils.mProductsDBPrefs = mProductsDBPrefs;
    }

    public static void loadData(final LoadListener listener, final Context context) {
        mPrefs = context.getSharedPreferences(PREF_TAG, 0);
        mExecService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    loadPref();
                    listener.OnLoadComplete(getProductsDBPrefs());
                } catch (Exception e) {
                    listener.OnLoadError(e.getMessage());
                }
            }
        });
    }

    public static void saveItem(final SaveListener listener, Context context, final ProductDetails product) {
        mPrefs = context.getSharedPreferences(PREF_TAG, 0);
        mExecService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    savePref(product);
                    listener.OnSaveComplete(true);
                } catch (Exception e) {
                    listener.OnSaveError(e.getMessage());
                }
            }
        });
    }



    private static void savePref(ProductDetails product) {
        loadPref();
        ArrayList<ProductDetails> products = getProductsDBPrefs();
        if (products != null) {
            ArrayList<ProductDetails> productsId = new ArrayList<ProductDetails>();
            for (int i = 0; i < products.size(); i++) {
                productsId.add(products.get(i));
            }
            productsId.add(product);
            products.clear();
            products.addAll(productsId);
        } else {
            products = new ArrayList<ProductDetails>();
            products.add(product);
        }
        savePrefFull(products);
    }

    private static void savePrefFull(ArrayList<ProductDetails> products) {
        if (mPrefs == null) {
            return;
        }
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.clear();
        if (products != null) {
            Gson gson = new Gson();
            String json = gson.toJson(products);
            prefsEditor.putString(PREF_TAG, json);
        }
        prefsEditor.apply();
        loadPref();
    }

    private static void loadPref() {
        if (mPrefs == null) {
            return;
        }
        Gson gson = new Gson();
        String json = mPrefs.getString(PREF_TAG, null);
        setProductsDBPrefs((ArrayList<ProductDetails>) gson.fromJson(
                json, new TypeToken<ArrayList<ProductDetails>>() {
                }.getType()));
    }

    public static void EmptyMessage(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Информация:")
                .setMessage("В корзине нет товаров.")
                .setCancelable(false)
                .setNegativeButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static JSONObject convertXmltoJSON(String xml) {
        JSONObject jsonObj = null;
        try {
            jsonObj = XML.toJSONObject(xml);
        } catch (JSONException e) {
            Log.e("JSON exception", e.getMessage());
            e.printStackTrace();
        }

        Log.d("XML", xml);
        Log.d("JSON", jsonObj.toString());

        return jsonObj;
    }

    private static void convertJSONtoMagazin(JSONObject jsonObj) {

        String json = jsonObj.toString();
        JsonObject jRequest = mGson.fromJson(json, JsonObject.class).getAsJsonObject("magazin");
        convertJsonToXml(jRequest.toString());
//        VipCard Product = mGson.fromJson(jRequest, new TypeToken<VipCard>() {
//        }.getType());
    }

    public static Product convertJSONtoProduct(JSONObject jsonObj) {
        String json = jsonObj.toString();
        JsonObject jRequest = mGson.fromJson(json, JsonObject.class).getAsJsonObject("magazin");
        Product Magazin = mGson.fromJson(jRequest, new TypeToken<Product>() {
        }.getType());
        return Magazin;
    }

    static void convertJsonToXml(String str) {
        JSONObject json = null;
        try {
            json = new JSONObject(str);
            String xml = XML.toString(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void deserializeXML(String xml) {
        XStream xstream = new XStream();
        xstream.alias("magazin", Product.class);
        xstream.alias("stock", Stock.class);
        xstream.alias("product", ProductDetails.class);
        xstream.addImplicitCollection(Product.class, "stock");
        xstream.addImplicitCollection(Stock.class, "product");
        Product magazin = (Product) xstream.fromXML(xml);
        String wewe = magazin.getDescription();
    }

    public static String convertStreamToString(BufferedReader reader) {
    /*
     * To convert the InputStream to String we use the
     * BufferedReader.readLine() method. We iterate until the BufferedReader
     * return null which means there's no more data to read. Each line will
     * appended to a StringBuilder and returned as String.
     */
        //BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString().replace("</stock>", "<product empty=\"\"/></stock>").replace("</magazin>", "<stock empty=\"\"/></magazin>");
    }

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        Date now = new Date();
        return sdfDate.format(now);
    }

    /****
     * Method for Setting the Height of the ListView dynamically.
     * *** Hack to fix the issue of not showing all the items of the ListView
     * *** when placed inside a ScrollView
     ****/
    public static void setListViewHeightBasedOnChildren(ExpandableStickyListHeadersListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            //view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            view.measure(View.MeasureSpec.makeMeasureSpec(desiredWidth, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static void setMyList(Context context, ExpandableStickyListHeadersListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        for (int i = 0; i < mHeadersCount; i++) {

            totalHeight += convertDp2Px(context);
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * listAdapter.getCount()); //+ (viewHeader.getMeasuredHeight()*mHeadersCount);
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static float convertDp2Px(Context context) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, r.getDimension(R.dimen.activity_list_header), r.getDisplayMetrics());
        return px;
    }

    public static ArrayList<ProductDetails> convertArrayList(ArrayList<Stock> stocks) {
        if (stocks != null) {
            ArrayList<ProductDetails> productDetails = new ArrayList<ProductDetails>();
            for (Stock stock : stocks) {
                if (stock.getName() != null) {
                    for (ProductDetails item : stock.getProduct()) {
                        if (item.getBarcode() != null) {
                            item.setmStockName(stock.getName());
                            item.setmStockCode(stock.getCode());
                            productDetails.add(item);
                        }
                    }
                }
            }
            return productDetails;
        }
        return null;
    }

    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public static String bytesToHex(byte[] bytes) {
        final char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public interface LoadListener {
        void OnLoadComplete(Object result);
        void OnLoadError(String error);
    }

    public interface SaveListener {
        void OnSaveComplete(boolean result);
        void OnSaveError(String error);
    }

    public interface DeleteListener {
        void OnDeleteComplete(boolean result);
        void OnDeleteError(String error);
    }

}
