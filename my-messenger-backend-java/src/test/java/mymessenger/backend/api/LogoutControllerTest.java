/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymessenger.backend.api;

import mymessenger.backend.Application;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import mymessenger.backend.model.users.UserProfile;
import mymessenger.backend.model.users.UserSession;
import mymessenger.backend.services.UserRepository;
import mymessenger.backend.services.UserService;
import mymessenger.backend.services.UserSessionRepository;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 *
 * @author guilherme
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@Ignore
public class LogoutControllerTest extends BaseControllerTest {

    private final String userName = "bdussault";
    private final String password = "123456";
    private final LocalDate birthDate = LocalDate.of(2010, Month.MARCH, 5);
    private String userId;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSessionRepository userSessionRepository;
    private UserSession validSession;

    @Before
    public void setup() throws Exception {
        this.userRepository.deleteAll();
        this.userSessionRepository.deleteAll();

        UserProfile newuser = new UserProfile(userName, password, birthDate);

        this.userService.register(newuser);

        this.validSession = this.userService.login(userName, password);

        this.userId = newuser.getId();
    }

    @Test
    public void dontAuthorizedWithoutHeader() throws Exception {
        mockMvc.perform(get("/api/logout")
        //                .accept(contentType)
        )
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void dontAuthorizedWithInvalidHeader() throws Exception {
        mockMvc.perform(get("/api/logout")
                //                .accept(contentType)
                .header("Authorization", "Bearer 12345"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void singout() throws Exception {
        assertTrue(userSessionRepository.findById(validSession.getId()).isPresent());

        mockMvc.perform(get("/api/logout")
                .header("Authorization", "Bearer " + validSession.getId()))
                .andExpect(status().isOk());

        assertFalse(userSessionRepository.findById(validSession.getId()).isPresent());
    }

}
