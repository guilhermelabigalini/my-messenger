using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;

namespace my.messenger.common.Messaging
{
    public interface IMessageReceiver
    {
        ISubscriber Subscribe(String userId, IMessageConsumer consumer);

        Task<List<Message>> Dequeue(string userId, int count);
    }
}
