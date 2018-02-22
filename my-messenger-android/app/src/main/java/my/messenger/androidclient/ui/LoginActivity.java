package my.messenger.androidclient.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import my.messenger.androidclient.R;
import my.messenger.androidclient.api.model.LoginRequest;
import my.messenger.androidclient.services.MyMessengerService;
import my.messenger.androidclient.services.MyMessengerServiceFactory;
import my.messenger.androidclient.services.UsernameValidator;
import my.messenger.androidclient.ui.async.AsyncHelper;
import my.messenger.androidclient.ui.async.AsyncHelperCancelled;
import my.messenger.androidclient.ui.async.AsyncHelperCompleted;
import my.messenger.androidclient.ui.async.AsyncHelperFunction;
import my.messenger.androidclient.ui.async.AsyncHelperFunctionResult;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  {
    private static final String LOGGING = "LoginActivity";
    private static final String PARAM_USERNAME = "LoginActivity_I_USERNAME";

    private AsyncTask<LoginRequest, Void, AsyncHelperFunctionResult<Boolean>> mAuthTask = null;

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button mCreateAccountButton = (Button) findViewById(R.id.email_create_account);
        mCreateAccountButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });


        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void createAccount() {
        CreateAccountActivity.startActivity(this);
    }
    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String strUsername = mUsernameView.getText().toString();
        String strPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(strPassword) && !UsernameValidator.isPasswordValid(strPassword)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(strUsername)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!UsernameValidator.isUsernameValid(strUsername)) {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            final LoginRequest lr = new LoginRequest(strUsername, strPassword);
            //new UserLoginTask(strUsername, strPassword);
            this.mAuthTask = AsyncHelper.runAsync(

                    new AsyncHelperFunction<LoginRequest, Boolean>() {
                        @Override
                        public Boolean execute(LoginRequest i) throws Exception {
                            MyMessengerService svc = MyMessengerServiceFactory.getInstance();
                            return svc.login(lr);
                        }
                    },

                    // completed
                    new AsyncHelperCompleted<Boolean>() {
                        @Override
                        public void completed(AsyncHelperFunctionResult<Boolean> e) {
                            LoginActivity.this.mAuthTask = null;
                            LoginActivity.this.showProgress(false);

                            if (e.err != null) {
                                Log.e(LOGGING, "Unable to login", e.err);
                            }

                            if (!e.result || e.err != null) {
                                mPasswordView.setError(getString(R.string.error_incorrect_password));
                                mPasswordView.requestFocus();
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.login_successfully), Toast.LENGTH_LONG).show();
                                ContactsMainActivity.startActivity(LoginActivity.this);
                                LoginActivity.this.finish();
                            }
                        }
                    },

                    //  cancelled
                    new AsyncHelperCancelled() {
                        @Override
                        public void cancelled() {
                            LoginActivity.this.mAuthTask = null;
                            LoginActivity.this.showProgress(false);
                        }
                    }).execute(lr);
        }
    }
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public static void startActivity(Activity parent, String userName) {
        Intent intent = new Intent(parent, LoginActivity.class);
        intent.getExtras().putString(PARAM_USERNAME, userName);
        parent.startActivity(intent);
    }

    public static void startActivity(Activity parent) {
        Intent intent = new Intent(parent, LoginActivity.class);
        parent.startActivity(intent);
    }

}

