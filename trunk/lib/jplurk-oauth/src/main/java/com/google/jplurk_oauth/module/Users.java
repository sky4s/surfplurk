package com.google.jplurk_oauth.module;

import com.google.jplurk_oauth.data.UserInfo;
import org.json.JSONObject;

import com.google.jplurk_oauth.skeleton.AbstractModule;
import com.google.jplurk_oauth.skeleton.HttpMethod;
import com.google.jplurk_oauth.skeleton.RequestException;
import org.json.JSONException;

public class Users extends AbstractModule {

    public JSONObject getKarmaStats() throws RequestException {
        return requestBy("getKarmaStats").withoutArgs().in(HttpMethod.GET).thenJsonObject();
    }

    public UserInfo currUser() throws RequestException, JSONException {
        JSONObject json = requestBy("currUser ").withoutArgs().in(HttpMethod.GET).thenJsonObject();
        return new UserInfo(json);
    }

    @Override
    protected String getModulePath() {
        return "/APP/Users";
    }
}
