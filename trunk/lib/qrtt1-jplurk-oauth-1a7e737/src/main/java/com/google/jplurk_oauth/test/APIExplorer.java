/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.jplurk_oauth.test;

import com.google.jplurk_oauth.data.*;
import com.google.jplurk_oauth.module.Realtime;
import com.google.jplurk_oauth.module.Responses;
import com.google.jplurk_oauth.module.Timeline;
import com.google.jplurk_oauth.skeleton.DateTime;
import com.google.jplurk_oauth.skeleton.PlurkOAuth;
import com.google.jplurk_oauth.skeleton.RequestException;
import java.util.Arrays;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author skyforce
 */
public class APIExplorer {

    public static void main(String[] args) throws RequestException, JSONException {
        String apiKey = "KKtWtYprpPgX";
        String appSecret = "1L5XhtyNhG3SlpRwFl203cCmrlAk3whx";
        String tokenKey = "RgrLDoskkBaF";
        String tokenSecret = "6c7GbmL0S9l8FqlbmY0JmsKa258bIyWg";

        //Properties prop = System.getProperties();
        /*
         * create oauth config
         */
        PlurkOAuth auth = new PlurkOAuth(apiKey, appSecret,
                tokenKey, tokenSecret);
        UserChannel ch = auth.using(Realtime.class).getUserChannel();
        System.out.println(ch.toString());
        Plurks plurks = auth.using(Timeline.class).getPlurks(DateTime.now(), 1, Timeline.Filter.only_user);
        System.out.println(plurks.size());
        for (Plurk p : plurks.getPlurkList()) {
            System.out.println(p.gePlurkId() + " " + p.getUserId() + " " + p.getOwnerId() + " " + p.getQualifierTranslated() + ":" + p.getContent());
            System.out.println(Arrays.toString(p.getLimitedTo()));
            Comments comments = auth.using(Responses.class).get(new Long(p.gePlurkId()));
            for (Comment comment : comments.getCommentList()) {
                System.out.println(comment.getUserId() + " " + comment.getQualifierTranslated() + ":" + comment.getContent());
            }
        }

//        JSONArray plurk = plurks.getJSONArray("plurks");
//        JSONObject user = plurks.getJSONObject("plurk_users");
//        User u = new User(user);
//        System.out.println(user.toString());
//        System.out.println(u.getID());
//        System.out.println(u.getNickName());
//        System.out.println(plurk.toString());

    }
}
