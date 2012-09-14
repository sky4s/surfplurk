/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.jplurk_oauth.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author skyforce
 */
public abstract class Data {

//    public String getErrorText() throws JSONException {
//        return get("error_text");
//    }
//
//    public boolean isError() {
//        return false;
//    }
    protected JSONObject json;

    public final String get(String key) {
        return json.optString(key);
//        return json.getString(key);
    }

    public final boolean hasKey(String key) {
        return json.has(key);
    }

    protected Data(JSONObject json) {
        this.json = json;
    }

    static void listKeys(JSONObject json) {
        for (Iterator it = json.keys(); it.hasNext();) {
            System.out.println(it.next());
        }
    }

    protected static int[] toIntArray(JSONArray json) throws JSONException {
        int size = json.length();
        int[] result = new int[size];
        for (int x = 0; x < size; x++) {
            result[x] = json.getInt(x);
        }
        return result;
    }
    private static SimpleDateFormat GMTDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
    private Date postedDate = null;

    protected Date getPostedDate(String posted) throws ParseException {
        if (null == postedDate) {
            postedDate = GMTDateFormat.parse(posted);
        }
        return postedDate;
    }
}
