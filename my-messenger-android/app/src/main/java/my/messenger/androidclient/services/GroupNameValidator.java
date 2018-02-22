package my.messenger.androidclient.services;

public class GroupNameValidator {

    public static boolean isGroupNameValid(String username) {
        return (username != null)
                && username.length() >= 4;
    }
}
