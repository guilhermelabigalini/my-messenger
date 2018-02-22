using System;
using System.Collections.Generic;
using System.Text;

namespace my.messenger.common.Messaging
{
    public class TransmittedMessage
    {
        public Destination To { get; set; }
        public MessageType Type { get; set; }
        public String Body { get; set; }
    }
}
