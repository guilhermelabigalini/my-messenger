/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymessenger.backend.api;

import java.net.URI;
import java.util.Optional;
import javax.servlet.ServletRequest;
import mymessenger.backend.model.users.UserProfile;
import mymessenger.backend.model.users.UserSession;
import mymessenger.backend.services.UserService;
import mymessenger.backend.services.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author guilherme
 */
@RestController
@RequestMapping("api/logout")
public class LogoutController {

    @Autowired
    private UserService userService;
   
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> logout(ServletRequest req) {
    
        UserSession us = SecurityContext.get(req);
        
        if (us == null) {
            throw new InvalidTokenException("security context not found");
        }
        
        userService.logout(us);
        
        return ResponseEntity.ok().build();
    }

}
