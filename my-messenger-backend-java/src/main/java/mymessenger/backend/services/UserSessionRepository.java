/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymessenger.backend.services;

import java.util.List;
import java.util.Optional;
import mymessenger.backend.model.users.UserSession;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author guilherme
 */
public interface UserSessionRepository extends MongoRepository<UserSession, String> {

    Optional<UserSession> findById(String sessionid);
    List<UserSession> findByUserId(String username);
}