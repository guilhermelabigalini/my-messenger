package my.messenger.androidclient.api.model;

/**
 * Created by guilherme on 2/16/2018.
 */

public class GroupMemberChangeRequest {
    private String memberUserId;

    public String getMemberUserId() {
        return memberUserId;
    }

    public void setMemberUserId(String memberUserId) {
        this.memberUserId = memberUserId;
    }
}
