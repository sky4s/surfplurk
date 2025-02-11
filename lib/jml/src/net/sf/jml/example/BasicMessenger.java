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
package net.sf.jml.example;

import net.sf.jml.MsnMessenger;
import net.sf.jml.impl.MsnMessengerFactory;

/**
 * @author Roger Chen
 */
public class BasicMessenger {

    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    protected void initMessenger(MsnMessenger messenger) {
    }

    public void start() {
        //create MsnMessenger instance
        MsnMessenger messenger = MsnMessengerFactory.createMsnMessenger(email,
                password);

        //MsnMessenger support all protocols by default
        //messenger.setSupportedProtocol(new MsnProtocol[] { MsnProtocol.MSNP8 });

        //default init status is online, 
        //messenger.getOwner().setInitStatus(MsnUserStatus.BUSY);

        //log incoming message
        messenger.setLogIncoming(true);

        //log outgoing message
        messenger.setLogOutgoing(true);

        initMessenger(messenger);
        messenger.login();
    }

    public static void main(String[] args) throws Exception {
//        if (args.length != 3) {
//            System.out.println("Usage: java messengerClassName email password");
//            return;
//        }
        String className = "net.sf.jml.example.PrettyMessenger";
        BasicMessenger messenger = (BasicMessenger) Class.forName(className).newInstance();
        messenger.setEmail("skylayers@yahoo.com.tw");
        messenger.setPassword("sky2326");
        messenger.start();
    }
}
