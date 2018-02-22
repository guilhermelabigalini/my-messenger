/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymessenger.backend.services;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import mymessenger.backend.messaging.MessageSender;
import mymessenger.backend.messaging.MessageSenderException;
import mymessenger.backend.model.messaging.Destination;
import mymessenger.backend.model.messaging.DestinationType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import mymessenger.backend.model.users.UserProfile;
import mymessenger.backend.model.users.UserSession;

/**
 *
 * @author guilherme
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final MessageSender messageSender;

    public UserService(
            @Autowired UserRepository userRepository,
            @Autowired UserSessionRepository userSession,
            @Autowired MessageSender messageSender) {
        this.userRepository = userRepository;
        this.userSessionRepository = userSession;
        this.messageSender = messageSender;
    }

    public Optional<UserProfile> getProfile(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<UserProfile> getPublicProfile(String username) {
        Optional<UserProfile> result = userRepository.findByUsername(username);

        result.ifPresent(up -> up.setPassword(null));

        return result;
    }

    public void register(UserProfile user) throws ValidationException {

        if (!StringUtils.hasText(user.getUsername())) {
            throw new ValidationException("username is required");
        }

        if (!StringUtils.hasText(user.getPassword())) {
            throw new ValidationException("password is required");
        }

        if (user.getBirthDate() == null) {
            throw new ValidationException("BirthDate is required");
        }

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new ValidationException("username already exists");
        }

        try {
            user.setPassword(PasswordUtil.encode(user.getPassword()));
        } catch (NoSuchAlgorithmException ex) {
            throw new ValidationException("Unable to encode password", ex);
        }

        userRepository.insert(user);

        try {
            messageSender.registerDestination(new Destination(DestinationType.User, user.getId()));
        } catch (MessageSenderException ex) {
            userRepository.delete(user.getId());
            throw new ValidationException("Unable to register destination", ex);
        }
    }

    public Optional<UserSession> getSession(String sessionid) {
        
        if (!StringUtils.hasText(sessionid)) {
            return Optional.empty();
        }
        
        return userSessionRepository.findById(sessionid);
    }

    public UserSession login(String username, String password) throws ValidationException {
        Optional<UserProfile> profile = userRepository.findByUsername(username);

        if (!profile.isPresent()) {
            throw new ValidationException("username does not exists");
        }

        try {
            if (PasswordUtil.encode(password).equals(profile.get().getPassword())) {
                UserSession us = new UserSession(
                        UUID.randomUUID().toString(),
                        LocalDateTime.now(),
                        profile.get().getId());

                userSessionRepository.insert(us);

                return us;
            }
        } catch (NoSuchAlgorithmException ex) {
            throw new ValidationException("Unable to encode password", ex);
        }

        throw new ValidationException("Invalid password");
    }

    public void logout(UserSession session) {
        userSessionRepository.delete(session.getId());
    }

}
