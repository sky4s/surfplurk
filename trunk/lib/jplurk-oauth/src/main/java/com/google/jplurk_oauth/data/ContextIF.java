/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.jplurk_oauth.data;

import org.json.JSONException;

/**
 *
 * @author skyforceshen
 */
public interface ContextIF {

//    public int getID() throws JSONException;
    public String getContent() throws JSONException;

    public String getQualifierTranslated() throws JSONException;

    public String getQualifier() throws JSONException;

    public long getUserId() throws JSONException;

    public long getOwnerId() throws JSONException;
    
}
