package my.messenger.androidclient.services;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import my.messenger.androidclient.api.InvalidMyMessengerRequest;
import my.messenger.androidclient.api.MyMessengerRestClient;
import my.messenger.androidclient.api.model.Destination;
import my.messenger.androidclient.api.model.DestinationType;
import my.messenger.androidclient.api.model.Group;
import my.messenger.androidclient.api.model.GroupMemberChangeRequest;
import my.messenger.androidclient.api.model.LoginRequest;
import my.messenger.androidclient.api.model.Message;
import my.messenger.androidclient.api.model.MessageType;
import my.messenger.androidclient.api.model.TransmittedMessage;
import my.messenger.androidclient.api.model.UserProfile;
import my.messenger.androidclient.api.model.UserSession;
import my.messenger.androidclient.db.AppDatabase;
import my.messenger.androidclient.db.AppDatabaseFactory;
import my.messenger.androidclient.db.ChatMessageDB;
import my.messenger.androidclient.db.ChatMessageDBDAO;
import my.messenger.androidclient.db.DestinationTypeDB;
import my.messenger.androidclient.db.UserProfileDBDAO;
import my.messenger.androidclient.db.UserSessionDB;
import my.messenger.androidclient.db.UserSessionDBDAO;

public class MyMessengerService implements MyMessengerMessageMonitorListener {

    private static final String LOGGING = "MyMessengerService";

    private static final int DefaultMessageRetrieveCount = 15;

    private static final int DefaultMessageResumeConversationCount = 5;

    private final UserProfileDBDAO userProfileDBDAO;
    private final UserSessionDBDAO userSessionDBDAO;
    private final ChatMessageDBDAO chatMessageDBDAO;

    private ArrayList<MyMessengerMessageListener> messageListeners;

    private UserSessionDB userSession;
    private MyMessengerRestClient client;
    private ContactManager contactManager;
    private MyMessengerMessageMonitor messageMonitor;

    MyMessengerService() {
        AppDatabase db = AppDatabaseFactory.create();

        this.messageListeners = new ArrayList<>();
        this.messageMonitor = null;

        this.userSession = null;
        this.client = new MyMessengerRestClient();
        this.userProfileDBDAO = db.userProfileDBDao();
        this.userSessionDBDAO = db.userSessionDBDAODao();
        this.chatMessageDBDAO = db.chatMessageDBDAO();

        tryToLoadExistingSession();
        this.contactManager = new ContactManager(userProfileDBDAO);
    }

    private void tryToLoadExistingSession() {
        if (this.userSession == null) {
            List<UserSessionDB> dbSessionList = this.userSessionDBDAO.getSession();
            if (dbSessionList != null && dbSessionList.size() == 1) {
                 this.userSession = dbSessionList.get(0);
                 startMessageMonitor();
            } else {
                this.userSessionDBDAO.truncate();
            }
        }
    }

    private void startMessageMonitor() {
        this.stopMessageMonitor();

       this.messageMonitor = new MyMessengerMessageMonitor(this.client, this.userSession, this);
       this.messageMonitor.start();
    }

    private void stopMessageMonitor() {
        if (this.messageMonitor != null) {
            this.messageMonitor.stop();
            this.messageMonitor = null;
        }
    }

    private void assetIsLoggedIn() throws MyMessengerServiceException {
        if (this.userSession == null) {
            throw new MyMessengerServiceException("invalid sessionId");
        }
    }
    
    public String getLoggedUserId() { return this.userSession.userId; }

    public String getLoggedUserName() {
        return this.userSession.userName;
    }

    public boolean isLogged() {
        return this.userSession != null;
    }

    public void logout() {
        stopMessageMonitor();
        this.userSession = null;
        reset();
    }

    private void reset() {
        this.userSessionDBDAO.truncate();
        this.userProfileDBDAO.truncate();
        this.chatMessageDBDAO.truncate();
    }

    public synchronized void registerMessageListener(MyMessengerMessageListener listener) {
        if (!messageListeners.contains(listener)) {
            messageListeners.add(listener);
        }
    }

    public synchronized void unRegisterMessageListener(MyMessengerMessageListener listener) {
        messageListeners.remove(listener);
    }

    private void raiseMessageEvent(ChatMessageDB m) {
        ArrayList<MyMessengerMessageListener> tempListenerList;

        synchronized (this) {
            if (messageListeners.size() == 0)
                return;
            tempListenerList = (ArrayList<MyMessengerMessageListener>) messageListeners.clone();
        }

        for (MyMessengerMessageListener listener : tempListenerList) {
            listener.onSentOrReceivedMessage(m);
        }
    }

    public GroupInformation searchGroupById(String groupId) throws MyMessengerServiceException {
        try {
            assetIsLoggedIn();

            Group g = client.searchGroupById(groupId, this.userSession.id);

            GroupInformation result = new GroupInformation(g);

            String[] members = g.getMembers();
            UserProfile[] upl = new UserProfile[members.length];

            for (int i = 0; i < g.getMembers().length; i++) {
                upl[i] = (searchByUserId(members[i]));
            }
            result.setMembersProfile(upl);
            result.setOwnerProfile(searchByUserId(g.getOwnerUserId()));

            return result;

        } catch (InvalidMyMessengerRequest e) {
            throw new MyMessengerServiceException("Unable to query group", e);
        }
    }

    public UserProfile searchByUserId(String userId) throws MyMessengerServiceException {
        try {
            return client.searchByUserId(userId);
        } catch (InvalidMyMessengerRequest e) {
            throw new MyMessengerServiceException("Unable to login", e);
        }
    }

    public UserProfile search(String userName) throws MyMessengerServiceException {
        try {
            return client.search(userName);
        } catch (InvalidMyMessengerRequest e) {
            throw new MyMessengerServiceException("Unable to login", e);
        }
    }

    public boolean login(LoginRequest loginRequest) throws MyMessengerServiceException {

        this.logout();

        UserSession us;

        try {
            us = client.login(loginRequest);
        } catch (InvalidMyMessengerRequest e) {
            throw new MyMessengerServiceException("Unable to login", e);
        }

        if (us != null) {

            UserSessionDB dbSession = new UserSessionDB();
            dbSession.createdAt = us.getCreatedAt();
            dbSession.id = us.getId();
            dbSession.userId = us.getUserId();
            dbSession.userName = loginRequest.getUsername();
            this.userSessionDBDAO.insert(dbSession);

            this.userSession = dbSession;

            startMessageMonitor();

            return true;
        }

        return false;
    }
    public Group createGroup(String name, List<String> membersId) throws MyMessengerServiceException {
        try {
            assetIsLoggedIn();

            Group g = new Group();
            g.setName(name);
            String groupId = client.createGroup(g, this.userSession.id);
            g.setId(groupId);

            GroupMemberChangeRequest gmcr = new GroupMemberChangeRequest();
            for (String mId: membersId) {
                gmcr.setMemberUserId(mId);
                client.addMemberToGroup(groupId, gmcr, this.userSession.id);
            }

            this.contactManager.addContact(g);

            return g;
        } catch (InvalidMyMessengerRequest e) {
            throw new MyMessengerServiceException("Unable to sendMessage", e);
        }
    }

    public void createUser(UserProfile userProfile) throws MyMessengerServiceException {

        if (userProfile == null) {
            throw new MyMessengerServiceException("userProfile is required");
        }

        if (userProfile.getBirthDate() == null) {
            throw new MyMessengerServiceException("birthDate is required");
        }

        if (userProfile.getPassword() == null || userProfile.getPassword().isEmpty()) {
            throw new MyMessengerServiceException("password is required");
        }

        if (userProfile.getUsername() == null || userProfile.getUsername().isEmpty()) {
            throw new MyMessengerServiceException("username is required");
        }

        try {
            client.createUser(userProfile);
        } catch (InvalidMyMessengerRequest e) {
            throw new MyMessengerServiceException("Unable to createUser", e);
        }
    }

    public Contact addContact(UserProfile lastProfileResult) {
        return this.contactManager.addContact(lastProfileResult);
    }

    public Contact getContact(String userId) {
        return this.contactManager.getContact(userId);
    }

    public List<Contact> getContacts() {
        return this.contactManager.getContacts();
    }

    public ChatMessageDB sendMessage(String userId, String body) throws MyMessengerServiceException {

        Contact contact = this.contactManager.getContact(userId);

        try {
            assetIsLoggedIn();

            TransmittedMessage tmsg = new TransmittedMessage();
            tmsg.setBody(body);
            tmsg.setType(MessageType.Text);
            if (contact.getType() == DestinationTypeDB.Group) {
                tmsg.setTo(new Destination(userId, DestinationType.Group));
            }
            else {
                tmsg.setTo(new Destination(userId, DestinationType.User));
            }

            client.sendMessage(tmsg, this.userSession.id);

            return handleSentMessage(contact.getUserProfile(), tmsg);

        } catch (InvalidMyMessengerRequest e) {
            throw new MyMessengerServiceException("Unable to sendMessage", e);
        }
    }

    private ChatMessageDB handleSentMessage(UserProfile userProfile, TransmittedMessage m) {
        ChatMessageDB dbMsg = new ChatMessageDB();
        dbMsg.body = m.getBody();
        dbMsg.messageDt = new Date();
        dbMsg.sent = true;
        dbMsg.connectionId = m.getTo().getId();
        dbMsg.fromUserId = this.userSession.userId;

        dbMsg.id = this.chatMessageDBDAO.insert(dbMsg);

        contactManager.setLastMessage(dbMsg.connectionId, dbMsg.body, dbMsg.messageDt, dbMsg.id);

        raiseMessageEvent(dbMsg);

        return dbMsg;
    }

    public void handleReceivedMessage(Message m) {
        String fromUserId = m.getFromUserId();

        if (fromUserId.equalsIgnoreCase(this.getLoggedUserId()))
            return;

        int destType;
        String connectionId;

        if (m.getTo().getType() == DestinationType.Group) {
            destType =  DestinationTypeDB.Group;
            // the contact is the group!
            connectionId = m.getTo().getId();
        } else {
            destType = DestinationTypeDB.User;
            connectionId = m.getFromUserId();
        }

        Contact contact = this.contactManager.getContact(connectionId);


        try {
            if (contact == null) {
                if (destType == DestinationTypeDB.Group) {
                    // received message from a group that is not in contact, add group to contacts
                    Group group = client.searchGroupById(connectionId, this.userSession.id);
                    contactManager.addContact(group);


                } else {
                    UserProfile profile = client.searchByUserId(connectionId);
                    contactManager.addContact(profile);
                }
            }

            if (! fromUserId.equalsIgnoreCase(connectionId)) {
                // we need to pre-load the contact detais, to display username in the conversation window
                Contact memberDetail = this.contactManager.getContact(fromUserId);

                if (memberDetail == null) {
                    UserProfile profile = client.searchByUserId(fromUserId);
                    contactManager.addContact(profile, true);
                }
            }

        } catch (InvalidMyMessengerRequest e) {
            Log.e(LOGGING, "failed to get contact, use id as name", e);
            return;
        }

        ChatMessageDB dbMsg = new ChatMessageDB();
        dbMsg.body = m.getBody();
        dbMsg.messageDt = m.getSentAt();
        dbMsg.sent = false;
        dbMsg.connectionId = connectionId;
        dbMsg.fromUserId = fromUserId;
        dbMsg.destinationType = destType;

        dbMsg.id = chatMessageDBDAO.insert(dbMsg);

        contactManager.setLastMessage(connectionId, m.getBody(), m.getSentAt(), dbMsg.id);

        raiseMessageEvent(dbMsg);
    }

    public void markAsRead(String userId) throws MyMessengerServiceException {
        this.contactManager.markMessagesAsRead(userId);
    }

    public void removeHistory(String userId) throws MyMessengerServiceException {
        this.chatMessageDBDAO.deleteFromUserId(userId);
        this.contactManager.removeHistory(userId);
    }

    public List<ChatMessageDB> getRecentMessagesOldThan(String userId, long olderThanMessageId) throws MyMessengerServiceException {
        List<ChatMessageDB> chatHistory = this.chatMessageDBDAO.getMessagesOldThan(userId, olderThanMessageId, DefaultMessageRetrieveCount);

        Collections.reverse(chatHistory);

        return chatHistory;
    }

    public List<ChatMessageDB> getRecentMessages(String userId) throws MyMessengerServiceException {

        List<ChatMessageDB> chatHistory;

        Contact contact = this.contactManager.getContact(userId);

        if (contact.getLastReadMessageId() > 0) {
            // get all unread messages
            chatHistory = this.chatMessageDBDAO.getFromUserIdFrom(userId, contact.getLastReadMessageId());

            // plus last N messages , to give context to the conversation
            List<ChatMessageDB> oldChatHistory = this.chatMessageDBDAO.getFromUserId(userId, contact.getLastReadMessageId(), DefaultMessageResumeConversationCount);

            chatHistory.addAll(oldChatHistory);

        } else {
            chatHistory = this.chatMessageDBDAO.getFromUserId(contact.getUserProfile().getId(), DefaultMessageRetrieveCount);
        }

        Collections.reverse(chatHistory);

        return (chatHistory);
    }

    public void leaveGroup(String groupId) throws MyMessengerServiceException {
        try {
            assetIsLoggedIn();

            // remove me from the group
            GroupMemberChangeRequest request = new GroupMemberChangeRequest();
            request.setMemberUserId(this.getLoggedUserId());
            client.leaveGroup(groupId, request, this.userSession.id);

            // delete the contact and all history
            this.chatMessageDBDAO.deleteFromUserId(groupId);
            contactManager.removeContact(groupId);

        } catch (InvalidMyMessengerRequest e) {
            throw new MyMessengerServiceException("Unable to leaveGroup", e);
        }
    }
}
