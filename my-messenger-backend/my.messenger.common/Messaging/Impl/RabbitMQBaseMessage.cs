using Microsoft.Extensions.Configuration;
using RabbitMQ.Client;
using System;
using System.Collections.Generic;
using System.Text;

namespace my.messenger.common.Messaging.Impl
{
    public class RabbitMQBaseMessage
    {
        private IConfiguration configuration;
        private string userName;
        private string password;
        private string hostName;

        public RabbitMQBaseMessage(IConfiguration configuration)
        {
            this.configuration = configuration;

            var section = configuration.GetSection("RabbitMQ");

            this.userName = section["User"];
            this.password = section["Password"];
            this.hostName = section["Hostname"];
        }

        protected ConnectionFactory GetFactory()
        {
            ConnectionFactory factory = new ConnectionFactory();
            factory.UserName = this.userName;
            factory.Password = this.password;
            factory.HostName = this.hostName;
            return factory;
        }

        protected IConnection GetConnection()
        {
            ConnectionFactory factory = GetFactory();
            return factory.CreateConnection();
        }

        protected void useRabbitAndDispose(Action<IConnection, IModel> consumer)
        {
            using (IConnection conn = GetConnection())
            {
                using (IModel channel = conn.CreateModel())
                {
                    consumer(conn, channel);
                }
            }
        }
    

        protected String getQueueName(Destination d)
        {
            return "q." + d.Type + "." + d.Id;
        }

        protected String getExchangeName(Destination d)
        {
            return "ex." + d.Type + "." + d.Id;
        }
    }
}
