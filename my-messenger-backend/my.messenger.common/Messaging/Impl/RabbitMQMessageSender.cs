using Microsoft.Extensions.Configuration;
using System;
using System.Threading.Tasks;

namespace my.messenger.common.Messaging.Impl
{
    public class RabbitMQMessageSender : RabbitMQBaseMessage, IMessageSender
    {
        public RabbitMQMessageSender(
           IConfiguration configuration)
           : base(configuration)
        {
        }

        public Task RegisterDestinationAsync(Destination d)
        {
            return Task.Run(() => useRabbitAndDispose((conn, channel) => {
                String exchangeName = getExchangeName(d);
                channel.ExchangeDeclare(exchangeName, "fanout", true, false, null);

                if (d.Type == DestinationType.User)
                {
                    String queueName = getQueueName(d);

                    channel.QueueDeclare(queueName, true, false, false, null);
                    channel.QueueBind(queueName, exchangeName, "", null);
                }
            }));
        }

        public Task RegisterDestinationListenerAsync(Destination d, Destination destinationListener)
        {
            return Task.Run(() => useRabbitAndDispose((conn, channel) => {
                String exchangeName = getExchangeName(d);

                if (destinationListener.Type == DestinationType.User)
                {
                    String queueName = getQueueName(destinationListener);
                    channel.QueueBind(queueName, exchangeName, "", null);
                }
            }));
        }

        public Task RemoveDestinationListenerAsync(Destination d, Destination destinationListener)
        {
            return Task.Run(() => useRabbitAndDispose((conn, channel) => {
                String exchangeName = getExchangeName(d);

                if (destinationListener.Type == DestinationType.User)
                {
                    String queueName = getQueueName(destinationListener);
                    channel.QueueUnbind(queueName, exchangeName, "", null);
                }
            }));
        }

        public Task SendAync(Message msg)
        {
            return Task.Run(() => useRabbitAndDispose((conn, channel) => {
                String exchangeName = getExchangeName(msg.To);

                byte[] messageBodyBytes = JsonUtil.ToBytes(msg);

                var properties = channel.CreateBasicProperties();
                properties.Persistent = true;

                channel.BasicPublish(
                        exchangeName, 
                        "",
                        false,
                        properties,
                        messageBodyBytes);
            }));
        }
    }
}
