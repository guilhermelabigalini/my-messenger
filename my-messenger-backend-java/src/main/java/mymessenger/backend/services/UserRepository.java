/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymessenger.backend.services;

import java.util.Optional;
import mymessenger.backend.model.users.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author guilherme
 */
public interface UserRepository extends MongoRepository<UserProfile, String> {

  // { 'location' : { '$near' : [point.x, point.y], '$maxDistance' : distance}}
  //  List<UserProfile> findByLocationNear(Point location, Distance distance);
    Optional<UserProfile> findById(String userId);
    
    Optional<UserProfile> findByUsername(String username);
}