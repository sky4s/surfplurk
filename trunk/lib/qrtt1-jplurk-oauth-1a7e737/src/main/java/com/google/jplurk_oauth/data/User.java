/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.jplurk_oauth.data;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author skyforce
 */
public class User extends Data {

    private JSONObject json;
    private int id;

    public User(JSONObject json) throws JSONException {
        String keys = (String) json.keys().next();
        id = Integer.parseInt(keys);
        this.json = (JSONObject) json.get(keys);
    }

    public String get(String key) throws JSONException {
        return json.getString(key);
    }

    public int getID() {
        return id;
    }

    public String getNickName() throws JSONException {
        return (String) get("nick_name");
    }

    public String getDisplayName() throws JSONException {
        return (String) get("display_name");
    }

    public String getFullName() throws JSONException {
        return (String) get("full_name");
    }
}
