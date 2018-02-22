package my.messenger.androidclient.services;

import android.os.Build;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import my.messenger.androidclient.api.InvalidMyMessengerRequest;
import my.messenger.androidclient.api.MyMessengerRestClient;
import my.messenger.androidclient.api.model.Message;
import my.messenger.androidclient.api.model.StreamMessage;
import my.messenger.androidclient.api.model.StreamMessageType;
import my.messenger.androidclient.api.model.StreamMessageResponse;
import my.messenger.androidclient.db.UserSessionDB;

class MyMessengerMessageMonitor {
    private static final String LOGGING = "MessageMonitor";

    private Thread receivedThread;
    private MyMessengerRestClient restClient;
    private UserSessionDB userSession;
    private MyMessengerMessageMonitorListener listener;
    private MyMessengerWebSocketClient wsClient;

    static {
        if ("google_sdk".equals( Build.PRODUCT )) {
            // ... disable IPv6 due emulator issues
            java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
            java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
        }
    }

    public MyMessengerMessageMonitor(MyMessengerRestClient restClient,
                                     UserSessionDB userSession,
                                     MyMessengerMessageMonitorListener listener) {
        this.restClient = restClient;
        this.userSession = userSession;
        this.listener = listener;
    }

    public void start() {
        this.receivedThread = new Thread(new MyMessenserMessageReceiver());
        this.receivedThread.start();
    }

    public void stop() {
        if (this.receivedThread != null) {
            this.receivedThread.interrupt();
            this.receivedThread = null;
        }
    }


    private Message[] getRecentMessages() {
        try {
            Message[] messages = restClient.getMessages(5, userSession.id);
            return messages;
        } catch (InvalidMyMessengerRequest e) {
            Log.e(LOGGING, "error while pulling", e);
            return null;
        }
    }

    private boolean pullMessages()  {
        Message[] messages = getRecentMessages();

        boolean result = messages != null && messages.length > 0;

        while (messages != null && messages.length > 0) {
            for (Message m : messages) {
                listener.handleReceivedMessage(m);
            }
            messages = getRecentMessages();
        }

        return result;
    }

    private void tryToCloseWS() {
        if (wsClient != null) {
            wsClient.close();
            wsClient = null;
        }
    }

    private class MyMessenserMessageReceiver implements Runnable {

        private static final int StateStarting = 0;
        private static final int StateTryingWS = 1;
        private static final int StateUsingWS = 2;
        private static final int StateUsingPooling = 3;

        // give about 30s to connect to WS, otherwise move to pulling
        private static final long TimeoutToAbortWSinMS = 30000;

        // time between connection quality check (5 minutes)
        private static final long TimeToCheckConnectionQuality = 60000 * 5;

        private static final String BaseWSAPIUrl = "wss://my-messenger-backend.azurewebsites.net/ws/messaging";

        private long startedTryingWS;
        private long lastConnectionCheck;
        private int state;
        private URI uri;

        /*
        https://github.com/TooTallNate/Java-WebSocket
         */

        @Override
        public void run() {

            try {
                uri = new URI(BaseWSAPIUrl);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }

            startedTryingWS = Long.MAX_VALUE;
            lastConnectionCheck = System.currentTimeMillis();

            if (! Connectivity.isConnectedFast()) {
                Log.d(LOGGING, "isGoodConnection = false");
                state = StateUsingPooling;
            } else {
                state = StateStarting;
                Log.d(LOGGING, "isGoodConnection = true");
            }

            while (! Thread.interrupted()) {
                try {
                    // Delay 2 seconds between each cycle

                    Thread.sleep(2000);

                    processState();

                } catch (InterruptedException e) {
                    return;
                } catch (Exception e) {
                    Log.e(LOGGING, "unable to process state", e);
                }
            }
        }

        private void processState() {
            switch (state) {
                case StateStarting:
                    Log.d(LOGGING, "state = StateStarting");

                    startedTryingWS = System.currentTimeMillis();
                    wsClient = new MyMessengerWebSocketClient(uri);
                    wsClient.connect();
                    state = StateTryingWS;
                    break;

                case StateTryingWS:
                    Log.d(LOGGING, "state = StateTryingWS");

                    if (wsClient.isOpen() && wsClient.isLoggedIn()) {
                        Log.d(LOGGING, "WS is live and logged, changed state to StateUsingWS");
                        state = StateUsingWS;
                    }
                    else {
                        // trying to use WS for less than "TimeoutToAbortWSinMS"
                        long tryingWSForInMS = System.currentTimeMillis() - startedTryingWS;
                        if (tryingWSForInMS < TimeoutToAbortWSinMS) {
                            Log.d(LOGGING, "Trying WS for " + tryingWSForInMS + " ms, will give up in " + TimeoutToAbortWSinMS);
                        } else {
                            Log.d(LOGGING, "Aborting attempt to use WS after " + tryingWSForInMS + " ms, , changed state to StateUsingPooling");
                            tryToCloseWS();

                            // Trying to use WS for a long-time, move to pooling!
                            state = StateUsingPooling;

                            // reset connection timer check, we want to check connection again soon,
                            // just got an connection error !
                            lastConnectionCheck = System.currentTimeMillis();
                        }
                    }
                    break;

                case StateUsingWS:
                    Log.d(LOGGING, "state = StateUsingWS");

                    // lost the connection?
                    if (!wsClient.isOpen()) {
                        Log.d(LOGGING, "WS is not open, changed state to StateUsingPooling");

                        tryToCloseWS();

                        state = StateUsingPooling;
                        lastConnectionCheck = System.currentTimeMillis();
                    }
                    break;

                case StateUsingPooling:
                    Log.d(LOGGING, "state = StateUsingPooling");

                    pullMessages();

                    long lastConnectionQualityCheckInMS = System.currentTimeMillis() - lastConnectionCheck;

                    if (lastConnectionQualityCheckInMS > TimeToCheckConnectionQuality) {
                        Log.d(LOGGING, "Last connection quality over " +
                                TimeToCheckConnectionQuality + " ms, checking connection quality again");

                        lastConnectionCheck = System.currentTimeMillis();

                        // using a fast connection, try to switch to WS
                        if (Connectivity.isConnectedFast()) {
                            state = StateStarting;
                            Log.d(LOGGING, "Connection is fast, changed state to StateStarting");
                        } else {
                            Log.d(LOGGING, "Connection is slow, keeping with state = StateUsingPooling");
                        }
                    } else {
                        Log.d(LOGGING, "Last connection quality check was in " +
                                lastConnectionQualityCheckInMS + " ms, keeping with state = StateUsingPooling");
                    }

                    break;
            }
        }
    }

    private class MyMessengerWebSocketClient extends WebSocketClient {

        private boolean sentLoginRequest;
        private boolean isLoggedIn;

        public MyMessengerWebSocketClient(URI serverUri) {
            super(serverUri);
            this.sentLoginRequest = false;
            this.isLoggedIn = false;
        }

        public boolean isLoggedIn() {
            return isLoggedIn;
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            // send the login info!
            StreamMessage obj = new StreamMessage();
            obj.setTokenId(userSession.id);
            obj.setStreamMessageType(StreamMessageType.Singin);

            try {
                this.send(JSON.stringify(obj));
                this.sentLoginRequest = true;
            } catch (JsonProcessingException e) {
                Log.e(LOGGING, "unable serialize onOpen", e);
                this.close();
            }
        }

        @Override
        public void onMessage(String message) {
            try {
                StreamMessageResponse response  = JSON.parse(message, StreamMessageResponse.class);

                if (response.isOk() && this.sentLoginRequest) {
                    isLoggedIn = true;
                }

                if (response.getMessage() != null) {
                    listener.handleReceivedMessage(response.getMessage());
                }

            } catch (IOException e) {
                Log.e(LOGGING, "unable parse onMessage", e);
                this.close();
            }
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            this.isLoggedIn = false;
            this.sentLoginRequest = false;
        }

        @Override
        public void onError(Exception ex) {
            Log.e(LOGGING, "onMessage", ex);
        }
    }
}
