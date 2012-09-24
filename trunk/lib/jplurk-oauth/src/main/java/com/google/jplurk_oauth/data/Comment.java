/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.jplurk_oauth.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author SkyforceShen
 */
public class Comment extends Data implements ContextIF {

    private JSONObject parent;
    private int floor;

    public Comment(JSONObject json) {
        super(json);
    }

    public JSONObject getParent() {
        return parent;
    }

    public Comment(JSONObject json, JSONObject parent) {
        super(json);
        this.parent = parent;
    }

    public String getContent() {
        return get("content");
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

    public Date getPostedDate() throws ParseException {
        return getPostedDate(getPosted());
    }

    public long getUserId() throws JSONException {
        return json.getLong("user_id");
    }

    public long getOwnerId() throws JSONException {
        return getUserId();
    }

    public long getPlurkId() throws JSONException {
        return json.getLong("plurk_id");
    }

    public long getId() throws JSONException {
        return json.getLong("id");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Comment) {
            Comment that = (Comment) obj;
            try {
                return this.getId() == that.getId();
            } catch (JSONException ex) {
                Logger.getLogger(Plurk.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        } else {
            return super.equals(obj);
        }
    }

    @Override
    public String toString() {
        return json.toString();
    }
}
