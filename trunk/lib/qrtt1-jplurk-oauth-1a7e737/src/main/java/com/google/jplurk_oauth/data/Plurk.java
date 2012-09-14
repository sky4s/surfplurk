/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.jplurk_oauth.data;

import java.util.StringTokenizer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author skyforce
 */
public class Plurk extends Data {

    @Override
    public String toString() {
        return json.toString();
    }
    private JSONObject json;

    public Plurk(JSONObject json) {
        this.json = json;
    }

    @Override
    public String get(String key) throws JSONException {
        return json.getString(key);
    }

    public String getQualifierTranslated() throws JSONException {
        return (String) get("qualifier_translated");
    }

    public String getQualifier() throws JSONException {
        return (String) get("qualifier");
    }

    public String getPosted() throws JSONException {
        return (String) get("posted");
    }

    public String getContent() throws JSONException {
        return (String) get("content");
    }

    public String getContentRaw() throws JSONException {
        return (String) get("content_raw");
    }

    public int gePlurkId() throws JSONException {
        return Integer.parseInt((String) get("plurk_id"));
    }

    public Type getPlurkType() throws JSONException {
        int type = Integer.parseInt((String) get("plurk_type"));
        return Type.getType(type);
    }

    public int getUserId() throws JSONException {
        return Integer.parseInt((String) get("user_id"));
    }

    public int getOwnerId() throws JSONException {
        return Integer.parseInt((String) get("owner_id"));
    }

    public int[] getLimitedTo() throws JSONException {
        String s = json.getString("limited_to");
        if (s.equals("null")) {
            return null;
        }
        StringTokenizer tokenizer = new StringTokenizer(s, "|");
        int tokens = tokenizer.countTokens();
        int[] result = new int[tokens];
        for (int x = 0; x < tokens; x++) {
            String t = tokenizer.nextToken();
            result[x] = Integer.parseInt(t);
        }
        return result;
    }

    public static enum Type {

        Public, Private, Public_Logged, Private_Logged;

        static Type getType(int type) {
            switch (type) {
                case 0:
                    return Public;
                case 1:
                    return Private;
                case 2:
                    return Public_Logged;
                case 3:
                    return Private_Logged;
                default:
                    return null;
            }
        }
    }
}
