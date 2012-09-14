/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.jplurk_oauth.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author SkyforceShen
 */
public class Comments extends Data {

////    private JSONObject json;
    private JSONObject plurkUsers;

    public Comments(JSONObject json) throws JSONException {
        super(json);
        plurkUsers = json.optJSONObject("friends");
//        plurkUsers = json.getJSONObject("friends");
    }

    public UserInfo getUserInfo(long userid) throws JSONException {
        if (null == plurkUsers) {
            return null;
        }
        JSONObject json = plurkUsers.getJSONObject(Long.toString(userid));
        return new UserInfo(json);
//        return null;
    }

    public UserInfo[] getUserInfos() throws JSONException {
        if (null == plurkUsers) {
            return null;
        }
        int size = plurkUsers.length();
        UserInfo[] infos = new UserInfo[size];
        int index = 0;
        for (Iterator keys = plurkUsers.keys(); keys.hasNext();) {
            long userid = Long.parseLong((String) keys.next());
            infos[index++] = getUserInfo(userid);
        }
        return infos;
    }

    public int getResponseCount() throws JSONException {
        return json.optInt("response_count");
    }

    public int getResponseSeen() throws JSONException {
        return json.getInt("response_seen");
    }

    public List<Comment> getCommentList() throws JSONException {
        JSONArray array = json.getJSONArray("responses");
        int length = array.length();
        List<Comment> result = new ArrayList(length);


        for (int x = 0; x < length; x++) {
            JSONObject jsonOfArray = array.getJSONObject(x);
            Comment comment = new Comment(jsonOfArray);
            result.add(comment);
        }
        return result;
    }
}
