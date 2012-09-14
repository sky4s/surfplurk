package com.google.jplurk_oauth.skeleton;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.OAuthConfig;
import org.scribe.model.Token;
import org.scribe.oauth.OAuth10aServiceImpl;

public class PlurkOAuthConfig extends DefaultApi10a {

    @Override
    public String getRequestTokenEndpoint() {
        return "http://www.plurk.com/OAuth/request_token";
    }

    @Override
    public String getAccessTokenEndpoint() {
        return "http://www.plurk.com/OAuth/access_token";
    }

    @Override
    public String getAuthorizationUrl(Token requestToken) {
        return String.format("http://www.plurk.com/OAuth/authorize?oauth_token=%s", requestToken.getToken());
    }

    public static void main(String[] args) {
        PlurkOAuthConfig api = new PlurkOAuthConfig();
        OAuthConfig config = new OAuthConfig("KKtWtYprpPgX", "1L5XhtyNhG3SlpRwFl203cCmrlAk3whx");
        OAuth10aServiceImpl service = new OAuth10aServiceImpl(api, config);
        Token token = service.getRequestToken();
        System.out.println(token);
        String url = service.getAuthorizationUrl(token);
        System.out.println(url);
    }
}
