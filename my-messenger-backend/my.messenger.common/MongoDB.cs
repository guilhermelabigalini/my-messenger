using Microsoft.Extensions.Configuration;
using MongoDB.Driver;
using my.messenger.common.Users;
using System;
using System.Collections.Generic;
using System.Security.Authentication;
using System.Text;

namespace my.messenger.common
{
    public class MongoDB
    {
        protected IMongoDatabase database;
        protected IMongoCollection<UserProfile> userProfile;
        protected IMongoCollection<UserSession> userSession;
        protected IMongoCollection<Group> group;

        public MongoDB(IConfiguration configuration)
        {
            var section = configuration.GetSection("MongoDB");

            var connectionString = section["ConnectionString"];

            MongoClientSettings settings = MongoClientSettings.FromUrl(
              new MongoUrl(connectionString)
            );
            settings.SslSettings =
              new SslSettings() { EnabledSslProtocols = SslProtocols.Tls12 };
            var mongoClient = new MongoClient(settings);
            this.database = mongoClient.GetDatabase("mymessenger");
            this.userProfile = database.GetCollection<UserProfile>("userProfile");
            this.userSession = database.GetCollection<UserSession>("userSession");
            this.group = database.GetCollection<Group>("group");
        }
    }
}
