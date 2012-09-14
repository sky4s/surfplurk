package com.google.jplurk_oauth.module;

import java.io.File;

import org.json.JSONObject;

import com.google.jplurk_oauth.Ids;
import com.google.jplurk_oauth.Qualifier;
import com.google.jplurk_oauth.data.Plurks;
import com.google.jplurk_oauth.skeleton.AbstractModule;
import com.google.jplurk_oauth.skeleton.Args;
import com.google.jplurk_oauth.skeleton.HttpMethod;
import com.google.jplurk_oauth.skeleton.RequestException;
import com.google.jplurk_oauth.skeleton.DateTime;
import org.json.JSONException;

public class Timeline extends AbstractModule {

    public JSONObject getPlurk(Long plurkId) throws RequestException {
        return getPlurk(plurkId, null);
    }

    public JSONObject getPlurk(Long plurkId, Args optional) throws RequestException {
        return requestBy("getPlurk").with(new Args().add("plurk_id", plurkId).add(optional)).in(HttpMethod.GET).thenJsonObject();
    }

    public static enum Filter {

        only_user, only_responded, only_private, only_favorite
    }

    public Plurks getPlurks(DateTime offset, int limit, Filter filter) throws RequestException, JSONException {
        return getPlurks(offset, limit, filter, false, false, false);
    }

    public Plurks getPlurks(DateTime offset, int limit, Filter filter, boolean favorers_detail, boolean limited_detail, boolean replurkers_detail) throws RequestException, JSONException {
        Args args = new Args();
        args.add("offset", offset.toTimeOffset());
        args.add("limit", limit);
        args.add("filter", filter.name());
        args.add("favorers_detail", favorers_detail);
        args.add("limited_detail", limited_detail);
        args.add("replurkers_detail", replurkers_detail);
        return getPlurks(args);
    }

    public Plurks getPlurks(Args optional) throws RequestException, JSONException {
        JSONObject json = requestBy("getPlurks").with(new Args().add(optional).add(MinimalData ? new Args().add("minimal_data", "1") : null)).in(HttpMethod.GET).thenJsonObject();
        return new Plurks(json);
    }

    public JSONObject getUnreadPlurks() throws RequestException {
        return getUnreadPlurks(null);
    }

    public JSONObject getUnreadPlurks(Args optional) throws RequestException {
        return requestBy("getUnreadPlurks").with(new Args().add(optional)).in(HttpMethod.GET).thenJsonObject();
    }

    public JSONObject getPublicPlurks(Long userId) throws RequestException {
        return getPublicPlurks(userId, null);
    }

    public JSONObject getPublicPlurks(Long userId, Args optional) throws RequestException {
        return requestBy("getPublicPlurks").with(new Args().add("user_id", userId).add(optional)).in(HttpMethod.GET).thenJsonObject();
    }

    public JSONObject plurkAdd(String content, Qualifier qualifier) throws RequestException {
        return plurkAdd(content, qualifier, null);
    }

    public JSONObject plurkAdd(String content, Qualifier qualifier, Args optional) throws RequestException {
        return requestBy("plurkAdd").with(new Args().add("content", content).add("qualifier", qualifier.toString()).add(optional)).in(HttpMethod.GET).thenJsonObject();
    }

    public JSONObject plurkDelete(Long plurkId) throws RequestException {
        return requestBy("plurkDelete").with(new Args().add("plurk_id", plurkId)).in(HttpMethod.GET).thenJsonObject();
    }

    public JSONObject plurkEdit(Long plurkId, String content) throws RequestException {
        return requestBy("plurkEdit").with(new Args().add("plurk_id", plurkId).add("content", content)).in(HttpMethod.GET).thenJsonObject();
    }

    public JSONObject mutePlurks(Ids ids) throws RequestException {
        return requestBy("mutePlurks").with(new Args().add("ids", ids.formatted())).in(HttpMethod.GET).thenJsonObject();
    }

    public JSONObject unmutePlurks(Ids ids) throws RequestException {
        return requestBy("unmutePlurks").with(new Args().add("ids", ids.formatted())).in(HttpMethod.GET).thenJsonObject();
    }

    public JSONObject favoritePlurks(Ids ids) throws RequestException {
        return requestBy("favoritePlurks").with(new Args().add("ids", ids.formatted())).in(HttpMethod.GET).thenJsonObject();
    }

    public JSONObject unfavoritePlurks(Ids ids) throws RequestException {
        return requestBy("unfavoritePlurks").with(new Args().add("ids", ids.formatted())).in(HttpMethod.GET).thenJsonObject();
    }

    public JSONObject replurk(Ids ids) throws RequestException {
        return requestBy("replurk").with(new Args().add("ids", ids.formatted())).in(HttpMethod.GET).thenJsonObject();
    }

    public JSONObject unreplurk(Ids ids) throws RequestException {
        return requestBy("unreplurk").with(new Args().add("ids", ids.formatted())).in(HttpMethod.GET).thenJsonObject();
    }

    public JSONObject markAsRead(Ids ids) throws RequestException {
        return requestBy("markAsRead").with(new Args().add("ids", ids.formatted())).in(HttpMethod.GET).thenJsonObject();
    }

    public JSONObject uploadPicture(File file) throws RequestException {
        /*
         * TODO ...
         */
        throw new UnsupportedOperationException();
    }

    @Override
    protected String getModulePath() {
        return "/APP/Timeline";
    }
}
