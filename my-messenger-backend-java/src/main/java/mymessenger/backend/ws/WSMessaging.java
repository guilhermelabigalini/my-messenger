package mymessenger.backend.ws;

import java.util.Optional;
import mymessenger.backend.encoding.JsonUtil;
import mymessenger.backend.messaging.Subscriber;
import mymessenger.backend.model.users.UserSession;
import mymessenger.backend.services.MessageService;
import mymessenger.backend.services.UserService;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class WSMessaging extends TextWebSocketHandler {

    private static final String USERSESSION = "user-session";
    private static final String USERSUBSCRIBE = "user-SUBSCRIBE";
    private static final Logger LOG = LoggerFactory.getLogger(WSMessaging.class);

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        LOG.info("afterConnectionClosed");

        Object sub = session.getAttributes().get(USERSUBSCRIBE);

        if (sub != null && sub instanceof Subscriber) {
            LOG.info("closing subscriber");
            ((Subscriber) sub).close();
        } else {
            LOG.info("subscriber not found");
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        // http://stackoverflow.com/questions/13592236/parse-a-uri-string-into-name-value-collection
        // UriComponentsBuilder.fromUri(session.getUri()).build().getQueryParams().getFirst(key);
        LOG.info("afterConnectionEstablished");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        LOG.info("handleTextMessage: " + message.toString());

        UserSession usv;
        try {
            StreamMessage strmMsg = JsonUtil.decode(message.getPayload(), StreamMessage.class);

            switch (strmMsg.getStreamMessageType()) {
                case Singin:

                    String token = strmMsg.getTokenId();
                    Optional<UserSession> us = userService.getSession(token);

                    if (us.isPresent()) {
                        usv = us.get();
                        LOG.info("registering message listener for " + usv);

                        Subscriber sub = messageService.subscribe(usv.getUserId(), new WSMessageConsumer(session));
                        session.getAttributes().put(USERSESSION, usv);
                        session.getAttributes().put(USERSUBSCRIBE, sub);     

                        session.sendMessage(new TextMessage(JsonUtil.toString(StreamMessageResponse.ok())));

                    } else {
                        LOG.warn("unable to find a valid session with id " + token);
                        session.close(CloseStatus.PROTOCOL_ERROR);
                    }

                    break;
                case Ping:
                    LOG.info("Get PING");
                    session.sendMessage(new TextMessage(JsonUtil.toString(StreamMessageResponse.ok())));
                    break;
                case Info:
                    LOG.info("Get DEBUG");
                    if (session.getAttributes().containsKey(USERSESSION)) {
                        usv = (UserSession) session.getAttributes().get(USERSESSION);
                        session.sendMessage(new TextMessage(JsonUtil.toString(StreamMessageResponse.userSession(usv))));
                    } else {
                        session.sendMessage(new TextMessage(JsonUtil.toString(StreamMessageResponse.ok())));
                    }
                    break;
                case Message:
                    break;
            }
        } catch (Exception ex) {
            session.close();
            LOG.error(ex.toString());
        }
    }

}
