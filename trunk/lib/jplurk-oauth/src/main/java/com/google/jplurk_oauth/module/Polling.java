package com.google.jplurk_oauth.module;

import org.json.JSONObject;

import com.google.jplurk_oauth.Offset;
import com.google.jplurk_oauth.data.Plurks;
import com.google.jplurk_oauth.data.UnreadCount;
import com.google.jplurk_oauth.skeleton.*;
import org.json.JSONException;

public class Polling extends AbstractModule {

    public Plurks getPlurks(Offset offset) throws RequestException, JSONException {
        JSONObject json = requestBy("getPlurks").with(new Args().add("offset", offset.formatted())).in(HttpMethod.GET).thenJsonObject();
        return new Plurks(json);
    }

    public Plurks getPlurks(DateTime offset) throws RequestException, JSONException {
        JSONObject json = requestBy("getPlurks").with(new Args().add("offset", offset.toTimeOffset())).in(HttpMethod.GET).thenJsonObject();
        return new Plurks(json);
    }

    public Plurks getPlurks(Offset offset, int limit) throws RequestException, JSONException {
        JSONObject json = requestBy("getPlurks").with(new Args().add("offset", offset.formatted()).add("limit", limit)).in(HttpMethod.GET).thenJsonObject();
        return new Plurks(json);
    }

    public Plurks getPlurks(DateTime offset, int limit) throws RequestException, JSONException {
        JSONObject json = requestBy("getPlurks").with(new Args().add("offset", offset.toTimeOffset()).add("limit", limit)).in(HttpMethod.GET).thenJsonObject();
        return new Plurks(json);
    }

    public UnreadCount getUnreadCount() throws RequestException {
        JSONObject json = requestBy("getUnreadCount").withoutArgs().in(HttpMethod.GET).thenJsonObject();
        return new UnreadCount(json);
    }

    @Override
    protected String getModulePath() {
        return "/APP/Polling";
    }
}
