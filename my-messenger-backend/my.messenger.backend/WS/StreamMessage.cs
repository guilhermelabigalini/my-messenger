using my.messenger.common.Messaging;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace my.messenger.backend.WS
{
    public class StreamMessage
    {
        public StreamMessageType StreamMessageType { get; set; }

        public Message Message { get; set; }

        public String TokenId { get; set; }
    }
}
