using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;
using my.messenger.common.Messaging;
using RabbitMQ.Client;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;

namespace my.messenger.common.Messaging.Impl
{
    public class RabbitMQMessageReceiver : RabbitMQBaseMessage, IMessageReceiver
    {
        private ILogger<RabbitMQMessageReceiver> logger;

        private class RabbitMQSubscriber : ISubscriber
        {
            private IConnection conn;
            private IModel channel;
            private ILogger<RabbitMQMessageReceiver> logger;

            public RabbitMQSubscriber(IConnection conn, IModel channel, ILogger<RabbitMQMessageReceiver> logger)
            {
                this.conn = conn;
                this.channel = channel;
                this.logger = logger;
            }

            public void Close()
            {
                logger.LogInformation("closing rabbitmq connection");
                try
                {
                    channel.Close();
                    conn.Close();
                    logger.LogInformation("closed rabbitmq connection");
                }
                catch (Exception ex)
                {
                    logger.LogError(ex, "Error when closing");
                }
            }
        }

        private class CustomConsumer : DefaultBasicConsumer
        {
            private IMessageConsumer consumer;
            private ILogger<RabbitMQMessageReceiver> logger;

            public CustomConsumer(IModel channel, IMessageConsumer consumer, ILogger<RabbitMQMessageReceiver> logger)
                : base(channel)
            {
                this.consumer = consumer;
                this.logger = logger;
            }

            public override void HandleBasicDeliver(string consumerTag, ulong deliveryTag, bool redelivered, string exchange, string routingKey, IBasicProperties properties, byte[] body)
            {
                logger.LogDebug("got rabbitq message");
                if (!consumer.CanHandle())
                {
                    this.Model.BasicNack(deliveryTag, false, true);
                }
                else
                {
                    try
                    {
                        consumer.Handle(JsonUtil.FromBytes<Message>(body));
                        this.Model.BasicAck(deliveryTag, false);
                    }
                    catch (Exception ex)
                    {
                        logger.LogError(ex, "Unable to handle message");
                        this.Model.BasicNack(deliveryTag, false, true);
                    }
                }
            }
        }

        public RabbitMQMessageReceiver(
            ILogger<RabbitMQMessageReceiver> logger,
            IConfiguration configuration)
            : base(configuration)
        {
            this.logger = logger;
        }

        public ISubscriber Subscribe(string userId, IMessageConsumer consumer)
        {
            var conn = this.GetConnection();
            var channel = conn.CreateModel();
            var rabbitListener = new CustomConsumer(channel, consumer, this.logger);
            String queueName = getQueueName(new Destination(DestinationType.User, userId));
            channel.BasicConsume(queueName, false, rabbitListener);
            return new RabbitMQSubscriber(conn, channel, this.logger);
        }

        public Task<List<Message>> Dequeue(string userId, int count)
        {
            return Task.Run(() => {
                BasicGetResult queueResult;
                var result = new List<Message>();
                using (var conn = this.GetConnection())
                {
                    using (var channel = conn.CreateModel())
                    {
                        String queueName = getQueueName(new Destination(DestinationType.User, userId));
                        do
                        {
                            count--;
                            queueResult = channel.BasicGet(queueName, true);
                            if (queueResult != null)
                            {
                                result.Add(JsonUtil.FromBytes<Message>(queueResult.Body));
                            }

                        } while (count > 0 && queueResult != null);
                        return result;
                    }
                }
            });
        }
    }
}
