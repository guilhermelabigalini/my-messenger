using System;
using System.Collections.Generic;
using System.Text;

namespace my.messenger.common.Users
{
    public class Group
    {
        public String Id { get; set; }
        public String Name { get; set; }
        public String OwnerUserId { get; set; }
        public List<String> Members { get; set; }

        public Group()
        {
            Id = Identifier.New();
        }
    }
}
