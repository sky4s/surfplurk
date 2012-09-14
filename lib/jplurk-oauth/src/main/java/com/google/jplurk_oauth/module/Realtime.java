/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.jplurk_oauth.module;

import com.google.jplurk_oauth.data.Comet;
import com.google.jplurk_oauth.data.UserChannel;
import com.google.jplurk_oauth.skeleton.*;
import java.util.StringTokenizer;
import org.json.JSONException;
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

    public Comet getComet(UserChannel userChannel) throws JSONException, RequestException {
        return getComet(userChannel, -1);
    }

    public Comet getComet(UserChannel userChannel, int offset) throws JSONException, RequestException {
        RequestBuilder req = new RequestBuilder(getPlurkOAuth(), userChannel.getCometServer()).in(HttpMethod.GET).with(new Args().add("offset", Integer.toString(offset)));
        String result = req.result();
//        System.out.println("result: " + result);
        final String callback = "CometChannel.scriptCallback(";
        int callbackStart = result.indexOf(callback);
        if (-1 != callbackStart) {
            result = result.substring(callback.length());
            result = result.substring(0, result.length() - 2);
//            System.out.println("result: " + result);
            return new Comet(new JSONObject(result));
        }

        return null;
    }

    public UserChannel getUserChannel() throws RequestException {
        JSONObject json = requestBy("getUserChannel").withoutArgs().in(HttpMethod.GET).thenJsonObject();
        return new UserChannel(json);
    }

    public UserChannel getUserChannel(int offset) throws RequestException {
        JSONObject json = requestBy("getUserChannel").with(new Args().add("offset", offset)).in(HttpMethod.GET).thenJsonObject();
        return new UserChannel(json);
    }
}
