/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.jplurk_oauth.data;

import java.awt.Color;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author skyforce
 */
public class UserInfo extends Data {

    private long id;

    public UserInfo(JSONObject json) throws JSONException {
        super(json);
        id = json.getLong("id");
//        this.json = json;
    }

//    @Override
//    public String get(String key) throws JSONException {
//        return json.getString(key);
//    }
    public long getID() {
        return id;
    }

    public String getNickName() throws JSONException {
        return get("nick_name");
    }

    public String getDisplayName() throws JSONException {
        return get("display_name");
    }

    public String getFullName() throws JSONException {
        return get("full_name");
    }

    public boolean hasProfileImage() throws JSONException {
        return json.getInt("has_profile_image") == 1;
    }

    public String getAvatar() throws JSONException {
        return get("avatar");
    }

    public Color getNameColor() {
        String colorstr = get("name_color");
        return Color.decode(getHTMLNameColor());
    }

    public String getHTMLNameColor() {
        String colorstr = get("name_color");
        if (colorstr.equals("null")) {
            return "000000";
        } else {
            return colorstr;
        }
    }

    public static enum ImageSize {

        Small, Medium, Big;

        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
//        String text;
//        ImageSize(String text) {
//            this.text = text;
//        }
    }

    public String getProfileImage(ImageSize size) throws JSONException {
        if (this.hasProfileImage()) {
            String avatar = getAvatar();
            String extfilename = (ImageSize.Big == size) ? ".jpg" : ".gif";
            if ("null".equals(avatar)) {
                return "http://avatars.plurk.com/" + id + "-" + size + extfilename;
            } else {
                return "http://avatars.plurk.com/" + id + "-" + size + avatar + extfilename;
            }
        } else {
            return "http://www.plurk.com/static/default_" + size + ".gif";
        }
    }
}
