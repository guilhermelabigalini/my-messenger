using System;
using System.Collections.Generic;
using System.Text;

namespace my.messenger.common.Users
{
    public class UserProfile
    {
        public String Id { get; set; }
        public String Username { get; set; }
        public String Password { get; set; }
        public DateTime BirthDate { get; set; }

        public UserProfile(string id, string username, string password, DateTime birthDate)
        {
            Id = id;
            Username = username;
            Password = password;
            BirthDate = birthDate;
        }

        public UserProfile()
        {
            Id = Identifier.New();
        }
    }
}
