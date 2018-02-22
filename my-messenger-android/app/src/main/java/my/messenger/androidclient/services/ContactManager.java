package my.messenger.androidclient.services;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import my.messenger.androidclient.api.model.Group;
import my.messenger.androidclient.api.model.UserProfile;
import my.messenger.androidclient.db.DestinationTypeDB;
import my.messenger.androidclient.db.UserProfileDB;
import my.messenger.androidclient.db.UserProfileDBDAO;

/**
 * Created by guilherme on 2/2/2018.
 */

class ContactManager {
    private static final int MAX_LASTMESSAGE_LEN = 15;

    private static final String LOGGING = "ContactManager";
    private final UserProfileDBDAO userProfileDBDAO;
    private HashMap<String,Contact> contactHashMap;
    private static Comparator<Contact> comparatorDesc = new Comparator<Contact>() {
        @Override
        public int compare(Contact left, Contact right) {
            if (left == null && right == null)
                return 0;

            Date d1 = left.getLastMessageReceivedAt();
            Date d2 = right.getLastMessageReceivedAt();

            if (d1 == null && d2 == null)
                return 0;

            if (d1 == null && d2 != null)
                return 1;

            if (d1 != null && d2 == null)
                return -1;

            return d2.compareTo(d1);
        }
    };

    ContactManager(UserProfileDBDAO userProfileDBDAO) {
        this.contactHashMap = new HashMap<>();
        this.userProfileDBDAO = userProfileDBDAO;
        tryToLoadExistingContacts();
    }

    private void tryToLoadExistingContacts() {

        contactHashMap.clear();

        List<UserProfileDB> resultDb = this.userProfileDBDAO.getAll();

        for (UserProfileDB db: resultDb) {
            addContactDBToHash(db);
        }
    }

    private Contact addContactDBToHash(UserProfileDB db) {
        Contact up = new Contact(
                new UserProfile(db.id, db.userName),
                db.lastMessage,
                db.lastMessageAt,
                db.unreadCount,
                db.lastReceivedMessageId,
                db.lastReadMessageId,
                db.type,
                db.hidden);

        contactHashMap.put(db.id, up);

        return up;
    }

    List<Contact> getContacts() {
        List<Contact> list = new ArrayList<>();
        for (Contact c: this.contactHashMap.values()) {
            if (! c.isHidden())
                list.add(c);
        }
        Collections.sort(list, comparatorDesc);
        return list;
    }

    Contact getContact(String userId) {
        return contactHashMap.get(userId);
    }


    void markMessagesAsRead(String userId) {
        Contact result = getContact(userId);

        if (result != null) {
            long lastMsgId = result.getLastReceivedMessageId();

            result.setUnreadCount(0);
            result.setLastReadMessageId(lastMsgId);

            userProfileDBDAO.markMessagesAsRead(result.getUserProfile().getId(), lastMsgId);
        }
    }

    void removeHistory(String userId) {
        Contact result = getContact(userId);

        if (result != null) {
            result.setLastMessage(null, null, 0);
            result.setUnreadCount(0);
        }

        userProfileDBDAO.setLastMessage(userId, null, null, 0, 0);
    }

    void setLastMessage(String userId, String body, Date at, long lastMessageid) {
        Contact result = getContact(userId);

        if (result != null) {

            if (body.length() > MAX_LASTMESSAGE_LEN)
                body = body.substring(0, MAX_LASTMESSAGE_LEN - 1) + "...";

            result.setLastMessage(body, at, lastMessageid);
            result.setUnreadCount(1 + result.getUnreadCount());

            int rows = userProfileDBDAO.setLastMessage(userId, body, at, result.getUnreadCount(), lastMessageid);
            Log.d(LOGGING, "updated last message, affected rows: " + rows);
        }
    }

    Contact addContact(Group group) {

        Contact result = getContact(group.getId());

        if (result == null) {
            Log.d(LOGGING, "contact is being added");
            return internalAddContact(group);
        } else {
            Log.d(LOGGING, "contact is already on the list");
            return result;
        }
    }

    Contact addContact(UserProfile profile) {
        return addContact(profile, false);
    }

    Contact addContact(UserProfile profile, boolean hidden) {

        Contact result = getContact(profile.getId());

        if (result == null) {
            Log.d(LOGGING, "contact is being added");
            return internalAddContact(profile, hidden);
        } else {
            Log.d(LOGGING, "contact is already on the list");

            if (result.isHidden()) {
                this.userProfileDBDAO.setHidden(profile.getId(), false);
                result.setHidden(false);
            }

            return result;
        }
    }

    private Contact internalAddContact(Group group) {
        UserProfileDB updb = new UserProfileDB();
        updb.id = group.getId();
        updb.userName = group.getName();
        updb.type = DestinationTypeDB.Group;
        updb.hidden = false;
        this.userProfileDBDAO.insert(updb);
        return addContactDBToHash(updb);
    }

    private Contact internalAddContact(UserProfile lastProfileResult, boolean hidden) {
        UserProfileDB updb = new UserProfileDB();
        updb.id = lastProfileResult.getId();
        updb.userName = lastProfileResult.getUsername();
        updb.type = DestinationTypeDB.User;
        updb.hidden = hidden;
        this.userProfileDBDAO.insert(updb);
        return addContactDBToHash(updb);
    }

    public void removeContact(String userId) {
        this.userProfileDBDAO.delete(userId);
        this.contactHashMap.remove(userId);
    }
}
