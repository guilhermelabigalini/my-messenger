/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymessenger.backend.api;

import java.net.URI;
import java.util.Optional;
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
@RequestMapping("api/user")
public class UserController {

    @Autowired
    private UserService userService;
   
    @RequestMapping(path = "{username}", method = RequestMethod.GET)
    public ResponseEntity<?> get(@PathVariable("username") String username) {

        Optional<UserProfile> result = userService.getPublicProfile(username);
        
        if (result.isPresent())
            return ResponseEntity.ok(result.get());
    
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(path = "register", method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestBody UserProfile userProfile) throws ValidationException {
            userService.register(userProfile);

        return ResponseEntity
                .created(URI.create("api/user/" + userProfile.getUsername()))
                .build();
    }

    @RequestMapping(path = "login", method = RequestMethod.POST)
    public ResponseEntity<UserSession> login(@RequestBody LoginRequest login) throws ValidationException {

        return ResponseEntity.ok(userService.login(login.getUsername(), login.getPassword()));
    }

}
