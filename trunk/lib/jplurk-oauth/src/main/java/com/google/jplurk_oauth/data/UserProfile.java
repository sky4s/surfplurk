/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.jplurk_oauth.data;

import java.io.Serializable;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author skyforceshen
 */
public class UserProfile extends Data implements Serializable {


//    public String get(String key) throws JSONException {
//        return json.getString(key);
//    }

    public UserProfile(JSONObject json) throws JSONException {
      super(json);
    }

    public UserInfo getUserInfo() throws JSONException {
        JSONObject result = json.getJSONObject("user_info");
        return new UserInfo(result);
    }
}
