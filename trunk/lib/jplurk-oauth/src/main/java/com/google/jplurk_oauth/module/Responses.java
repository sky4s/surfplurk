package com.google.jplurk_oauth.module;

import org.json.JSONObject;

import com.google.jplurk_oauth.Qualifier;
import com.google.jplurk_oauth.data.Comment;
import com.google.jplurk_oauth.data.Comments;
import com.google.jplurk_oauth.skeleton.AbstractModule;
import com.google.jplurk_oauth.skeleton.Args;
import com.google.jplurk_oauth.skeleton.HttpMethod;
import com.google.jplurk_oauth.skeleton.RequestException;
import org.json.JSONException;

public class Responses extends AbstractModule {

    public Comments get(Long plurkId) throws RequestException, JSONException {
//        JSONObject json = requestBy("get").with(new Args().add("plurk_id", plurkId)).in(HttpMethod.GET).thenJsonObject();
//        return new Comments(json);
        return get(plurkId, new Long(0));
    }

    public Comments get(Long plurkId, Long fromResponse) throws RequestException, JSONException {
        JSONObject json = requestBy("get").with(new Args().add("plurk_id", plurkId).add("from_response", fromResponse)).in(HttpMethod.GET).thenJsonObject();
        return new Comments(json);
    }

    public Comment responseAdd(Long plurkId, String content, Qualifier qualifier)
            throws RequestException {
        JSONObject json = requestBy("responseAdd").with(new Args().add("plurk_id", plurkId).add("content", content).add("qualifier", qualifier.toString())).in(HttpMethod.GET).thenJsonObject();
        return new Comment(json);
    }

    public boolean responseDelete(Long responseId, Long plurkId) throws RequestException, JSONException {
        JSONObject json = requestBy("responseDelete").with(new Args().add("response_id", responseId).add("plurk_id", plurkId)).in(HttpMethod.GET).thenJsonObject();
        return json.getString("success_text").equals("ok");
    }

    @Override
    protected String getModulePath() {
        return "/APP/Responses";
    }
}
