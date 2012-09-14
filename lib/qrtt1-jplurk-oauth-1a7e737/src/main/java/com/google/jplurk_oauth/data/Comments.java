/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.jplurk_oauth.data;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author SkyforceShen
 */
public class Comments extends Data {

    private JSONObject json;

    public Comments(JSONObject json) {
        this.json = json;
    }

    public int getResponseCount() throws JSONException {
        int count = json.optInt("response_count");
        return count;
    }

    public int getResponseSeen() throws JSONException {
        return Integer.parseInt(get("response_seen"));
    }

    public List<Comment> getCommentList() throws JSONException {
        int size = getResponseCount();
        List<Comment> result = new ArrayList(size);
        JSONArray array = json.getJSONArray("responses");
        for (int x = 0; x < size; x++) {
            JSONObject json = array.getJSONObject(x);
            Comment comment = new Comment(json);
            result.add(comment);
        }
        return result;
    }

    @Override
    public String get(String key) throws JSONException {
        return json.getString(key);
    }
}
