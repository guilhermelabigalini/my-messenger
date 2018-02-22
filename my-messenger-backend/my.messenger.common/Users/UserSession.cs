using System;
using System.Collections.Generic;
using System.Text;

namespace my.messenger.common.Users
{
    public class UserSession
    {
        public String Id { get; set; }
        public DateTime CreatedAt { get; set; }
        public String UserId { get; set; }

        public UserSession(string id, DateTime createdAt, string userId)
        {
            Id = id;
            CreatedAt = createdAt;
            UserId = userId;
        }

        public UserSession()
        {
            Id = Identifier.New();
        }

        public override string ToString()
        {
            return "UserSession{" + "id=" + Id + ", createdAt=" + CreatedAt + ", userId=" + UserId + '}';
        }
    }
}
