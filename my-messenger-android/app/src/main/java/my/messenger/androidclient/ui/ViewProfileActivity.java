package my.messenger.androidclient.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import my.messenger.androidclient.R;
import my.messenger.androidclient.api.model.UserProfile;
import my.messenger.androidclient.services.DateService;
import my.messenger.androidclient.services.MyMessengerServiceFactory;
import my.messenger.androidclient.ui.async.AsyncHelper;
import my.messenger.androidclient.ui.async.AsyncHelperCompleted;
import my.messenger.androidclient.ui.async.AsyncHelperFunction;
import my.messenger.androidclient.ui.async.AsyncHelperFunctionResult;

public class ViewProfileActivity extends AppCompatActivity {
    private final static String PARAM_UserProfile = "ViewProfileActivity_UserProfile";
    private static final String LOGGING = "ViewProfileActivity";

    private String userId;
    private TextView txtUsername;
    private TextView txtBirth;
    private ScrollView ViewProfile_results_form;
    private ProgressBar ViewProfile_progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        Intent intent = this.getIntent();
        this.userId = intent.getStringExtra(PARAM_UserProfile);

        this.txtUsername = (TextView)findViewById(R.id.ViewProfile_txtUsername);
        this.txtBirth = (TextView)findViewById(R.id.ViewProfile_txtBirth);
        this.ViewProfile_results_form = (ScrollView)findViewById(R.id.ViewProfile_results_form);
        this.ViewProfile_progressBar = (ProgressBar)findViewById(R.id.ViewProfile_progressBar);

        ViewProfile_results_form.setVisibility(View.GONE);
        ViewProfile_progressBar.setVisibility(View.VISIBLE);

        displayContactInfo();
    }

    private void displayContactInfo() {
        AsyncHelper.runAsync(
                new AsyncHelperFunction<String, UserProfile>() {
                    @Override
                    public UserProfile execute(String userId) throws Exception  {
                        return MyMessengerServiceFactory.getInstance().searchByUserId(userId);
                    }
                },
                new AsyncHelperCompleted<UserProfile>()  {
                    @Override
                    public void completed(AsyncHelperFunctionResult<UserProfile> result) {
                        if (result.err == null) {
                            ViewProfileActivity.this.displayUserProfile(result.result);
                        } else {
                            Log.e(LOGGING, "unable to clear messages", result.err);
                        }
                    }
                }).execute(this.userId);
    }

    private void displayUserProfile(UserProfile result) {
        boolean hasProfile = result != null;

        this.ViewProfile_results_form.setVisibility(View.VISIBLE);
        this.ViewProfile_progressBar.setVisibility(View.GONE);

        if (hasProfile) {
            this.txtUsername.setText(result.getUsername());
            this.txtBirth.setText(DateService.toDateString(result.getBirthDate()));
        } else {
            this.txtUsername.setText("-");
            this.txtBirth.setText("-");
        }
    }

    public static void startActivity(Activity parent, String userId) {
        Intent intent = new Intent(parent, ViewProfileActivity.class);
        intent.putExtra(PARAM_UserProfile, userId);
        parent.startActivity(intent);
    }
}
