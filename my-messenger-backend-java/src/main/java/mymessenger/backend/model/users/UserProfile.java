/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymessenger.backend.model.users;

import java.time.LocalDate;
import org.springframework.data.annotation.Id;

/**
 *
 * @author guilherme
 */
public class UserProfile {

    @Id
    private String id;
    private String username;
    private String password;
    private LocalDate birthDate;

    public UserProfile() {

    }

    public UserProfile(String username, String password, LocalDate birthDate) {
        this.username = username;
        this.password = password;
        this.birthDate = birthDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    @Override
    public String toString() {
        return "UserProfile{" + "username=" + username + ", password=" + password + ", birthDate=" + birthDate + '}';
    }

}
