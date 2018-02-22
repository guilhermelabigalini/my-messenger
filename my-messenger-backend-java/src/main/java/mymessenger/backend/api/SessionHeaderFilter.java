/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymessenger.backend.api;

import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mymessenger.backend.model.users.UserSession;
import mymessenger.backend.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.GenericFilterBean;

/**
 *
 * @author guilherme
 */
@Service
public class SessionHeaderFilter extends GenericFilterBean {
    public static String[] PROTECTED_URLS = {
        "/api/message", "/api/logout"
    };
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionHeaderFilter.class);
    
    @Autowired
    private UserService userService;

    private Optional<UserSession> getUserSession(String authHeader) {
        if (authHeader != null) {

            String[] pieces = authHeader.split(" ");

            if (pieces.length == 2 && pieces[0].equals("Bearer")) {

                Optional<UserSession> us = userService.getSession(pieces[1]);

                return us;
            }
        }

        return Optional.empty();
    }

    @Override
    public void doFilter(final ServletRequest req,
            final ServletResponse res,
            final FilterChain chain) throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing Authorization header.");
            return;
        }

        Optional<UserSession> us = getUserSession(authHeader);

        if (!us.isPresent()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid authorization token");
            return;
            //throw new InvalidTokenException();
        }

        LOGGER.info("Storing " + us.get().getId() + " session in context.");
        SecurityContext.set(req, us.get());
        LOGGER.info("Stored " + us.get().getId() + " session in context.");
        
        chain.doFilter(req, res);
    }
}
