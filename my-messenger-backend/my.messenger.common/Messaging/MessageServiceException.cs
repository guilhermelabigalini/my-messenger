using System;
using System.Runtime.Serialization;

namespace my.messenger.common.Messaging
{
    [Serializable]
    internal class MessageServiceException : Exception
    {
        public MessageServiceException()
        {
        }

        public MessageServiceException(string message) : base(message)
        {
        }

        public MessageServiceException(string message, Exception innerException) : base(message, innerException)
        {
        }

        protected MessageServiceException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
    }
}