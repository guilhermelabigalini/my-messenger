package my.messenger.androidclient.api;

import android.support.annotation.NonNull;
import android.util.Log;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

import my.messenger.androidclient.api.model.Group;
import my.messenger.androidclient.api.model.GroupMemberChangeRequest;
import my.messenger.androidclient.api.model.LoginRequest;
import my.messenger.androidclient.api.model.Message;
import my.messenger.androidclient.api.model.TransmittedMessage;
import my.messenger.androidclient.api.model.UserProfile;
import my.messenger.androidclient.api.model.UserSession;

// https://stackoverflow.com/questions/4075991/post-request-via-resttemplate-in-json
// https://github.com/PCreations/RESTDroid
public class MyMessengerRestClient {

    private static final String LOGGING = "MyMessengerClient";

    private static final String BaseAPIUrl = "https://my-messenger-backend.azurewebsites.net/";

    private final RestTemplate restTemplate;
    private static final Class<ArrayList<Message>> clazzListMessage =  (Class<ArrayList<Message>>) new ArrayList<Message>().getClass();

    public MyMessengerRestClient() {
        this.restTemplate = new RestTemplate();
    }

    private <T2> T2 getEntity(String path, Class<T2> responseType, HttpStatus expectedResult, String session) throws InvalidMyMessengerRequest {
        HttpHeaders headers = getHttpHeaders(session);
        HttpEntity<T2> entity = new HttpEntity<T2>(headers);

        ResponseEntity<T2> result = restTemplate.exchange(this.BaseAPIUrl + path, HttpMethod.GET, entity, responseType);

        if (result.getStatusCode() == expectedResult)
            return result.getBody();

        throw new InvalidMyMessengerRequest("Unable to get " + path + " response is " + result.toString());
    }

    private <T,T2> ResponseEntity<T> postForEntity(String path, T2 request, Class<T> responseType, HttpStatus expectedResult, String session) throws InvalidMyMessengerRequest {
        HttpHeaders headers = getHttpHeaders(session);
        HttpEntity<T2> entity = new HttpEntity<T2>(request, headers);

        ResponseEntity<T> result = restTemplate.postForEntity(this.BaseAPIUrl + path, entity, responseType);

        if (result.getStatusCode() == expectedResult)
            return result;

        throw new InvalidMyMessengerRequest("Unable to post to " + path + " response is " + result.toString());
    }

    @NonNull
    private HttpHeaders getHttpHeaders(String sessionId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        if (sessionId != null)
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + sessionId);

        return headers;
    }

    public UserSession login(LoginRequest lr) throws InvalidMyMessengerRequest {

        Log.d(LOGGING, "logging with " + lr);

        UserSession us = postForEntity("api/user/login", lr, UserSession.class, HttpStatus.OK, null).getBody();

        return us;
    }

    public void createUser(UserProfile userProfile) throws InvalidMyMessengerRequest {

        Log.d(LOGGING, "creating user " + userProfile);

        this.postForEntity("api/user/register", userProfile, String.class, HttpStatus.CREATED,null);
    }

    public Group searchGroupById(String groupId, String sessionId) throws InvalidMyMessengerRequest {
        return this.getEntity("api/group/" + groupId, Group.class, HttpStatus.OK, sessionId);
    }

    public UserProfile searchByUserId(String userId) throws InvalidMyMessengerRequest {
        return this.getEntity("api/user?userId=" + userId, UserProfile.class, HttpStatus.OK, null);
    }

    public UserProfile search(String userName) throws InvalidMyMessengerRequest {
        return this.getEntity("api/user/" + userName, UserProfile.class, HttpStatus.OK, null);
    }

    public void sendMessage(TransmittedMessage tmsg, String sessionId) throws InvalidMyMessengerRequest {
        Log.d(LOGGING, "sending message " + tmsg);

        this.postForEntity("api/message", tmsg, String.class, HttpStatus.OK, sessionId);
    }

    public Message[] getMessages(int maxMessages, String sessionId) throws InvalidMyMessengerRequest {
        Log.d(LOGGING, "gettings messages");

        // https://stackoverflow.com/questions/2797914/passing-the-classt-in-java-of-a-generic-list

        //Class<List<Message>> clazz = (Class) List.class;

        return this.getEntity("api/message?count=" + maxMessages, Message[].class, HttpStatus.OK, sessionId);
    }

    public void leaveGroup(String groupId, GroupMemberChangeRequest request, String sessionId) throws InvalidMyMessengerRequest {
        Log.d(LOGGING, "leaving group" + groupId);

        this.postForEntity("api/group/" + groupId + "/Remove", request, String.class, HttpStatus.OK, sessionId);
    }

    public String createGroup(Group group, String sessionId) throws InvalidMyMessengerRequest {
        ResponseEntity<String> response = this.postForEntity("api/group", group, String.class, HttpStatus.CREATED, sessionId);

        URI location = response.getHeaders().getLocation();
        String[] path = location.getPath().split("/");

        // Location = /api/group/44a7b6b6c51944b896f4a80597c1c0f8
        // get the last piece, that is the ID
        String groupId = path[path.length - 1];
        group.setId(groupId);
        return groupId;
    }

    public void addMemberToGroup(String groupId, GroupMemberChangeRequest request, String sessionId) throws InvalidMyMessengerRequest {
        Log.d(LOGGING, "adding user to group" + groupId);

        this.postForEntity("api/group/" + groupId + "/Add", request, String.class, HttpStatus.OK, sessionId);
    }
}
