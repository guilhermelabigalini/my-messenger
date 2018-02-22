package my.messenger.androidclient.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import my.messenger.androidclient.R;
import my.messenger.androidclient.db.ChatMessageDB;
import my.messenger.androidclient.db.DestinationTypeDB;
import my.messenger.androidclient.services.Contact;
import my.messenger.androidclient.services.DateService;
import my.messenger.androidclient.services.MyMessengerMessageListener;
import my.messenger.androidclient.services.MyMessengerService;
import my.messenger.androidclient.services.MyMessengerServiceException;
import my.messenger.androidclient.services.MyMessengerServiceFactory;
import my.messenger.androidclient.ui.async.AsyncHelper;
import my.messenger.androidclient.ui.async.AsyncHelperCancelled;
import my.messenger.androidclient.ui.async.AsyncHelperCompleted;
import my.messenger.androidclient.ui.async.AsyncHelperFunction;
import my.messenger.androidclient.ui.async.AsyncHelperFunctionResult;

public class ChatActivity extends AppCompatActivity implements MyMessengerMessageListener {
    private static final String LOGGING = "ChatActivity";

    private final static String PARAM_ContactUserID = "PARAM_ContactUserID";
    private final static int FROM_ME_MSG_COLOR = Color.parseColor("#d8ead3");
    private final static int TO_ME_MSG_COLOR = Color.parseColor("#e3e5e8");
    private Contact contact;

    private class ChatMessageItemAdapter extends ArrayAdapter<ChatMessageDB> {

        private final LayoutInflater inflater;

        public ChatMessageItemAdapter(Context context, List<ChatMessageDB> values) {
            super(context, -1, values);
            this.inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ChatMessageDB msg = this.getItem(position);
            View rowView;
            TextView lblContact;
            TextView lblMessage;
            TextView lblTime;
            String contactTxt;

            MyMessengerService myMsg = MyMessengerServiceFactory.getInstance();

            if (msg.sent) {
                rowView = inflater.inflate(R.layout.activity_chat_message_sent, parent, false);
                lblContact = (TextView) rowView.findViewById(R.id.activity_chat_message_sent_lblContact);
                lblMessage = (TextView) rowView.findViewById(R.id.activity_chat_message_sent_lblMessage);
                lblTime = (TextView) rowView.findViewById(R.id.activity_chat_message_sent_lblTime);

                contactTxt = myMsg.getLoggedUserName();
            } else {
                rowView = inflater.inflate(R.layout.activity_chat_message_received, parent, false);
                lblContact = (TextView) rowView.findViewById(R.id.activity_chat_message_received_lblContact);
                lblMessage = (TextView) rowView.findViewById(R.id.activity_chat_message_received_lblMessage);
                lblTime = (TextView) rowView.findViewById(R.id.activity_chat_message_received_lblTime);

                Contact c = myMsg.getContact(msg.fromUserId);
                if (c != null)
                    contactTxt = c.getUserProfile().getUsername();
                else
                    contactTxt = msg.fromUserId;
            }

            lblMessage.setText(msg.body);
            lblContact.setText(contactTxt);
            lblTime.setText(DateService.toShortDateTimeString(msg.messageDt));

            return rowView;
        }
    }

    private String userId;
    private ListView lstChatHistory;
    private Button btnSendMessage;
    private EditText txtMessageContent;
    private ProgressBar contact_chat_progressBar_LoadingChat;
    private ChatMessageItemAdapter adapter;

    @Override
    public void onSentOrReceivedMessage(final ChatMessageDB m) {
        if (this.adapter != null && m.connectionId.equalsIgnoreCase(this.userId)) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.add(m);
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        final MyMessengerService messenger = MyMessengerServiceFactory.getInstance();
        final String up = this.userId;
        messenger.unRegisterMessageListener(this);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    messenger.markAsRead(up);
                } catch (MyMessengerServiceException e) {
                    Log.e(LOGGING, "unable to mark as read", e);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        MyMessengerServiceFactory.getInstance().registerMessageListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = this.getIntent();
        this.userId = intent.getStringExtra(PARAM_ContactUserID);

        this.lstChatHistory = findViewById(R.id.contact_chat_lstChatHistory);
        this.btnSendMessage = findViewById(R.id.contact_chat_btnSendMessage);
        this.txtMessageContent = findViewById(R.id.contact_chat_txtMessageContent);
        this.contact_chat_progressBar_LoadingChat = findViewById(R.id.contact_chat_progressBar_LoadingChat);
        this.lstChatHistory.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                ChatMessageDB msgDb = ChatActivity.this.adapter.getItem(position);
                handleLongClickOnMessage(msgDb);
                return true;
            }
        });

        this.contact = MyMessengerServiceFactory.getInstance().getContact(this.userId);
        this.setTitle(contact.getUserProfile().getUsername());

        loadMessages();
    }

    private void handleLongClickOnMessage(final ChatMessageDB msgDb) {
        final CharSequence[] items = {
                getString(R.string.activity_chat_msg_menu_copy_message)
        };

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.activity_chat_msg_menu_title))
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                // read as read
                                ClipboardHelper.copyText(msgDb.body, ChatActivity.this);
                                break;
                        }
                    }
                })
                .create()
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_chat_menu, menu);

        if (contact.getType() == DestinationTypeDB.Group) {
            // hide the option to view profile
            menu.findItem(R.id.activity_chat_menu_view_profile)
                    .setVisible(false);
        } else {
            // hide option to display group members
            menu.findItem(R.id.activity_chat_menu_view_members)
                    .setVisible(false);

            // hide option to leave group
            menu.findItem(R.id.activity_chat_menu_leave_group)
                    .setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.activity_chat_menu_view_members:
                handleViewGroupViews();
                return true;
            case R.id.activity_chat_menu_leave_group:
                handleLeaveGroup();
                return true;
            case R.id.activity_chat_menu_load_old_messages:
                handleLoadOldMessages();
                return true;
            case R.id.activity_chat_menu_clear_chat:
                handleClearChatRequest();
                return true;
            case R.id.activity_chat_menu_view_profile:
                ViewProfileActivity.startActivity(this, this.userId);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void handleViewGroupViews() {
        GroupDetailsActivity.startActivity(this, this.userId);
    }

    public void handleLeaveGroup() {
        AsyncHelper.runAsync(
                new AsyncHelperFunction<String, Boolean>() {
                    @Override
                    public Boolean execute(String groupId) throws Exception  {
                        MyMessengerServiceFactory.getInstance().leaveGroup(groupId);
                        return true;
                    }
                },
                new AsyncHelperCompleted<Boolean>()  {
                    @Override
                    public void completed(AsyncHelperFunctionResult<Boolean> result) {
                        if (result.err != null) {
                            Log.e(LOGGING, "unable to leave group", result.err);
                        } else {
                            // close the chat and return to contact list
                            ChatActivity.this.finish();
                        };
                    }
                }).execute(this.userId);
    }

    private void handleLoadOldMessages() {
        if (adapter.getCount() <= 0)
            return;

        final long topMessageId = adapter.getItem(0).id;

        AsyncHelper.runAsync(
                new AsyncHelperFunction<String, List<ChatMessageDB>>() {
                    @Override
                    public List<ChatMessageDB> execute(String userId) throws Exception  {
                        return MyMessengerServiceFactory.getInstance().getRecentMessagesOldThan(userId, topMessageId);
                    }
                },
                new AsyncHelperCompleted<List<ChatMessageDB>>()  {
                    @Override
                    public void completed(AsyncHelperFunctionResult<List<ChatMessageDB>> result) {
                        if (result.err != null) {
                            Log.e(LOGGING, "unable to load old messages", result.err);
                        } else {
                            for (int i = result.result.size() - 1; i >= 0; i--) {
                                adapter.insert(result.result.get(i), 0);
                            }
                        };
                    }
                }).execute(this.userId);
    }

    private void handleClearChatRequest() {
        AsyncHelper.runAsync(
                new AsyncHelperFunction<String, Boolean>() {
                    @Override
                    public Boolean execute(String userId) throws Exception  {
                        MyMessengerServiceFactory.getInstance().removeHistory(userId);
                        return true;
                    }
                },
                new AsyncHelperCompleted<Boolean>()  {
                    @Override
                    public void completed(AsyncHelperFunctionResult<Boolean> result) {
                        if (result.err != null) {
                            Log.e(LOGGING, "unable to clear messages", result.err);
                            Toast.makeText(getApplicationContext(), getString(R.string.contact_chat_unable_to_leave_group), Toast.LENGTH_LONG).show();
                        }
                        ChatActivity.this.loadMessages();
                    }
                }).execute(this.userId);
    }

    private void setMessageList(List<ChatMessageDB> list) {
        this.adapter = new ChatMessageItemAdapter(ChatActivity.this, list);
        this.lstChatHistory.setAdapter(adapter);
    }

    private void loadMessages() {

        contact_chat_progressBar_LoadingChat.setVisibility(View.VISIBLE);

        AsyncHelper.runAsync(
                new AsyncHelperFunction<String, List<ChatMessageDB>>() {
                    @Override
                    public List<ChatMessageDB> execute(String userId) throws Exception  {
                        return MyMessengerServiceFactory.getInstance().getRecentMessages(userId);
                    }
                },
                new AsyncHelperCompleted<List<ChatMessageDB>>()  {
                    @Override
                    public void completed(AsyncHelperFunctionResult<List<ChatMessageDB>> result) {
                        ChatActivity.this.contact_chat_progressBar_LoadingChat.setVisibility(View.GONE);
                        if (result.err == null) {
                            setMessageList(result.result);
                        } else {
                            Log.e(LOGGING, "unable to get messages", result.err);
                        }
                    }
                },
                new AsyncHelperCancelled(){
                    @Override
                    public void cancelled() {
                        ChatActivity.this.contact_chat_progressBar_LoadingChat.setVisibility(View.GONE);
                    }
                }).execute(this.userId);
    }
/*
    private void fillTestData() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, -12);

        String myId = MyMessengerServiceFactory.getInstance().getLoggedUserId();
        List<ChatMessageDB> sampleMsg = new ArrayList<>();
        for(int i = 0; i < 40; i++) {
            cal.add(Calendar.DATE, 1);
            cal.add(Calendar.HOUR, i);
            cal.add(Calendar.MINUTE, i*3);
            cal.add(Calendar.SECOND, i*7);

            ChatMessageDB m = new ChatMessageDB();
            m.body = ("this a message text " + i + " aush auhsuhasuhauhs huah suahus huahsuaushauhs uha suhasdhasod a");
            m.userId = (myId);
            m.userName = (this.contact.getUsername());
            m.sent = (i % 2 == 0);
            m.messageDt = (cal.getTime());
            sampleMsg.add(m);
        }

        ChatMessageItemAdapter adapter = new ChatMessageItemAdapter(this, sampleMsg);
        this.lstChatHistory.setAdapter(adapter);
    }*/

    private void disableEnableMsgSend(boolean v) {
        txtMessageContent.setEnabled(v);
        btnSendMessage.setEnabled(v);
        if (v)
            txtMessageContent.setText("");
    }

    protected void btnSendMessageClick(View v) {
        final String msgToSend = txtMessageContent.getText().toString().trim();

        if (TextUtils.isEmpty(msgToSend)) {
            return;
        }

        ChatActivity.this.disableEnableMsgSend(false);

        AsyncHelper.runAsync(
                new AsyncHelperFunction<String, ChatMessageDB>() {
                    @Override
                    public ChatMessageDB execute(String i) throws Exception {
                        return MyMessengerServiceFactory.getInstance().sendMessage(userId, msgToSend);
                    }
                },
                new AsyncHelperCompleted<ChatMessageDB>() {
                    @Override
                    public void completed(AsyncHelperFunctionResult<ChatMessageDB> i) {
                        ChatActivity.this.disableEnableMsgSend(true);

                        if (i.err != null) {
                            Log.e(LOGGING, "unable to send message", i.err);
                            Toast.makeText(getApplicationContext(), getString(R.string.contact_chat_unable_to_send_msg), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new AsyncHelperCancelled() {
                    @Override
                    public void cancelled() {
                        ChatActivity.this.disableEnableMsgSend(true);
                    }
                }).execute(msgToSend);
    }

    public static void startActivity(Activity parent, String userId) {
        Intent intent = new Intent(parent, ChatActivity.class);
        intent.putExtra(PARAM_ContactUserID, userId);
        parent.startActivity(intent);
    }
}
