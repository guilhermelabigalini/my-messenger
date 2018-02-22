using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;

namespace my.messenger.common.Users
{
    public interface IGroupRepository
    {
        Task InsertAsync(Group user);

        Task<Group> FindByIdAsync(string groupId);

        Task AddMemberAsync(String groupId, String memberUserId);

        Task RemoveMemberAsync(String groupId, String memberUserId);
    }
}
