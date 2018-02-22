package my.messenger.androidclient.services;

/**
 * Created by guilherme on 1/14/2018.
 */

public class UsernameValidator {

    public static boolean isUsernameValid(String username) {
        return (username != null)
                && username.length() >= 4
                && username.matches("[A-Za-z0-9_]+");
    }

    public static boolean isPasswordValid(String password) {
        return (password != null
                && password.length() > 4);
    }
}
