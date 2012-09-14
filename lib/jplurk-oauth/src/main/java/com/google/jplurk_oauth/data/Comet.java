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
 * @author SkyforceShen
 */
public class Comet extends Data {

    @Override
    public String toString() {
        return json.toString();
    }
//    public JSONObject getDataAsObject() {
//        return json.optJSONObject("data");
//    }
    private JSONArray data;

    private JSONObject getJSONObjectFromData(int index) throws JSONException {
        JSONObject jsonObject = data.getJSONObject(index);
        return jsonObject;
    }

    public boolean isNewResponse(int index) throws JSONException {
        JSONObject jsonObject = getJSONObjectFromData(index);
        return jsonObject.getString("type").equals("new_response");
    }

    public boolean isNewPlurk(int index) throws JSONException {
        JSONObject jsonObject = getJSONObjectFromData(index);
        return jsonObject.getString("type").equals("new_plurk");
    }

    public Plurk getPlurk(int index) throws JSONException {
        JSONObject jsonObject = getJSONObjectFromData(index);
        return new Plurk(jsonObject);
    }

    public Comment getComment(int index) throws JSONException {
        JSONObject jsonObject = getJSONObjectFromData(index);
        JSONObject comment = jsonObject.getJSONObject("response");
        return new Comment(comment, jsonObject);
    }

    public Plurk getPlurkOfComment(int index) throws JSONException {
        JSONObject jsonObject = getJSONObjectFromData(index);
        JSONObject plurk = jsonObject.getJSONObject("plurk");
        return new Plurk(plurk);
    }

    public UserInfo[] getUserInfoOfComment(int index) throws JSONException {
        JSONObject jsonObject = getJSONObjectFromData(index);
        JSONObject user = jsonObject.getJSONObject("user");
        Iterator it = user.keys();
        UserInfo[] userInfo = new UserInfo[user.length()];
        int arrayIndex = 0;
        while (it.hasNext()) {
            String key = (String) it.next();
            JSONObject userJson = user.getJSONObject(key);
            userInfo[arrayIndex++] = new UserInfo(userJson);
        }
        return userInfo;
    }

//    public 
    public void fetchData() {
        if (null == data) {
            data = json.optJSONArray("data");
        }
    }

    public boolean isDataAvailable() {
        fetchData();
        return null != data;
    }

    public int size() {
        if (isDataAvailable()) {
            return data.length();
        } else {
            return -1;
        }
    }

    public JSONArray getDataAsArray() {
        fetchData();
        return data;
    }

    public Comet(JSONObject json) {
        super(json);
    }

    public int getNewOffset() throws JSONException {
        return json.getInt("new_offset");
    }
}
