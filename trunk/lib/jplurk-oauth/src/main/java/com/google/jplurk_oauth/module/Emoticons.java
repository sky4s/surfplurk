/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.jplurk_oauth.module;

import com.google.jplurk_oauth.skeleton.AbstractModule;
import com.google.jplurk_oauth.skeleton.HttpMethod;
import com.google.jplurk_oauth.skeleton.RequestException;
import org.json.JSONObject;

/**
 *
 * @author SkyforceShen
 */
public class Emoticons extends AbstractModule {

    public void addFromURL(String url, String keyword) {
    }

    public void delete(String url) {
    }

    public JSONObject get() throws RequestException {
        JSONObject json = requestBy("get").withoutArgs().in(HttpMethod.GET).thenJsonObject();
        return json;
    }

    public void getHot() {
    }

    public void getPopular() {
    }

    @Override
    protected String getModulePath() {
        return "/APP/Emoticons";
    }
}
