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
public class UserControllerTest extends BaseControllerTest {

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


    @Before
    public void setup() throws Exception {
        this.userRepository.deleteAll();

        UserProfile newuser = new UserProfile(userName, password, birthDate);
        
        this.userService.register(newuser);
        
        this.userId = newuser.getId();
    }

    @Test
    public void getUserNotFound() throws Exception {
        mockMvc.perform(get("/api/user/notvaliduser")
                .accept(contentType))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getValidUser() throws Exception {
        mockMvc.perform(get("/api/user/" + userName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.username", is(userName)))
                .andExpect(jsonPath("$.birthDate", is(birthDate.toString()))) //.andExpect(jsonPath("$.description", is("A description")));
                ;
    }

    @Test
    public void register() throws Exception {

        UserProfile newuser = new UserProfile("TEST" + Instant.now().toEpochMilli(), "123456", LocalDate.now());

        mockMvc.perform(post("/api/user/register")
                .contentType(contentType)
                .content(this.json(newuser)))
                .andExpect(status().isCreated());

        assertTrue(userRepository.findByUsername(newuser.getUsername()).isPresent());
    }

    @Test
    public void login() throws Exception {

        LoginRequest lr = new LoginRequest(userName, password);

        BaseMatcher testDB = new BaseMatcher() {
            @Override
            public boolean matches(Object item) {
                return userSessionRepository.findById(item.toString()).isPresent();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("has session in DB");
            }
        };

        mockMvc.perform(post("/api/user/login")
                .contentType(contentType)
                .content(this.json(lr)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.userId", is(userId)))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.id", testDB));
    }

    @Test
    public void singout() throws Exception {

    }

}
