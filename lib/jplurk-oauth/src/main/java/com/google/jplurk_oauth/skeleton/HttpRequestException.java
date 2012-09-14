/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.jplurk_oauth.skeleton;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.Response;

/**
 *
 * @author SkyforceShen
 */
public class HttpRequestException extends RequestException {

    private Response response;

    public String getErrorText() {
        try {
            JSONObject body = new JSONObject(response.getBody());
            String errorText = body.getString("error_text");
            return errorText;
        } catch (JSONException ex) {
            Logger.getLogger(HttpRequestException.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public Response getResponse() {
        return response;
    }

    public HttpRequestException(Exception e, Response response) {
        super(e);
        this.response = response;
    }

    public HttpRequestException(String message, Response response) {
        super(message);
        this.response = response;
    }
}
