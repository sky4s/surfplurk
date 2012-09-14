/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.jplurk_oauth.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author SkyforceShen
 */
public class Comment extends Data {

    private JSONObject json;

    public Comment(JSONObject json) {
        this.json = json;
    }

    public int getID() throws JSONException {
        return Integer.parseInt(get("id"));
    }

    public String getContent() throws JSONException {
        return get("content");
    }

    public String getQualifierTranslated() throws JSONException {
        return get("qualifier_translated");
    }

    public String getQualifier() throws JSONException {
        return get("qualifier");
    }

    public String getPosted() throws JSONException {
        return get("postedF");
    }

    public int getUserId() throws JSONException {
        return Integer.parseInt(get("user_id"));
    }

    public int getPlurkId() throws JSONException {
        return Integer.parseInt(get("plurk_Fid"));
    }

    @Override
    public String toString() {
        return json.toString();
    }

    @Override
    public String get(String key) throws JSONException {
        return json.getString(key);
    }
}
