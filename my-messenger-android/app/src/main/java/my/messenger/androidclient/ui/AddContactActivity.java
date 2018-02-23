package my.messenger.androidclient.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import my.messenger.androidclient.R;
import my.messenger.androidclient.api.model.UserProfile;
import my.messenger.androidclient.services.Contact;
import my.messenger.androidclient.services.DateService;
import my.messenger.androidclient.services.MyMessengerService;
import my.messenger.androidclient.services.MyMessengerServiceFactory;
import my.messenger.androidclient.services.UsernameValidator;
import my.messenger.androidclient.ui.async.AsyncHelper;
import my.messenger.androidclient.ui.async.AsyncHelperCancelled;
import my.messenger.androidclient.ui.async.AsyncHelperCompleted;
import my.messenger.androidclient.ui.async.AsyncHelperFunction;
import my.messenger.androidclient.ui.async.AsyncHelperFunctionResult;

public class AddContactActivity extends AppCompatActivity {
    private static final String LOGGING = "AddContactActivity";

    private ProgressBar progressBar;
    private LinearLayout results;
    private EditText contact_add_contact_inputsearch;
    private TextView contact_add_contact_txtUsername;
    private TextView contact_add_contact_txtBirth;
    private Button contact_add_contact_btnAddContact;
    private AsyncTask<UserProfile, Void, AsyncHelperFunctionResult<Contact>> mAddContactTask = null;
    private View contact_add_contact_results_form;
    private UserProfile lastProfileResult;
    private AsyncTask<String, Void, AsyncHelperFunctionResult<UserProfile>> mSearchTask;

    public static void startActivity(Activity parent) {
        Intent intent = new Intent(parent, AddContactActivity.class);
        parent.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        this.progressBar = findViewById(R.id.contact_add_contact_progressBar);
        this.results = findViewById(R.id.contact_add_contact_results);
        this.contact_add_contact_inputsearch = findViewById(R.id.contact_add_contact_inputsearch);
        this.contact_add_contact_txtUsername = findViewById(R.id.contact_add_contact_txtUsername);
        this.contact_add_contact_txtBirth = findViewById(R.id.contact_add_contact_txtBirth);
        this.contact_add_contact_results_form = findViewById(R.id.contact_add_contact_results_form);
        this.contact_add_contact_btnAddContact = findViewById(R.id.contact_add_contact_btnAddContact);

        this.showUserProfile(null);
        this.showProgress(false);
    }

    public void btnAddCancelClick(View v) {
        onBackPressed();
    }

    public void btnAddContactClick(View v) {
        if (this.lastProfileResult == null || this.mAddContactTask != null) {
            return;
        }


        this.mAddContactTask = AsyncHelper.runAsync(

                new AsyncHelperFunction<UserProfile, Contact>() {
                    @Override
                    public Contact execute(UserProfile i) {
                        MyMessengerService svc = MyMessengerServiceFactory.getInstance();
                        return svc.addContact(i);
                    }
                },

                // completed
                new AsyncHelperCompleted<Contact>() {
                    @Override
                    public void completed(AsyncHelperFunctionResult<Contact> e) {
                        AddContactActivity.this.mAddContactTask = null;
                        AddContactActivity.this.onBackPressed();

                        if (e.err == null) {
                            Toast.makeText(getApplicationContext(), getString(R.string.contact_add_contact_added), Toast.LENGTH_LONG).show();
                        } else {
                            Log.e(LOGGING, "unable to add contact", e.err);
                        }
                    }
                },

                //  cancelled
                new AsyncHelperCancelled() {
                    @Override
                    public void cancelled() {
                        AddContactActivity.this.mAddContactTask = null;
                    }
                }).execute(this.lastProfileResult);
    }

    public void btnsearchClick(View v) {

        if (mSearchTask != null) {
            return;
        }

        showUserProfile(null);

        final String strUsername = contact_add_contact_inputsearch.getText().toString();

        if (! UsernameValidator.isUsernameValid(strUsername)) {
            contact_add_contact_inputsearch.setError(getString(R.string.error_invalid_username));
            contact_add_contact_inputsearch.requestFocus();
            return;
        }

        this.showProgress(true);
        this.mSearchTask = AsyncHelper.runAsync(

                new AsyncHelperFunction<String, UserProfile>() {
                    @Override
                    public UserProfile execute(String i) throws Exception {
                        MyMessengerService svc = MyMessengerServiceFactory.getInstance();
                        return svc.search(strUsername);
                    }
                },

                // completed
                new AsyncHelperCompleted<UserProfile>() {
                    @Override
                    public void completed(AsyncHelperFunctionResult<UserProfile> e) {
                        AddContactActivity.this.mSearchTask = null;

                        showUserProfile(e.result);

                        if (e.result == null) {
                            Toast.makeText(getApplicationContext(), getString(R.string.contact_add_search_not_found), Toast.LENGTH_LONG).show();
                        }

                        if (e.err != null) {
                            Log.e(LOGGING, "error in search", e.err);
                        }

                        AddContactActivity.this.showProgress(false);
                    }
                },

                //  cancelled
                new AsyncHelperCancelled() {
                    @Override
                    public void cancelled() {
                        AddContactActivity.this.mSearchTask = null;
                        AddContactActivity.this.showProgress(false);
                    }
                }).execute(strUsername);
    }

    private void showProgress(final boolean show) {
        this.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        this.contact_add_contact_results_form.setVisibility(show ? View.GONE : View.VISIBLE);

        this.contact_add_contact_btnAddContact.setEnabled(this.lastProfileResult != null);
    }

    private void showUserProfile(UserProfile e) {
        this.lastProfileResult = e;
        boolean hasProfile = e != null;

        this.contact_add_contact_results_form.setVisibility(hasProfile ? View.VISIBLE: View.GONE);

        if (hasProfile) {
            contact_add_contact_txtUsername.setText(e.getUsername());
            contact_add_contact_txtBirth.setText(DateService.toDateString(e.getBirthDate()));
        } else {
            contact_add_contact_txtUsername.setText("-");
            contact_add_contact_txtBirth.setText("-");
        }
    }
}
