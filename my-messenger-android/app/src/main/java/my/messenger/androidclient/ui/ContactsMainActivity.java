package my.messenger.androidclient.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import my.messenger.androidclient.R;
import my.messenger.androidclient.db.ChatMessageDB;
import my.messenger.androidclient.services.Contact;
import my.messenger.androidclient.services.DateService;
import my.messenger.androidclient.services.MyMessengerMessageListener;
import my.messenger.androidclient.services.MyMessengerService;
import my.messenger.androidclient.services.MyMessengerServiceFactory;
import my.messenger.androidclient.ui.async.AsyncHelper;
import my.messenger.androidclient.ui.async.AsyncHelperCompleted;
import my.messenger.androidclient.ui.async.AsyncHelperFunction;
import my.messenger.androidclient.ui.async.AsyncHelperFunctionResult;

public class ContactsMainActivity extends AppCompatActivity implements MyMessengerMessageListener {

    private final static String LOGGING = "ContactsMainActivity";

    private ListView contactList;
    private Button btnAddNewContact;

    private class ContactItemAdapter extends ArrayAdapter<Contact> {

        private final LayoutInflater inflater;

        public ContactItemAdapter(Context context, List<Contact> values) {
            super(context, -1, values);
            this.inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View rowView = inflater.inflate(R.layout.activity_contacts_main_list_item, parent, false);
            TextView txtUsername = (TextView) rowView.findViewById(R.id.contacts_list_item_username);
            TextView txtLastMessage = (TextView) rowView.findViewById(R.id.contacts_list_item_last_message);
            TextView txtReceivedAt = (TextView) rowView.findViewById(R.id.contacts_list_item_received_at);
            TextView txtUnreadCount = (TextView) rowView.findViewById(R.id.contacts_list_item_unread_count);
            Contact c = this.getItem(position);
            txtUsername.setText(c.getUserProfile().getUsername());
            txtLastMessage.setText(c.getLastMessageBody());
            txtReceivedAt.setText(DateService.toShortDateTimeString(c.getLastMessageReceivedAt()));

            if (c.getUnreadCount() > 0) {
                txtUnreadCount.setText(Integer.toString(c.getUnreadCount()));
                txtUnreadCount.setVisibility(View.VISIBLE);
            } else {
                txtUnreadCount.setText(null);
                txtUnreadCount.setVisibility(View.GONE);
            }

            return rowView;
        }
    }

    public static void startActivity(Activity parent) {
        Intent intent = new Intent(parent, ContactsMainActivity.class);
        parent.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contact_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.contact_menu_singout:
                handleSingoutRequest();
                return true;
            case R.id.contact_menu_create_group:
                handleCreateGroupRequest();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void handleCreateGroupRequest() {
        /*final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final AlertDialog alertDialog = builder
                .setTitle(getString(R.string.contact_main_create_group_popoup_title))
                .setMessage(getString(R.string.contact_main_create_group_popoup_message))
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setView(input)
                .create();

        alertDialog.show();

        Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        theButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input.getText().toString().length() > 0) {
                    Toast.makeText(getApplicationContext(),
                            "TODO, create group: " + input.getText().toString(), Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Name required", Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        CreateGroupActivity.startActivity(this);
    }

    @Override
    public void onSentOrReceivedMessage(final ChatMessageDB m) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshContacts();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_main);

        this.contactList = findViewById(R.id.contact_list_listview);
        this.btnAddNewContact = findViewById(R.id.btnAddNewContact);

        MyMessengerService service = MyMessengerServiceFactory.getInstance();

        if (! service.isLogged()) {
            LoginActivity.startActivity(this);
            return;
        }

        refreshContacts();

        contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView< ? > adapter, View view, int position, long arg){
                //TextView v = (TextView) view.findViewById(R.id.contacts_list_item_username);
                //Toast.makeText(getApplicationContext(), "selected Item Name is " + v.getText(), Toast.LENGTH_LONG).show();

                Contact selectCnt = ((ContactItemAdapter)adapter.getAdapter()).getItem(position);
                ChatActivity.startActivity(ContactsMainActivity.this, selectCnt.getUserProfile().getId());
            }
        });

        contactList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View view, int position, long id) {
                Contact selectCnt = ((ContactItemAdapter)adapter.getAdapter()).getItem(position);
                handleLongClickOnContact(selectCnt);
                return true;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyMessengerServiceFactory.getInstance().unRegisterMessageListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyMessengerServiceFactory.getInstance().registerMessageListener(this);
        this.refreshContacts();
    }

    private void handleLongClickOnContact(final Contact contact) {
        final CharSequence[] items = {
                getString(R.string.contact_main_long_click_mark_as_read),
                getString(R.string.contact_main_long_click_clear_history)
        };

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.contact_main_long_click_title))
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                // read as read
                                ContactsMainActivity.this.markMessagesAsRead(contact);
                                break;
                            case 1:
                                // clear history
                                ContactsMainActivity.this.clearMessagesHistory(contact);
                                break;
                        }
                    }
                })
                .create()
                .show();


    }

    private void clearMessagesHistory(final Contact contact) {
        AsyncHelper.runAsync(
                new AsyncHelperFunction<Void, Void>() {
                    @Override
                    public Void execute(Void i) throws Exception {
                        MyMessengerService service = MyMessengerServiceFactory.getInstance();
                        service.removeHistory(contact.getUserProfile().getId());
                        return null;
                    }
                },
                new AsyncHelperCompleted<Void>() {
                    @Override
                    public void completed(AsyncHelperFunctionResult<Void> e) {
                        ContactsMainActivity.this.refreshContacts();
                    }
                }).execute((Void)null);
    }

    private void markMessagesAsRead(final Contact contact) {
        AsyncHelper.runAsync(
                new AsyncHelperFunction<Void, Void>() {
                    @Override
                    public Void execute(Void i) throws Exception {
                        MyMessengerService service = MyMessengerServiceFactory.getInstance();
                        service.markAsRead(contact.getUserProfile().getId());
                        return null;
                    }
                },
                new AsyncHelperCompleted<Void>() {
                    @Override
                    public void completed(AsyncHelperFunctionResult<Void> e) {
                        ContactsMainActivity.this.refreshContacts();
                    }
                }).execute((Void)null);
    }

    private void handleSingoutRequest() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.contact_singout_popup_title))
                .setMessage(getString(R.string.contact_singout_popup_message))
                .setPositiveButton(android.R.string.yes,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            doSingout();
                            dialog.dismiss();
                        }
                    })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                         dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void doSingout() {
        AsyncHelper.runAsync(
                new AsyncHelperFunction<Void, Boolean>() {
                    @Override
                    public Boolean execute(Void i) {
                        MyMessengerService service = MyMessengerServiceFactory.getInstance();
                        service.logout();
                        return true;
                    }
                },
                new AsyncHelperCompleted<Boolean>() {
                    @Override
                    public void completed(AsyncHelperFunctionResult<Boolean> e) {
                        ContactsMainActivity.this.onBackPressed();
                    }
                }).execute((Void)null);
    }

    private void refreshContacts() {

        /*final ContactItem[] data = new ContactItem[100];
        for (int i = 0; i < 100;i++) {
            data[i] = new ContactItem("username" + i, "last message " +i);
        };*/

        AsyncHelper.runAsync(
                new AsyncHelperFunction<Void, List<Contact>>() {
                    @Override
                    public List<Contact> execute(Void i) {
                        MyMessengerService service = MyMessengerServiceFactory.getInstance();
                        List<Contact> results = service.getContacts();
                        return results;
                    }
                },
                new AsyncHelperCompleted<List<Contact>>() {
                    @Override
                    public void completed(AsyncHelperFunctionResult<List<Contact>> e) {
                        if (e.err != null) {
                            Log.e(LOGGING, "unable to get contacts", e.err);
                        }
                        ContactItemAdapter adapter = new ContactItemAdapter(ContactsMainActivity.this, e.result);
                        ContactsMainActivity.this.contactList.setAdapter(adapter);
                    }
                }).execute((Void)null);
    }

    protected void btnAddContactClick(View v) {
        AddContactActivity.startActivity(this);
    }

    /*private void setRefreshMessagesState(boolean done) {
        btnContactsRefreshMessages.setEnabled(done);
        btnAddNewContact.setEnabled(done);
    }

    protected void btnRefreshMessages(View view) {
        setRefreshMessagesState(false);

        AsyncHelper2.runAsync(
                new AsyncHelperFunction2<Void, Void>() {
                    @Override
                    public Void execute(Void i) throws Exception {
                        MyMessengerServiceFactory.getInstance().pullMessages();
                        return null;
                    }
                },
                new AsyncHelperCompleted<Void>() {
                    @Override
                    public void completed(AsyncHelperFunctionResult<Void> e) {
                        if (e.err != null) {
                            Log.e(LOGGING, "unable to get message", e.err);
                            Toast.makeText(getApplicationContext(), getString(R.string.contact_unable_to_pull_messages), Toast.LENGTH_LONG).show();
                        }
                        ContactsMainActivity.this.setRefreshMessagesState(true);
                        ContactsMainActivity.this.refreshContacts();
                    }
                },
                new AsyncHelperCancelled() {
                    @Override
                    public void cancelled() {
                        ContactsMainActivity.this.setRefreshMessagesState(true);
                    }
                }).execute((Void)null);
    }
*/
}
