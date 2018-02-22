using Microsoft.Extensions.Configuration;
using MongoDB.Driver;
using System;
using System.Collections.Generic;
using System.Security.Authentication;
using System.Text;
using System.Threading.Tasks;

namespace my.messenger.common.Users.Impl
{
    public class MongoUserSessionRepository : MongoDB, IUserSessionRepository
    {
        public MongoUserSessionRepository(IConfiguration configuration) : base(configuration)
        {
        }

        public async Task DeleteAsync(string id)
        {
            await userSession.DeleteOneAsync(us => us.Id == id);
        }

        public async Task<UserSession> FindByIdAsync(string id)
        {
            return await userSession.Find(up => up.Id == id).FirstOrDefaultAsync();
        }

        public async Task<UserSession> FindByUserIdAsync(string userId)
        {
            return await userSession.Find(up => up.UserId == userId).FirstOrDefaultAsync();
        }

        public async Task InsertAsync(UserSession user)
        {
            await userSession.InsertOneAsync(user);
        }
        
    }
}
