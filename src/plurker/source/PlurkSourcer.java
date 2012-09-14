/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package plurker.source;

//import com.google.jplurk_oauth.Qualifier;
import com.google.jplurk_oauth.data.*;
import com.google.jplurk_oauth.module.*;
import com.google.jplurk_oauth.skeleton.AbstractModule;
import com.google.jplurk_oauth.skeleton.DateTime;
import com.google.jplurk_oauth.skeleton.PlurkOAuth;
import com.google.jplurk_oauth.skeleton.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Date;
import java.util.InvalidPropertiesFormatException;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import plurker.ui.PlurkerApplication;
import plurker.ui.Qualifier;

/**
 *
 * @author skyforce
 */
public class PlurkSourcer implements Serializable {

    PlurkPool plurkPool;

    public final static void setMinimalData(boolean minimalData) {
        AbstractModule.setMinimalData(minimalData);
    }
    private static boolean doValidToken = true;

    public final static void setDoValidToken(boolean valid) {
        doValidToken = valid;
    }
    private UserProfile ownProfile;

    public UserProfile getOwnProfile() throws JSONException {
        try {
            if (null == ownProfile) {
                ownProfile = auth.using(Profile.class).getOwnProfile();
            }
            UserInfo userInfo = ownProfile.getUserInfo();
            userID = userInfo.getID();
        } catch (RequestException ex) {
            Logger.getLogger(PlurkSourcer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(PlurkSourcer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ownProfile;
    }
    private long userID = -1;

    public long getUserID() {
        if (-1 == userID) {
            try {
                getOwnProfile();
            } catch (JSONException ex) {
                Logger.getLogger(PlurkSourcer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return userID;
    }

    private void validToken() throws RequestException, JSONException {
        if (doValidToken) {
            getOwnProfile();
        }
    }

//    public PlurkSourcer() throws RequestException, JSONException {
//        if (configFile.exists()) {
//            Properties properties = loadProperties(configFile);
//            tokenKey = properties.getProperty(TOKEN_KEY);
//            tokenSecret = properties.getProperty(TOKEN_SECRET);
//            auth = new PlurkOAuth(apiKey, appSecret,
//                    tokenKey, tokenSecret);
//        } else {
//            auth = new PlurkOAuth(apiKey, appSecret,
//                    tokenKey, tokenSecret);
//        }
//        validToken();
//    }
    public PlurkSourcer(String apiKey, String appSecret, String tokenKey, String tokenSecret) throws RequestException, JSONException {
        this.apiKey = apiKey;
        this.appSecret = appSecret;
        this.tokenKey = tokenKey;
        this.tokenSecret = tokenSecret;
        auth = new PlurkOAuth(apiKey, appSecret,
                tokenKey, tokenSecret);
        validToken();
    }
    public final static String API_KEY = "KKtWtYprpPgX";
    public final static String APP_SECRET = "1L5XhtyNhG3SlpRwFl203cCmrlAk3whx";
    String apiKey = "KKtWtYprpPgX";
    String appSecret = "1L5XhtyNhG3SlpRwFl203cCmrlAk3whx";
    String tokenKey = "4HSvemSc0VFL";
    String tokenSecret = "9xQqhiCnIyElhUzysDL39OZFazSnp4WQ";
    PlurkOAuth auth;

    public PlurkOAuth getPlurkOAuth() {
        return auth;
    }
    private HttpRequestException httpRequestException;

    public HttpRequestException getHttpRequestException() {
        return httpRequestException;
    }

    public Comment responseAdd(Long plurkId, String content, Qualifier qualifier) {
        try {
            Comment comment = auth.using(Responses.class).responseAdd(plurkId, content, qualifier.toJplurkOauthQualifier());
            return comment;

        } catch (RequestException ex) {
            Logger.getLogger(PlurkSourcer.class.getName()).log(Level.SEVERE, null, ex);
            Throwable cause = ex.getCause();
            if (cause instanceof HttpRequestException) {
                httpRequestException = (HttpRequestException) cause;
            }
            return null;
        }

    }

    public boolean responseDelete(Long responseId, Long plurkId) {
        try {
            return auth.using(Responses.class).responseDelete(responseId, plurkId);
        } catch (RequestException | JSONException ex) {
            Logger.getLogger(PlurkSourcer.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public Comments getComments(Plurk plurk) {
        try {
            Comments comments = auth.using(Responses.class).get(new Long(plurk.getPlurkId()));
            return comments;
        } catch (JSONException | RequestException ex) {
            Logger.getLogger(PlurkSourcer.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public Comments getComments(Plurk plurk, long fromResponse) {
        try {
            return getComments(plurk.getPlurkId(), fromResponse);
        } catch (JSONException ex) {
            Logger.getLogger(PlurkSourcer.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public Comments getComments(long plurkID, long fromResponse) {
        try {
            Comments comments = auth.using(Responses.class).get(plurkID, fromResponse);
            return comments;
        } catch (JSONException | RequestException ex) {
            Logger.getLogger(PlurkSourcer.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public Plurks getUnreadPlurks(DateTime offset, int limit, Timeline.Filter filter) {
        try {
            Plurks plurks = auth.using(Timeline.class).getPlurks(offset, limit, filter);
            if (null != plurkPool) {
                plurkPool.storePlurks(plurks);
            }
            return plurks;
        } catch (RequestException | JSONException ex) {
            Logger.getLogger(PlurkSourcer.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /*
     * 要用set還是map來儲存plurks? 需要key/value的場合才需要map, 否則set足矣
     */
    public Plurks getOlderPlurks(DateTime offset, int limit, Timeline.Filter filter) {
        try {
            Plurks plurks = auth.using(Timeline.class).getPlurks(offset, limit, filter);
            if (null != plurkPool) {
                plurkPool.storePlurks(plurks);
            }
            return plurks;
        } catch (RequestException | JSONException ex) {
            Logger.getLogger(PlurkSourcer.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * newer沒辦法像older可以加filter, 所以filter自己來? 甚麼時候需要filter? 就是切換過去分頁的時候.
     *
     * @param offset
     * @param limit
     * @return
     */
    public Plurks getNewerPlurks(DateTime offset, int limit) {
        try {
            Plurks plurks = auth.using(Polling.class).getPlurks(offset, limit);
            if (null != plurkPool) {
                plurkPool.storePlurks(plurks);
            }
            return plurks;
        } catch (RequestException | JSONException ex) {
            Logger.getLogger(PlurkSourcer.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public UnreadCount getUnreadCount() {
        try {
            return auth.using(Polling.class).getUnreadCount();
        } catch (RequestException ex) {
            Logger.getLogger(PlurkSourcer.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public UserProfile getProfile(long userID) {
        try {
            UserProfile profile = auth.using(Profile.class).getPublicProfile(userID);
            return profile;
        } catch (RequestException | JSONException ex) {
            Logger.getLogger(PlurkSourcer.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public Realtime getRealtime() {
        Realtime realtime = auth.using(Realtime.class);
        return realtime;
    }

    public static void main(String[] args) throws RequestException, JSONException {
        PlurkSourcer source = PlurkerApplication.getPlurkSourcerInstance();
        UnreadCount unreadCount = source.getUnreadCount();
        System.out.println(unreadCount.getAll() + " " + unreadCount.getMy() + " " + unreadCount.getPrivate() + " " + unreadCount.getResponded());
        System.out.println(unreadCount.getFavorite());
        //        while (true) {
        //            System.out.println(source.getNewerPlurks(DateTime.now(), 20));
        //        }
    }
}
