package my.messenger.androidclient.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import my.messenger.androidclient.R;
import my.messenger.androidclient.api.model.Group;
import my.messenger.androidclient.api.model.UserProfile;
import my.messenger.androidclient.db.ChatMessageDB;
import my.messenger.androidclient.db.DestinationTypeDB;
import my.messenger.androidclient.services.Contact;
import my.messenger.androidclient.services.GroupInformation;
import my.messenger.androidclient.services.MyMessengerServiceFactory;
import my.messenger.androidclient.ui.async.AsyncHelper;
import my.messenger.androidclient.ui.async.AsyncHelperCancelled;
import my.messenger.androidclient.ui.async.AsyncHelperCompleted;
import my.messenger.androidclient.ui.async.AsyncHelperFunction;
import my.messenger.androidclient.ui.async.AsyncHelperFunctionResult;

public class GroupDetailsActivity extends Activity {

    private final static String PARAM_ContactUserID = "PARAM_GroupID";
    private static final String LOGGING = "GroupDetailsActivity";
    private String groupID;
    private TextView activity_group_details_lblNMembers;
    private TextView activity_group_details_lblOwner;
    private TextView activity_group_details_lblGroupName;
    private ListView activity_group_details_lstMembers;
    private ProgressBar activity_group_details_progressBar_pgLoadingGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        Intent intent = this.getIntent();
        this.groupID = intent.getStringExtra(PARAM_ContactUserID);

        this.activity_group_details_lblGroupName = (TextView)findViewById(R.id.activity_group_details_lblGroupName);
        this.activity_group_details_lblOwner = (TextView)findViewById(R.id.activity_group_details_lblOwner);
        this.activity_group_details_lblNMembers = (TextView)findViewById(R.id.activity_group_details_lblNMembers);
        this.activity_group_details_lstMembers = (ListView)findViewById(R.id.activity_group_details_lstMembers);
        this.activity_group_details_progressBar_pgLoadingGroup = (ProgressBar)findViewById(R.id.activity_group_details_progressBar_pgLoadingGroup);

        loadGroupInformation();
    }

    private void loadGroupInformation() {
        activity_group_details_progressBar_pgLoadingGroup.setVisibility(View.VISIBLE);

        AsyncHelper.runAsync(
                new AsyncHelperFunction<String, GroupInformation>() {
                    @Override
                    public GroupInformation execute(String userId) throws Exception  {
                        return MyMessengerServiceFactory.getInstance().searchGroupById(userId);
                    }
                },
                new AsyncHelperCompleted<GroupInformation>()  {
                    @Override
                    public void completed(AsyncHelperFunctionResult<GroupInformation> result) {
                        GroupDetailsActivity.this.activity_group_details_progressBar_pgLoadingGroup.setVisibility(View.GONE);
                        if (result.err == null) {
                            GroupDetailsActivity.this.displayGroupInfo(result.result);
                        } else {
                            Log.e(LOGGING, "unable to get group", result.err);
                        }
                    }
                },
                new AsyncHelperCancelled(){
                    @Override
                    public void cancelled() {
                        GroupDetailsActivity.this.activity_group_details_progressBar_pgLoadingGroup.setVisibility(View.GONE);
                    }
                }).execute(this.groupID);
    }

    private void displayGroupInfo(GroupInformation result) {
        activity_group_details_lblGroupName.setText(result.getName());
        activity_group_details_lblOwner.setText(result.getOwnerProfile().getUsername());
        activity_group_details_lblNMembers.setText(Integer.toString(result.getMembers().length));

        List<String> userNames = new ArrayList<>();
        for (UserProfile c: result.getMembersProfile()) {
            userNames.add(c.getUsername());
        }
        Collections.sort(userNames);

        ArrayAdapter<String> adapter
                = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                userNames);
        activity_group_details_lstMembers.setChoiceMode(ListView.CHOICE_MODE_NONE);
        activity_group_details_lstMembers.setAdapter(adapter);
    }

    public static void startActivity(Activity parent, String groupId) {
        Intent intent = new Intent(parent, GroupDetailsActivity.class);
        intent.putExtra(PARAM_ContactUserID, groupId);
        parent.startActivity(intent);
    }
}
