/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymessenger.backend.messaging.impl;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import mymessenger.backend.encoding.JsonUtil;
import mymessenger.backend.messaging.MessageConsumer;
import mymessenger.backend.messaging.MessageSenderException;
import mymessenger.backend.model.messaging.Destination;
import mymessenger.backend.model.messaging.DestinationType;
import mymessenger.backend.model.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mymessenger.backend.messaging.MessageReceiver;
import mymessenger.backend.messaging.Subscriber;
import org.springframework.stereotype.Service;

/**
 *
 * @author guilherme
 */
@Service
public class RabbitMQMessageListener extends RabbitMQBaseMessage implements MessageReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(RabbitMQMessageListener.class);

    @Override
    public Subscriber subscribe(String userId, MessageConsumer consumer) throws MessageSenderException {
        try {
            Channel channel = getChannel();
            Consumer rabbitListener = new CustomConsumer(channel, consumer);
            String queueName = getQueueName(new Destination(DestinationType.User, userId));
            channel.basicConsume(queueName, false, rabbitListener);
            return new CustomSubscriber(channel);
        } catch (IOException ex) {
            throw new MessageSenderException("unable to subscribe", ex);
        }
    }

    private static class CustomSubscriber implements Subscriber {

        private final Channel channel;

        public CustomSubscriber(Channel channel) {
            this.channel = channel;
        }

        @Override
        public void close() {
            LOG.info("closing rabbitmq connection");
            Connection conn = channel.getConnection();
            try {
                channel.close();
                conn.close();
                LOG.info("closed rabbitmq connection");
            } catch (IOException | TimeoutException ex) {
                LOG.error("Error when closing", ex);
            }
        }

    }

    private static class CustomConsumer extends DefaultConsumer {

        private final MessageConsumer consumer;

        public CustomConsumer(Channel channel, MessageConsumer consumer) {
            super(channel);
            this.consumer = consumer;
        }

        @Override
        public void handleDelivery(
                String consumerTag,
                Envelope envelope,
                AMQP.BasicProperties properties, byte[] body) throws IOException {

            LOG.debug("got rabbitq message");
            
            if (!consumer.canHandle()) {
                this.getChannel().basicNack(envelope.getDeliveryTag(), false, true);
            } else {
                try {
                    consumer.handle(JsonUtil.decode(body, Message.class));
                    
                    this.getChannel().basicAck(envelope.getDeliveryTag(), false);
                } catch (Exception ex) {
                    LOG.error("Unable to handle message", ex);
                    this.getChannel().basicNack(envelope.getDeliveryTag(), false, true);
                }
            }
        }
    }
}
