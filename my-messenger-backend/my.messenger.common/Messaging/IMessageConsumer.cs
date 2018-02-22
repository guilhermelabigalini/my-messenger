using System;
using System.Collections.Generic;
using System.Text;

namespace my.messenger.common.Messaging
{
    public interface IMessageConsumer
    {
        void Handle(Message msg) ;

        bool CanHandle();
    }
}
