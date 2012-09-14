/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.jplurk_oauth.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author skyforce
 */
public class UnreadCount extends Data {


    public UnreadCount(JSONObject json) {
        super(json);
    }

    public int getAll() throws JSONException {
        return json.getInt("all");
    }

    public int getMy() throws JSONException {
        return json.getInt("my");
    }

    public int getPrivate() throws JSONException {
        return json.getInt("private");
    }

    public int getResponded() throws JSONException {
        return json.getInt("responded");
    }

    public int getFavorite() throws JSONException {
        return json.getInt("favorite");
    }

}
