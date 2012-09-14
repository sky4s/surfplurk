/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.jplurk_oauth.data;

import java.util.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author SkyforceShen
 */
public class Plurks {

    private JSONArray plurks;

    public Plurks(JSONObject json) throws JSONException {
        plurks = json.getJSONArray("plurks");
    }

    public int size() {
        return plurks.length();
    }

    public Plurk getPlurk(int index) throws JSONException {
     JSONObject json=   plurks.getJSONObject(index);
        return new Plurk(json);
    }

    public List<Plurk> getPlurkList() throws JSONException {
        int size = size();
        List<Plurk> list = new ArrayList(size);
        for (int x = 0; x < size; x++) {
            Plurk plurk = getPlurk(x);
            list.add(plurk);
        }
        return list;
    }
}
