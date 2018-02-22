/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymessenger.backend.api;

import javax.servlet.ServletRequest;
import mymessenger.backend.model.users.UserSession;

/**
 *
 * @author guilherme
 */
public final class SecurityContext {

    private static final String USERSESSION = "user-session";

    private SecurityContext() {
    }

    public static void set(ServletRequest req, UserSession session) {
        req.setAttribute(USERSESSION, session);
    }

    public static UserSession get(ServletRequest req) {
        Object o = req.getAttribute(USERSESSION);
        return (o == null ? null : (UserSession) o);
    }
}
