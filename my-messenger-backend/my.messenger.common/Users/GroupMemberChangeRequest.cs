using System;
using System.Collections.Generic;
using System.Text;

namespace my.messenger.common.Users
{
    public class GroupMemberChangeRequest
    {
        public String GroupId { get; set; }
        public String MemberUserId { get; set; }
    }
}
