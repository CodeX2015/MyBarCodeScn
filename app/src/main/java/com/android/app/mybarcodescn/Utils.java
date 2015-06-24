package com.android.app.mybarcodescn;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

/**
 * Created by huligun on 24.06.2015.
 */
public class Utils {

    private static Gson mGson = new Gson();

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
                "</magazin>";
        convertXmltoJSON(xml);
    }

    private static JSONObject convertXmltoJSON(String xml) {

        JSONObject jsonObj = null;
        try {
            jsonObj = XML.toJSONObject(xml);
        } catch (JSONException e) {
            Log.e("JSON exception", e.getMessage());
            e.printStackTrace();
        }

        Log.d("XML", xml);

        Log.d("JSON", jsonObj.toString());
        convertJSONtoMagazin(jsonObj);
        return jsonObj;
    }

    private static void convertJSONtoMagazin(JSONObject jsonObj){

        String json = jsonObj.toString();
        JsonObject jRequest = mGson.fromJson(json, JsonObject.class).getAsJsonObject("magazin");
        convertJsonToXml(jRequest.toString());
        Magazin magazin = mGson.fromJson(jRequest, new TypeToken<Magazin>() {
        }.getType());
    }

    static void convertJsonToXml(String str){
        JSONObject json = null;
        try {
            json = new JSONObject(str);
            String xml = XML.toString(json);
            json =new JSONObject(str);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
