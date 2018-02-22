using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;

namespace my.messenger.common.Messaging
{
    public interface IMessageSender
    {
        Task SendAync(Message msg);

        Task RegisterDestinationAsync(Destination d);

        Task RegisterDestinationListenerAsync(Destination d, Destination destinationListener);

        Task RemoveDestinationListenerAsync(Destination d, Destination destinationListener);
    }
}
