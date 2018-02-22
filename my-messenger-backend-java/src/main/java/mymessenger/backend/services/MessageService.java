/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymessenger.backend.services;

import mymessenger.backend.messaging.Subscriber;
import java.util.logging.Level;
import java.util.logging.Logger;
import mymessenger.backend.messaging.MessageConsumer;
import mymessenger.backend.messaging.MessageSender;
import mymessenger.backend.messaging.MessageSenderException;
import mymessenger.backend.model.messaging.DestinationType;
import mymessenger.backend.model.messaging.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import mymessenger.backend.messaging.MessageReceiver;

/**
 *
 * @author guilherme
 */
@Service
public class MessageService {

    private final MessageSender messageSender;
    private final UserRepository userRepository;
    private final MessageReceiver messageReceiver;

    public MessageService(
            @Autowired MessageSender messageSender,
            @Autowired UserRepository userRepository,
            @Autowired MessageReceiver listenerService) {
        this.messageSender = messageSender;
        this.userRepository = userRepository;
        this.messageReceiver = listenerService;
    }

    public Subscriber subscribe(String userId, MessageConsumer consumer) throws MessageServiceException {
        try {
            return messageReceiver.subscribe(userId, consumer);
        } catch (MessageSenderException ex) {
            throw new MessageServiceException("Unable to listen", ex);
        }
    }

    public void send(Message message) throws MessageServiceException {

        if (!StringUtils.hasText(message.getFromUserId())) {
            throw new MessageServiceException("Invalid source");
        }

        if (message.getTo() == null || !StringUtils.hasText(message.getTo().getId())) {
            throw new MessageServiceException("Invalid source");
        }

        if (!StringUtils.hasText(message.getBody())) {
            throw new MessageServiceException("Invalid body");
        }

        if (message.getType() == null) {
            throw new MessageServiceException("Invalid Type");
        }

        if (message.getTo().getType() == DestinationType.User) {
            if (!userRepository.findById(message.getTo().getId()).isPresent()) {
                throw new MessageServiceException("Destination User Not Found");
            }
        }

        try {
            messageSender.send(message);
        } catch (MessageSenderException ex) {
            throw new MessageServiceException(ex);
        }
    }
}
