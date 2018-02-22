using my.messenger.common.Users;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;

namespace my.messenger.common.Messaging
{
    public class MessageService
    {
        private IUserRepository userRepository;
        private IMessageSender messageSender;
        private IMessageReceiver messageReceiver;

        public MessageService(IUserRepository userRepository, IMessageSender messageSender, IMessageReceiver messageReceiver)
        {
            this.userRepository = userRepository;
            this.messageSender = messageSender;
            this.messageReceiver = messageReceiver;
        }

        public ISubscriber Subscribe(String userId, IMessageConsumer consumer)
        {
            return messageReceiver.Subscribe(userId, consumer);
        }

        public Task<List<Message>> Dequeue(string userId, int count)
        {
            return messageReceiver.Dequeue(userId, count);
        }

        public async Task SendAsync(Message message)
        {
            if (!StringUtils.hasText(message.FromUserId))
            {
                throw new MessageServiceException("Invalid source");
            }

            if (message.To == null || !StringUtils.hasText(message.To.Id))
            {
                throw new MessageServiceException("Invalid source");
            }

            if (String.Equals(message.To.Id, message.FromUserId, StringComparison.InvariantCultureIgnoreCase))
            {
                throw new MessageServiceException("source equals destination");
            }

            if (!StringUtils.hasText(message.Body))
            {
                throw new MessageServiceException("Invalid body");
            }

            if (message.To.Type == DestinationType.User)
            {
                if (await userRepository.FindByIdAsync(message.To.Id) == null)
                {
                    throw new MessageServiceException("Destination User Not Found");
                }
            }

            await messageSender.SendAync(message);
        }
    }
}
