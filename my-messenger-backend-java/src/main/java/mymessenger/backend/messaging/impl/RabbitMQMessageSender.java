/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymessenger.backend.messaging.impl;

import com.rabbitmq.client.AMQP;
import mymessenger.backend.encoding.JsonUtil;
import mymessenger.backend.messaging.MessageSender;
import mymessenger.backend.model.messaging.Destination;
import mymessenger.backend.model.messaging.Message;

import mymessenger.backend.messaging.MessageSenderException;
import org.springframework.stereotype.Service;

/**
 *
 * @author guilherme
 */
@Service
public class RabbitMQMessageSender extends RabbitMQBaseMessage implements MessageSender {

    @Override
    public void registerDestination(Destination d) throws MessageSenderException {
        useRabbitAndDispose(rabbitMq -> {
            String exchangeName = getExchangeName(d);
            String queueName = getQueueName(d);
            rabbitMq.channel.exchangeDeclare(exchangeName, "fanout", true);
            /*
                    String queue,
                    boolean durable,
                    boolean exclusive,
                    boolean autoDelete,
                    Map<String,Object> arguments
             */
            rabbitMq.channel.queueDeclare(queueName, true, false, false, null);

            rabbitMq.channel.queueBind(queueName, exchangeName, "");
        });
    }

    @Override
    public void send(Message msg) throws MessageSenderException {
        useRabbitAndDispose(rabbitMq -> {
            String exchangeName = getExchangeName(msg.getTo());

            byte[] messageBodyBytes = JsonUtil.toBytes(msg);

            rabbitMq.channel.basicPublish(exchangeName, "",
                    new AMQP.BasicProperties.Builder()
                            .deliveryMode(2) // persistent
                            .build(),
                    messageBodyBytes);
        });
    }

}
