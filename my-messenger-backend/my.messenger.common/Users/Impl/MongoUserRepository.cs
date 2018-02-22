using Microsoft.Extensions.Configuration;
using MongoDB.Driver;
using System;
using System.Collections.Generic;
using System.Security.Authentication;
using System.Text;
using System.Threading.Tasks;

namespace my.messenger.common.Users.Impl
{
    public class MongoUserRepository : MongoDB, IUserRepository
    {
        public MongoUserRepository(IConfiguration configuration) : base(configuration)
        {
        }

        public Task DeleteByIdAsync(string id)
        {
            return userProfile.DeleteOneAsync(up => up.Id == id);
        }

        public Task<UserProfile> FindByIdAsync(string username)
        {
            return userProfile.Find(up => up.Id == username).FirstOrDefaultAsync();
        }

        public Task<UserProfile> FindByUsernameAsync(string username)
        {
            return userProfile.Find(up => up.Username == username).FirstOrDefaultAsync();
        }

        public async Task InsertAsync(UserProfile user)
        {
            await userProfile.InsertOneAsync(user);
        }        
    }
}
