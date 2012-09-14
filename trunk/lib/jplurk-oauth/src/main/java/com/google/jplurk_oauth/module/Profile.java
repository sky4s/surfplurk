package com.google.jplurk_oauth.module;

import com.google.jplurk_oauth.data.UserInfo;
import com.google.jplurk_oauth.data.UserProfile;
import org.json.JSONObject;

import com.google.jplurk_oauth.skeleton.AbstractModule;
import com.google.jplurk_oauth.skeleton.Args;
import com.google.jplurk_oauth.skeleton.HttpMethod;
import com.google.jplurk_oauth.skeleton.RequestException;
import org.json.JSONException;

public class Profile extends AbstractModule {

    @Override
    protected String getModulePath() {
        return "/APP/Profile";
    }

    public UserProfile getOwnProfile() throws RequestException, JSONException {
        JSONObject json = requestBy("getOwnProfile").withoutArgs().in(HttpMethod.GET).thenJsonObject();
        return new UserProfile(json);
    }

    public UserProfile getPublicProfile(Long userId) throws RequestException, JSONException {
        JSONObject json = requestBy("getPublicProfile").with(new Args().add("user_id", userId)).in(HttpMethod.GET).thenJsonObject();
        return new UserProfile(json);
    }

    public UserProfile getPublicProfile(String userId) throws RequestException, JSONException {
        JSONObject json = requestBy("getPublicProfile").with(new Args().add("user_id", userId)).in(HttpMethod.GET).thenJsonObject();
        return new UserProfile(json);
    }
}
