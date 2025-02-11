/*
 * Copyright 2004-2005 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.jml.protocol.incoming;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import net.sf.jml.Email;
import net.sf.jml.MsnMessenger;
import net.sf.jml.MsnOwner;
import net.sf.jml.MsnProtocol;
import net.sf.jml.MsnSwitchboard;
import net.sf.jml.exception.IncorrectPasswordException;
import net.sf.jml.exception.LoginException;
import net.sf.jml.impl.AbstractMessenger;
import net.sf.jml.impl.MsnOwnerImpl;
import net.sf.jml.protocol.MsnIncomingMessage;
import net.sf.jml.protocol.MsnSession;
import net.sf.jml.protocol.outgoing.OutgoingSYN;
import net.sf.jml.protocol.outgoing.OutgoingUSRAuthNS;
import net.sf.jml.protocol.soap.SSO;
import net.sf.jml.util.JmlConstants;
import net.sf.jml.util.StringUtils;

/**
 * The user have successed login in NS/SB server, or MSN NS server
 * return a authentication string, user should use this string to
 * get the validate string.
 * <p>
 * Supported Protocol: All
 * <p>
 * Auth Syntax 1: USR trId TWN S authStr
 * Auth Syntax 2: USR trId SSO S policy nonce
 * <p>
 * Login Success Syntax 1: USR trId OK email displayName
 * <p>
 * Login Success MSNP8/MSNP9 Syntax 2: USR trId OK email displayName verified 0
 * <p>
 * Login Success MSNP10 Syntax 2: USR trId OK email verified 0
 * <p>
 * Login Success Syntax 1 is used when login into SB server,
 * Login Success Syntax 2 is used when login into NS server.
 *
 * @author Roger Chen
 */
public class IncomingUSR extends MsnIncomingMessage {

//    private static final Log log = LogFactory.getLog(MsnIncomingMessage.class);

    private static final Pattern passportUrlPattern = Pattern
            .compile(".*DALogin=([^,]*),.*");

    private static final Pattern ticketPattern = Pattern
            .compile(".*from-PP='([^']*)'.*");

    public IncomingUSR(MsnProtocol protocol) {
        super(protocol);
    }

    private boolean isLoginSuccess() {
        return "OK".equals(getParam(0));
    }

    public String getAuthStr() {
        return getParam(2);
    }

    public String getPolicy()
    {
        return getParam(2);
    }

    public String getNonce()
    {
        return getParam(3);
    }

    private boolean isLoginIntoSB() {
        return !getParam(getParamCount() - 1).equals("0");
    }

    public Email getEmail() {
        return Email.parseStr(getParam(1));
    }

    public String getDisplayName() {
        if (isLoginIntoSB() || (protocol.before(MsnProtocol.MSNP10))) {
            return StringUtils.urlDecode(getParam(2));
        }
        return getEmail().getEmailAddress();
    }

    public boolean isVerified() {
        if (isLoginIntoSB())
            return true;
        if (protocol.before(MsnProtocol.MSNP10)) {
            return "1".equals(getParam(3));
        }
        return "1".equals(getParam(2));
    }

    @Override
    protected void messageReceived(final MsnSession session) {
        super.messageReceived(session);

        final MsnSwitchboard switchboard = session.getSwitchboard();
        final MsnMessenger messenger = session.getMessenger();
        if (isLoginSuccess()) {
//            log.debug("MSNDEBUG: We have a successful login USR packet!");
            if (isLoginIntoSB()) { //login success to SB
                if (switchboard != null) {
                    ((AbstractMessenger) messenger)
                            .fireSwitchboardStarted(switchboard);
                }
            } else { //login success to NS
                MsnOwnerImpl owner = (MsnOwnerImpl) messenger.getOwner();
                owner.setVerified(isVerified());
                owner.fSetDisplayName(getDisplayName());
                ((AbstractMessenger) messenger).fireLoginCompleted();

                // obtaining contact list with syn packet has been dropped after
                // MSN13
                if(protocol.before(MsnProtocol.MSNP13))
                {
                    OutgoingSYN message = new OutgoingSYN(protocol);
                    message.setCachedVersion(messenger.getContactList()
                            .getVersion());
                    messenger.send(message);
                }
                else
                {
                    // call for contact list
                    session.getContactList().dispatch();
                }
            }
        } else { //auth
//            log.debug("MSNDEBUG: We are doing auth, opening a thread to handle it");
            //SSL is slow, open new thread to do this
            new Thread() {

                private String getPassportUrlSlow() throws IOException {
//                    log.debug("MSNDEBUG: Lets ask the nexus what passport url to use");
                    HttpsURLConnection conn = null;
                    try {
                        conn = (HttpsURLConnection) new URL(
                                "https://nexus.passport.com/rdr/pprdr.asp")
                                .openConnection();
                        conn.setUseCaches(false);
//                        log.debug("MSNDEBUG: Got password urls: "+conn.getHeaderField("PassportURLs"));
                        Matcher matcher = passportUrlPattern.matcher(conn
                                .getHeaderField("PassportURLs"));
                        if (matcher.matches()) {
//                            log.debug("MSNDEBUG: We're going to use url: "+matcher.group(1));
                            return "https://" + matcher.group(1);
                        }
//                        log.debug("MSNDEBUG: Crap, no url found?!");
                        return null;
                    } finally {
                        if (conn != null)
                            conn.disconnect();
                    }
                }

                private String getPassportUrl() throws IOException {
//                    log.debug("MSNDEBUG: Retrieving passport url");
                    if (JmlConstants.FAST_SSL_LOGIN)
                        return "https://login.live.com/login2.srf";
                    return getPassportUrlSlow();
                }

                private String getLoginTicket(String visitUrl, String passport,
                        String password, String challengeStr)
                        throws IOException {
//                    log.debug("MSNDEBUG: Retrieving the login ticket from url "+visitUrl+" and passport "+passport);
                    HttpsURLConnection conn = null;
                    try {
                        conn = (HttpsURLConnection) new URL(visitUrl)
                                .openConnection();
                        conn.setUseCaches(false);
//                        log.debug("MSNDEBUG: Setting request property");
                        conn.setRequestProperty("Authorization",
//                                "Passport1.4 OrgVerb=GET,OrgURL=http%3A%2F%2Fmessenger%2Emsn%2Ecom,sign-in="
                                "Passport1.4 OrgVerb=GET,OrgURL="
                                        + StringUtils.urlEncode(visitUrl)
                                        + ",sign-in="
                                        + StringUtils.urlEncode(passport)
                                        + ",pwd="
                                        + StringUtils.urlEncode(password) + ","
                                        + challengeStr);
//                        log.debug("MSNDEBUG: Request property is "+conn.getRequestProperty("Authorization"));
                        switch (conn.getResponseCode()) {
                            case 200: //success
//                                log.debug("MSNDEBUG: Response code is 200, success!");
                                Matcher matcher = ticketPattern.matcher(conn
                                        .getHeaderField("Authentication-Info"));
                                if (matcher.matches()) {
                                    return matcher.group(1);
                                }
                                return null;
                            case 302: //redirect
//                                log.debug("MSNDEBUG: Response code is 302, being redirected!");
                                visitUrl = conn.getHeaderField("Location");
//                                log.debug("MSNDEBUG: Redirect to "+visitUrl);
                                return getLoginTicket(visitUrl, passport, password,
                                        challengeStr);
                            case 401: //failed
//                                log.debug("MSNDEBUG: Response code is 401, we failed?");
                                return null;
                            default:
//                                log.debug("MSNDEBUG: Response code (unknown) was "+conn.getResponseCode());
                        }
                    } finally {
                        if (conn != null) {
                            conn.disconnect();
                        }
                    }
                    return null;
                }

                @Override
                public void run()
                {
                    try
                    {
                        if(protocol.before(MsnProtocol.MSNP13))
                        {
                            OutgoingUSRAuthNS outgoing = new OutgoingUSRAuthNS(
                                    protocol);
                            //log.debug("MSNDEBUG: Time to request the login ticket for auth");
                            String ticket = getLoginTicket(getPassportUrl(),
                                    messenger.getOwner().getEmail()
                                            .getEmailAddress(),
                                    ((MsnOwnerImpl) messenger.getOwner())
                                            .getPassword(), getAuthStr());
                            if (ticket == null) {
                                ((AbstractMessenger) messenger)
                                        .fireExceptionCaught(new IncorrectPasswordException());
                                return;
                            }
                            outgoing.setTicket(ticket);
                            messenger.send(outgoing);
                        }
                        else
                        {
                            String nonce = getNonce();
                            String policy = getPolicy();

                            // must logout and throw exception or something
                            if(nonce == null)
                            {
                                ((AbstractMessenger)messenger).
                                    fireExceptionCaught(
                                        new LoginException(
                                            "Login Failed, nonce is missing!"));
                                ((AbstractMessenger)messenger).logout();

                                return;
                            }

                            MsnOwner owner = session.getMessenger().getOwner();

                            if(!(owner instanceof MsnOwnerImpl))
                                return;

                            String username = owner.getEmail().getEmailAddress();
                            String pass = ((MsnOwnerImpl)owner).getPassword();

                            SSO sso = session.createSSO(username, pass, policy, nonce);

                            String ticket = sso.getTicket();

                             // must logout and throw exception or something
                            if(ticket == null)
                            {
                                ((AbstractMessenger)messenger).
                                    fireExceptionCaught(
                                        new LoginException(
                                            "Login Failed, error making ticket!"));
                                ((AbstractMessenger)messenger).logout();
                                return;
                            }

                            MsnMessenger messenger = session.getMessenger();

                            OutgoingUSRAuthNS outgoing = new OutgoingUSRAuthNS(
                                    protocol);

                            if (ticket == null)
                            {
                                ((AbstractMessenger) messenger)
                                        .fireExceptionCaught(new IncorrectPasswordException());
                                return;
                            }
                            outgoing.setTicket(ticket);
                            messenger.send(outgoing);
                        }

                    } catch (IOException e)
                    {
                        ((AbstractMessenger) messenger).fireExceptionCaught(e);
                        messenger.logout();
                    }
                }
            }.start();
        }
    }
}