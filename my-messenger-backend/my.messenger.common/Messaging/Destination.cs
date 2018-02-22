using System;

namespace my.messenger.common.Messaging
{
    public class Destination
    {
        public DestinationType Type { get; set; }
        public String Id { get; set; }

        public Destination(DestinationType type, string id)
        {
            Type = type;
            Id = id;
        }

        public Destination()
        {
        }
    }
}