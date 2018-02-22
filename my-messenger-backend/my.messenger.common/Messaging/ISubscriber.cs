using System;
using System.Collections.Generic;
using System.Text;

namespace my.messenger.common.Messaging
{
    public interface ISubscriber
    {
        void Close();
    }
}
