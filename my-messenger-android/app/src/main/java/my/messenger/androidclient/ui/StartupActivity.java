package my.messenger.androidclient.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import my.messenger.androidclient.services.MyMessengerService;
import my.messenger.androidclient.services.MyMessengerServiceFactory;

public class StartupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (MyMessengerServiceFactory.getInstance().isLogged()) {
            ContactsMainActivity.startActivity(this);
        } else {
            LoginActivity.startActivity(this);
        }

        finish();
    }
}
