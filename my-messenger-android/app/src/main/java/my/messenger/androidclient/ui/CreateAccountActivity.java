package my.messenger.androidclient.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import my.messenger.androidclient.R;
import my.messenger.androidclient.api.model.UserProfile;
import my.messenger.androidclient.services.DateService;
import my.messenger.androidclient.services.MyMessengerService;
import my.messenger.androidclient.services.MyMessengerServiceFactory;
import my.messenger.androidclient.services.UsernameValidator;
import my.messenger.androidclient.ui.async.AsyncHelper;
import my.messenger.androidclient.ui.async.AsyncHelperCancelled;
import my.messenger.androidclient.ui.async.AsyncHelperCompleted;
import my.messenger.androidclient.ui.async.AsyncHelperFunction;
import my.messenger.androidclient.ui.async.AsyncHelperFunctionResult;

public class CreateAccountActivity extends AppCompatActivity {
    private static final String LOGGING = "CreateAccountActivity";
    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private EditText mBirthDateView;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private AsyncTask<UserProfile, Void, AsyncHelperFunctionResult<Boolean>> mCreateUserTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mUsernameView = (EditText) findViewById(R.id.create_account_txtUsername);
        mPasswordView = (EditText) findViewById(R.id.create_account_txtPassword);
        mBirthDateView = (EditText) findViewById(R.id.create_account_txtBirthDate);
        mBirthDateView.addTextChangedListener(Mask.insert("##/##/####", mBirthDateView));

        Button mCreateAccountButton = (Button) findViewById(R.id.create_account_create_account_button);

    }

    public void createAccountOnClick(View v) {
        if (this.mCreateUserTask != null) {
            return;
        }

        mUsernameView.setError(null);
        mBirthDateView.setError(null);
        mPasswordView.setError(null);

        String sUserName = mUsernameView.getText().toString();
        String sPwd = mPasswordView.getText().toString();
        String sBirthDt = mBirthDateView.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if (!UsernameValidator.isUsernameValid(sUserName)) {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mPasswordView;
            cancel = true;
        }

        if (!cancel && (!UsernameValidator.isPasswordValid(sPwd))) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (!cancel && (TextUtils.isEmpty(sBirthDt) || !DateService.isValidDate(sBirthDt))) {
            mBirthDateView.setError(getString(R.string.error_incorrect_birthdate));
            focusView = mBirthDateView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            final UserProfile up = new UserProfile();
            up.setUsername(sUserName);
            up.setPassword(sPwd);
            up.setBirthDate(DateService.parse(sBirthDt));

            this.mCreateUserTask = AsyncHelper.runAsync(

                new AsyncHelperFunction<UserProfile, Boolean>() {
                    @Override
                    public Boolean execute(UserProfile i) throws Exception {
                        MyMessengerService svc = MyMessengerServiceFactory.getInstance();
                        svc.createUser(up);
                        return true;
                    }
                },

                // completed
                new AsyncHelperCompleted<Boolean>() {
                    @Override
                    public void completed(AsyncHelperFunctionResult<Boolean> e) {
                        CreateAccountActivity.this.mCreateUserTask = null;
                        String msg;
                        if (! e.result) {
                            msg = getString(R.string.account_unable_to_create);
                            if (e.err != null)
                                Log.e(LOGGING, "failed to created user", e.err);
                        } else {
                            msg = getString(R.string.account_created_successfully);
                        }
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                        LoginActivity.startActivity(CreateAccountActivity.this, up.getUsername());
                    }
                },

            //  cancelled
            new AsyncHelperCancelled() {
                @Override
                public void cancelled() {
                    CreateAccountActivity.this.mCreateUserTask = null;
                }
            }).execute(up);
        }
    }

    public static void startActivity(Activity parent) {
        Intent intent = new Intent(parent, CreateAccountActivity.class);
        parent.startActivity(intent);
    }
}
