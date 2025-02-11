package org.scribe.builder.api;

import org.scribe.model.OAuthConfig;
import org.scribe.model.Token;
import org.scribe.oauth.OAuth10aServiceImpl;

public class PlurkApi extends DefaultApi10a {

    private static final String REQUEST_TOKEN_URL = "http://www.plurk.com/OAuth/request_token";
    private static final String AUTHORIZATION_URL = "http://www.plurk.com/OAuth/authorize?oauth_token=%s";
    private static final String ACCESS_TOKEN_URL = "http://www.plurk.com/OAuth/access_token";

    @Override
    public String getRequestTokenEndpoint() {
        return REQUEST_TOKEN_URL;
    }

    @Override
    public String getAuthorizationUrl(Token requestToken) {
        return String.format(AUTHORIZATION_URL, requestToken.getToken());
    }

    @Override
    public String getAccessTokenEndpoint() {
        return ACCESS_TOKEN_URL;
    }

    public class Mobile extends PlurkApi {

        private static final String AUTHORIZATION_URL = "http://www.plurk.com/m/authorize?oauth_token=%s";

        @Override
        public String getAuthorizationUrl(Token requestToken) {
            return String.format(AUTHORIZATION_URL, requestToken.getToken());
        }
    }

    public static void main(String[] args) {
        PlurkApi api = new PlurkApi();
        OAuthConfig config = new OAuthConfig("KKtWtYprpPgX", "1L5XhtyNhG3SlpRwFl203cCmrlAk3whx");
        OAuth10aServiceImpl service = new OAuth10aServiceImpl(api, config);
        Token token = service.getRequestToken();
        System.out.println(token);
        String url = service.getAuthorizationUrl(token);
        System.out.println(url);
    }
}
