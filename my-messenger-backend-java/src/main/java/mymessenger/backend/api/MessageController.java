/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymessenger.backend.api;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import javax.servlet.ServletRequest;
import mymessenger.backend.model.messaging.Message;
import mymessenger.backend.model.messaging.TransmittedMessage;
import mymessenger.backend.services.MessageService;
import mymessenger.backend.services.MessageServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author guilherme
 */
@RestController
@RequestMapping("api/message")
public class MessageController {
    
    @Autowired
    private MessageService messageService;
        
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> send(@RequestBody TransmittedMessage message, ServletRequest req) throws MessageServiceException
    {
        Message msg = new Message();
        msg.setBody(message.getBody());
        msg.setTo(message.getTo());
        msg.setType(message.getType());
        msg.setSentAt(LocalDateTime.now(ZoneOffset.UTC));
        msg.setFromUserId(SecurityContext.get(req).getUserId());
        
        messageService.send(msg);
        
        return ResponseEntity
            .ok()
            .build();
    }    
}
