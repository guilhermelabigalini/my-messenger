/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymessenger.backend.messaging.impl;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import mymessenger.backend.messaging.MessageSenderException;
import mymessenger.backend.model.messaging.Destination;

/**
 *
 * @author guilherme
 */
public class RabbitMQBaseMessage {

    protected static class RabbitChannel {

        public final Connection connection;
        public final Channel channel;

        public RabbitChannel(Connection connection, Channel channel) {
            this.connection = connection;
            this.channel = channel;
        }
    }

    @FunctionalInterface
    public static interface ConsumerException<T> {

        void accept(T t) throws Exception;

    }

    protected Channel getChannel() throws MessageSenderException {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            Connection conn = factory.newConnection();
            Channel channel = conn.createChannel();
            return channel;
        } catch (TimeoutException | IOException ex) {
            throw new MessageSenderException(ex);
        }
    }

    protected void useRabbitAndDispose(ConsumerException<RabbitChannel> consumer) throws MessageSenderException {
        ConnectionFactory factory = new ConnectionFactory();
        try (Connection conn = factory.newConnection()) {
            try (Channel channel = conn.createChannel()) {
                consumer.accept(new RabbitChannel(conn, channel));
            }
        } catch (Exception ex) {
            throw new MessageSenderException(ex);
        }
    }

    protected String getQueueName(Destination d) {
        return "q." + d.getType() + "." + d.getId();
    }

    protected String getExchangeName(Destination d) {
        return "ex." + d.getType() + "." + d.getId();
    }

}
