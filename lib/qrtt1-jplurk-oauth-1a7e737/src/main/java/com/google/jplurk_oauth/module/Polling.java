package com.google.jplurk_oauth.module;

import org.json.JSONObject;

import com.google.jplurk_oauth.Offset;
import com.google.jplurk_oauth.data.Plurks;
import com.google.jplurk_oauth.skeleton.AbstractModule;
import com.google.jplurk_oauth.skeleton.Args;
import com.google.jplurk_oauth.skeleton.HttpMethod;
import com.google.jplurk_oauth.skeleton.RequestException;
import org.json.JSONException;

public class Polling extends AbstractModule {

    public Plurks getPlurks(Offset offset) throws RequestException, JSONException {
        JSONObject json = requestBy("getPlurks").with(new Args().add("offset", offset.formatted())).in(HttpMethod.GET).thenJsonObject();
        return new Plurks(json);
    }

    public JSONObject getUnreadCount() throws RequestException {
        return requestBy("getUnreadCount").withoutArgs().in(HttpMethod.GET).thenJsonObject();
    }

    @Override
    protected String getModulePath() {
        return "/APP/Polling";
    }
}
