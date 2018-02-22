/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymessenger.backend.ws;

import mymessenger.backend.model.messaging.Message;
import mymessenger.backend.model.users.UserSession;

/**
 *
 * @author guilherme
 */
public class StreamMessageResponse {

    public static StreamMessageResponse ok() {
        StreamMessageResponse r = new StreamMessageResponse();
        r.setOk(true);
        return r;
    }

    public static StreamMessageResponse userSession(UserSession us) {
        StreamMessageResponse r = new StreamMessageResponse();
        r.setOk(true);
        r.setUserSession(us);
        return r;
    }

    static StreamMessageResponse message(Message msg) {
        StreamMessageResponse r = new StreamMessageResponse();
        r.setOk(true);
        r.setMessage(msg);
        return r;
    }
    
    private boolean ok;
    private UserSession userSession;
    private Message message;

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

}
