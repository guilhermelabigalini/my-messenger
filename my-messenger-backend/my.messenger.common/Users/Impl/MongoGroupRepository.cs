using Microsoft.Extensions.Configuration;
using MongoDB.Driver;
using System;
using System.Collections.Generic;
using System.Security.Authentication;
using System.Text;
using System.Threading.Tasks;

namespace my.messenger.common.Users.Impl
{
    public class MongoGroupRepository : MongoDB, IGroupRepository
    {
        public MongoGroupRepository(IConfiguration configuration) : base(configuration)
        {
        }

        public Task AddMemberAsync(string groupId, string memberUserId)
        {
            return group.UpdateOneAsync(a => a.Id == groupId,
                Builders<Group>.Update.AddToSet(g => g.Members, memberUserId));
        }

        public Task<Group> FindByIdAsync(string groupId)
        {
            return group.Find(up => up.Id == groupId).FirstOrDefaultAsync();
        }

        public async Task InsertAsync(Group g)
        {
            await group.InsertOneAsync(g);
        }

        public Task RemoveMemberAsync(string groupId, string memberUserId)
        {
            return group.UpdateOneAsync(a => a.Id == groupId,
                Builders<Group>.Update.Pull(g => g.Members, memberUserId));
        }
    }
}
