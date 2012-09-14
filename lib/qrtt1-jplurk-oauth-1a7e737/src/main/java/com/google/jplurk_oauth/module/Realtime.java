/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.jplurk_oauth.module;

import com.google.jplurk_oauth.data.UserChannel;
import com.google.jplurk_oauth.skeleton.AbstractModule;
import com.google.jplurk_oauth.skeleton.HttpMethod;
import com.google.jplurk_oauth.skeleton.RequestException;
import org.json.JSONObject;

/**
 *
 * @author SkyforceShen
 */
public class Realtime extends AbstractModule {

    @Override
    protected String getModulePath() {
        return "/APP/Realtime";
    }

    public UserChannel getUserChannel() throws RequestException {
        JSONObject json = requestBy("getUserChannel").withoutArgs().in(HttpMethod.GET).thenJsonObject();
        return new UserChannel(json);
    }
}
