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
    private JSONObject plurkUsers;

    public Plurks(JSONObject json) throws JSONException {
        plurks = json.getJSONArray("plurks");
        plurkUsers = json.getJSONObject("plurk_users");
    }

    public UserInfo[] getUserInfos() throws JSONException {
        int size = plurkUsers.length();;
        UserInfo[] infos = new UserInfo[size];
        int index = 0;
        for (Iterator keys = plurkUsers.keys(); keys.hasNext();) {
            long userid = Long.parseLong((String) keys.next());
            infos[index++] = getUserInfo(userid);
        }
        return infos;
    }

    public UserInfo getUserInfo(long userid) throws JSONException {
        JSONObject json = plurkUsers.getJSONObject(Long.toString(userid));
        return new UserInfo(json);
    }

    public int size() {
        return plurks.length();
    }

    public Plurk getPlurk(int index) throws JSONException {
        JSONObject json = plurks.getJSONObject(index);
        return new Plurk(json);
    }

    public List<Plurk> getSortedPlurkList() throws JSONException {
        int size = size();
        List<Plurk> list = new ArrayList(size);
        for (int x = size - 1; x >= 0; x--) {
            Plurk plurk = getPlurk(x);
            list.add(plurk);
        }
        return list;
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
