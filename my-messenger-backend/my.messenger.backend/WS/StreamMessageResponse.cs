using my.messenger.common.Messaging;
using my.messenger.common.Users;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace my.messenger.backend.WS
{
    public class StreamMessageResponse
    {
        public static StreamMessageResponse ofOk()
        {
            StreamMessageResponse r = new StreamMessageResponse();
            r.Ok = (true);
            return r;
        }

        public static StreamMessageResponse ofUserSession(UserSession us)
        {
            StreamMessageResponse r = new StreamMessageResponse();
            r.Ok = (true);
            r.UserSession = (us);
            return r;
        }

        public static StreamMessageResponse ofMessage(Message msg)
        {
            StreamMessageResponse r = new StreamMessageResponse();
            r.Ok = (true);
            r.Message = (msg);
            return r;
        }

        public bool Ok { get; set; }
        public UserSession UserSession { get; set; }
        public Message Message { get; set; }
    }
}
