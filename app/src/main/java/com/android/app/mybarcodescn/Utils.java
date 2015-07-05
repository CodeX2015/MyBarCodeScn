package com.android.app.mybarcodescn;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ViewFlipper;

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

import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;


/**
 * Created by CodeX on 24.06.2015.
 */

public class Utils {

    private static Gson mGson = new Gson();
    private static int mHeadersCount;
    private static int mHeaderHeight;

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

    public static void getData() {
        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<magazin>\n" +
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
                "  photo=\"фото клиента в формате hex\"/> \n" +
                "</Product>";
        convertXmltoJSON(xml);
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
        return  px;
    }

    public static ArrayList<ProductDetails> convertArrayList(ArrayList<Stock> stocks) {
        if (stocks != null) {
            ArrayList<ProductDetails> productDetails = new ArrayList<ProductDetails>();
            for (Stock stock : stocks) {
                if (stock.getName() != null) {
                    for (ProductDetails item : stock.getProduct()) {
                        if (item.getBarcode() != null) {
                            item.setmStockName(stock.getName());
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
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}
