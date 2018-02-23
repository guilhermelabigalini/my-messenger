package my.messenger.androidclient.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import my.messenger.androidclient.R;
import my.messenger.androidclient.api.model.Group;
import my.messenger.androidclient.api.model.UserProfile;
import my.messenger.androidclient.db.ChatMessageDB;
import my.messenger.androidclient.db.DestinationTypeDB;
import my.messenger.androidclient.services.Contact;
import my.messenger.androidclient.services.GroupNameValidator;
import my.messenger.androidclient.services.MyMessengerServiceFactory;
import my.messenger.androidclient.ui.async.AsyncHelper;
import my.messenger.androidclient.ui.async.AsyncHelperCancelled;
import my.messenger.androidclient.ui.async.AsyncHelperCompleted;
import my.messenger.androidclient.ui.async.AsyncHelperFunction;
import my.messenger.androidclient.ui.async.AsyncHelperFunctionResult;

public class CreateGroupActivity extends Activity {

    private static final String LOGGING = "CreateGroupActivity";

    private ListView activity_create_group_lstContacts;
    private ProgressBar activity_create_group_progressBar;
    private EditText activity_create_group_txtGroupName;
    private List<Contact> contacts;
    private List<String> userNames;
    private List<UserProfile> userProfiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        activity_create_group_lstContacts = this.findViewById(R.id.activity_create_group_lstContacts);
        activity_create_group_txtGroupName = this.findViewById(R.id.activity_create_group_txtGroupName);
        activity_create_group_progressBar = this.findViewById(R.id.activity_create_group_progressBar);

        displayContacts();
    }

    public void btnCreateGroup_Click(View view) {

        int cntChoice = activity_create_group_lstContacts.getCount();
        SparseBooleanArray sparseBooleanArray = activity_create_group_lstContacts.getCheckedItemPositions();

        final List<String> selectedMembers = new ArrayList<>();

        for(int i = 0; i < cntChoice; i++){

            if(sparseBooleanArray.get(i)) {
                selectedMembers.add(this.userProfiles.get(i).getId());
            }
        }

        final String groupName = activity_create_group_txtGroupName.getText().toString().trim();

        if (! GroupNameValidator.isGroupNameValid((groupName))) {
            String err = getString(R.string.activity_create_group_invalid_group_name);

            activity_create_group_txtGroupName.requestFocus();
            activity_create_group_txtGroupName.setError(err);
            Toast.makeText(this, err, Toast.LENGTH_LONG).show();
            return;
        }

        if (selectedMembers.size() <= 0) {
            Toast.makeText(this, R.string.activity_create_group_must_select_members, Toast.LENGTH_LONG).show();
            return;
        }

        this.activity_create_group_progressBar.setVisibility(View.VISIBLE);

        AsyncHelper.runAsync(
                new AsyncHelperFunction<Void, Group>() {
                    @Override
                    public Group execute(Void v) throws Exception  {
                        return MyMessengerServiceFactory.getInstance().createGroup(groupName, selectedMembers);
                    }
                },
                new AsyncHelperCompleted<Group>()  {
                    @Override
                    public void completed(AsyncHelperFunctionResult<Group> result) {
                        CreateGroupActivity.this.activity_create_group_progressBar.setVisibility(View.GONE);
                        if (result.err == null) {
                            Toast.makeText(getApplicationContext(), R.string.activity_create_group_group_created, Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Log.e(LOGGING, "unable to create group", result.err);
                        }
                    }
                },
                new AsyncHelperCancelled(){
                    @Override
                    public void cancelled() {
                        CreateGroupActivity.this.activity_create_group_progressBar.setVisibility(View.GONE);
                    }
                }).execute((Void)null);
    }

    private void displayContacts() {
        this.contacts = MyMessengerServiceFactory.getInstance().getContacts();
        this.userNames = new ArrayList<>();
        this.userProfiles = new ArrayList<>();
        for (Contact c: contacts) {
            if (c.getType() == DestinationTypeDB.User) {
                userProfiles.add(c.getUserProfile());
                userNames.add(c.getUserProfile().getUsername());
            }
        }
        Collections.sort(userNames);

        ArrayAdapter<String> adapter
                = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice,
                userNames);
        activity_create_group_lstContacts.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        activity_create_group_lstContacts.setAdapter(adapter);
    }

    public static void startActivity(Activity parent) {
        Intent intent = new Intent(parent, CreateGroupActivity.class);
        parent.startActivity(intent);
    }
}
