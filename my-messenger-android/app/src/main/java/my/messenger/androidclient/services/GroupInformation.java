package my.messenger.androidclient.services;

import my.messenger.androidclient.api.model.Group;
import my.messenger.androidclient.api.model.UserProfile;

public class GroupInformation extends Group {

    UserProfile[] membersProfile;
    UserProfile ownerProfile;

    public GroupInformation(Group g) {
        this.setMembers(g.getMembers());
        this.setId(g.getId());
        this.setOwnerUserId(g.getOwnerUserId());
        this.setName(g.getName());
    }

    public UserProfile[] getMembersProfile() {
        return membersProfile;
    }

    public void setMembersProfile(UserProfile[] membersProfile) {
        this.membersProfile = membersProfile;
    }

    public UserProfile getOwnerProfile() {
        return ownerProfile;
    }

    public void setOwnerProfile(UserProfile ownerProfile) {
        this.ownerProfile = ownerProfile;
    }
}
