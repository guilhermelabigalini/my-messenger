/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymessenger.backend.api;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import mymessenger.backend.Application;
import mymessenger.backend.model.messaging.Destination;
import mymessenger.backend.model.messaging.DestinationType;
import mymessenger.backend.model.messaging.MessageType;
import mymessenger.backend.model.messaging.TransmittedMessage;
import mymessenger.backend.model.users.UserProfile;
import mymessenger.backend.model.users.UserSession;
import mymessenger.backend.services.UserRepository;
import mymessenger.backend.services.UserService;
import mymessenger.backend.services.UserSessionRepository;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@Ignore
public class MessageControllerTest extends BaseControllerTest {

    private final String from_userName = "userfrom";
    private final String from_password = "123456";
    private final LocalDate from_birthDate = LocalDate.of(2010, Month.MARCH, 5);

    private final String to_userName = "userto";
    private final String to_password = "123456";
    private final LocalDate to_birthDate = LocalDate.of(2010, Month.MARCH, 5);

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserSessionRepository userSessionRepository;

    private UserSession fromUserValidSession;
    private UserProfile fromUser;
    private UserProfile toUser;

    @Before
    public void setup() throws Exception {
        this.userRepository.deleteAll();
        this.userSessionRepository.deleteAll();

        this.fromUser = new UserProfile(from_userName, from_password, from_birthDate);
        this.toUser = new UserProfile(to_userName, to_password, to_birthDate);

        this.userService.register(fromUser);
        this.userService.register(toUser);

        this.fromUserValidSession = this.userService.login(from_userName, from_password);
    }

    @Test
    public void messageNotAcceptedWithoutHeader() throws Exception {
        TransmittedMessage newMsg = new TransmittedMessage(
                new Destination(DestinationType.User, "user2"), MessageType.Text, "hello");

        mockMvc.perform(post("/api/message")
                .contentType(contentType)
                .content(this.json(newMsg)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void messageNotAcceptedWithInvalidHeader() throws Exception {
        TransmittedMessage newMsg = new TransmittedMessage(
                new Destination(DestinationType.User, to_userName), MessageType.Text, "hello");

        mockMvc.perform(post("/api/message")
                .header("Authorization", "Bearer 12345")
                .contentType(contentType)
                .content(this.json(newMsg)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void messageNotAcceptedWithValidHeader() throws Exception {
        TransmittedMessage newMsg = new TransmittedMessage(
                new Destination(DestinationType.User, toUser.getId()), MessageType.Text, "hello");

        mockMvc.perform(post("/api/message")
                .header("Authorization", "Bearer " + fromUserValidSession.getId())
                .contentType(contentType)
                .content(this.json(newMsg)))
                .andExpect(status().isOk());
    }
}
