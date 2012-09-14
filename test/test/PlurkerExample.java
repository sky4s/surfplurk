/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.google.jplurk_oauth.data.Plurk;
import com.google.jplurk_oauth.data.Plurks;
import com.google.jplurk_oauth.module.Timeline;
import com.google.jplurk_oauth.skeleton.DateTime;
import com.google.jplurk_oauth.skeleton.PlurkOAuth;
import com.google.jplurk_oauth.skeleton.RequestException;
import org.json.JSONException;

/**
 * msn(friend) -> msn-bot(myself)Xplurker->plurk(myself)->plurker
 * plurker->plurk(myself)->msn-botXplurk->msn(friend)
 *
 * @author skyforce
 */
public class PlurkerExample {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws RequestException, JSONException {

        //String settingApiKey = settings.getApiKey();
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

        //auth.using(Timeline.class).getPublicPlurks(Long.MIN_VALUE)

        //auth.using(Timeline.class).plurkAdd("這是測試喔!", Qualifier.SAYS);
//        JSONObject profile = auth.using(Profile.class).getOwnProfile();
//        Iterator keyIt = profile.keys();
//        while (keyIt.hasNext()) {
//            String key = (String) keyIt.next();
//            System.out.println(key + ": " + profile.getString(key));
//        }
//        System.out.println(profile.toString());

        Plurks plurks = auth.using(Timeline.class).getPlurks(DateTime.now(), 5);
        for (Plurk plurk : plurks.getPlurkList()) {
            System.out.println(plurk.toString());
        }
//        for (Iterator it = plurks.keys(); it.hasNext();) {
//            Object o = it.next();
////            System.out.println(it.next());
////            System.out.println(o.getClass().toString());
////            System.out.println(o.toString());
//            String key = (String) o;
//
//            JSONArray array = plurks.getJSONArray(key);
//            System.out.println(array);
//            break;
//        }




    }
}
