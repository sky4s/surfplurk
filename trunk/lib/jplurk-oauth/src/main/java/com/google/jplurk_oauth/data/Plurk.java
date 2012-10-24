/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.jplurk_oauth.data;

import com.sun.org.apache.bcel.internal.generic.GETSTATIC;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
//import plurker.sourcer.*;

/**
 *
 * @author skyforce
 */
public class Plurk extends Data implements ContextIF {

    @Override
    public String toString() {
        return json.toString();
    }

    public Plurk(JSONObject json) {
        super(json);
    }

    public String getQualifierTranslated() {
        return get("qualifier_translated");
    }

    public String getQualifier() {
        return get("qualifier");
    }

    public String getPosted() {
        return get("posted");
    }
//    private static SimpleDateFormat GMTDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
//    private Date postedDate = null;

    public Date getPostedDate() throws ParseException {
        return getPostedDate(getPosted());
    }

    public String getContent() {
        return get("content");
    }

    public String getContentRaw() {
        return get("content_raw");
    }

    public long getPlurkId() throws JSONException {
        return json.getLong("plurk_id");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Plurk) {
            Plurk that = (Plurk) obj;
            try {
                return this.getPlurkId() == that.getPlurkId();
            } catch (JSONException ex) {
                Logger.getLogger(Plurk.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        } else {
            return super.equals(obj);
        }
    }

    public PlurkType getPlurkType() throws JSONException {
        int type = json.getInt("plurk_type");
        return PlurkType.getType(type);

    }

    public long getUserId() throws JSONException {
        return json.getLong("user_id");
    }

    public String getNickName() throws JSONException {
        return json.getString("nick_name");
    }

//    public String getDisplayName() throws JSONException {
//        return json.getString("display_name");
//    }
    public long getOwnerId() throws JSONException {
        return json.getLong("owner_id");
    }

    public long getResponseCount() throws JSONException {
        return json.getLong("response_count");
    }

    public void setResponseCount(long count) throws JSONException {
        json.put("response_count", count);
    }

    public long getResponseSeen() throws JSONException {
        return json.getLong("responses_seen");
    }

    public long getReplurkerId() {
        String replurkerID = get("replurker_id");
        if (null != replurkerID && !replurkerID.equals("null") && !replurkerID.equals("")) {
            return Long.parseLong(replurkerID);
        } else {
            return -1;
        }
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

    public boolean isFavorite() throws JSONException {
        String favorite = get("favorite");
        return favorite.equals("true");
        //        return json.getLong("response_count");
    }

    public static enum PlurkType {

        Public, Private, Public_Logged, Private_Logged;

        static PlurkType getType(int type) {
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

    public static enum UnreadType {

        Read, Unread, Muted;

        static UnreadType getType(int type) {
            switch (type) {
                case 0:
                    return Read;
                case 1:
                    return Unread;
                case 2:
                    return Muted;

                default:
                    return null;
            }
        }
    }

    public UnreadType getUnreadType() throws JSONException {
        return UnreadType.getType(json.getInt("is_unread"));
    }
}
