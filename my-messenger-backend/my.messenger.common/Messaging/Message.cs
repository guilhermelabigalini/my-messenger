using System;
using System.Collections.Generic;
using System.Text;

namespace my.messenger.common.Messaging
{
    public class Message
    {
        public String FromUserId { get; set; }
        public Destination To { get; set; }
        public MessageType Type { get; set; }
        public String Body { get; set; }
        public DateTime SentAt { get; set; }
    }
}
