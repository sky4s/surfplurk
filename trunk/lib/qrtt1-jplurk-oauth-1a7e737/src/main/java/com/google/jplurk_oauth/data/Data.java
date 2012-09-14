/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.jplurk_oauth.data;

import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author skyforce
 */
public abstract class Data {

    public abstract String get(String key) throws JSONException;

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
}
