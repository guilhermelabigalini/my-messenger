using my.messenger.common.Messaging;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;

namespace my.messenger.common.Users
{
    public class UserService
    {
        private IUserRepository userRepository;
        private IUserSessionRepository userSessionRepository;
        private IMessageSender messageSender;

        public UserService(
            IUserRepository userRepository, 
            IUserSessionRepository userSessionRepository,
            IMessageSender messageSender)
        {
            this.userRepository = userRepository;
            this.userSessionRepository = userSessionRepository;
            this.messageSender = messageSender;
        }

        public async Task RegisterAsync(UserProfile user)
        {
            if (!StringUtils.hasText(user.Username))
            {
                throw new ValidationException("username is required");
            }

            if (!StringUtils.hasText(user.Password))
            {
                throw new ValidationException("password is required");
            }

            if (user.BirthDate == null || user.BirthDate == DateTime.MinValue)
            {
                throw new ValidationException("BirthDate is required");
            }

            var dbUser = await userRepository.FindByUsernameAsync(user.Username);
            if (dbUser != null)
            {
                throw new ValidationException("username already exists");
            }

            user.Password = (PasswordUtil.encode(user.Password));
            await userRepository.InsertAsync(user);

            try
            {
                await messageSender.RegisterDestinationAsync(new Destination(DestinationType.User, user.Id));
            }
            catch (Exception e)
            {
                await userRepository.DeleteByIdAsync(user.Id);
                throw;
            }
        }

        public async Task<UserSession> GetSessionAsync(string sessionId)
        {
            return await userSessionRepository.FindByIdAsync(sessionId);
        }

        public async Task<UserSession> LoginAsync(LoginRequest lr)
        {
            UserProfile profile = await FindByUsernameAsync(lr.Username);

            if (profile == null)
            {
                throw new ValidationException("username does not exists");
            }

            if (PasswordUtil.encode(lr.Password).Equals(profile.Password))
            {
                UserSession us = new UserSession()
                {
                    CreatedAt = DateTime.Now,
                    UserId = profile.Id
                };

                await userSessionRepository.InsertAsync(us);

                return us;
            }
            
            throw new ValidationException("Invalid password");
        }

        public Task<UserProfile> FindByUsernameAsync(string username)
        {
            return userRepository.FindByUsernameAsync(username);
        }

        public Task<UserProfile> FindPublicProfileByUserIdAsync(string userId)
        {
            return userRepository.FindByIdAsync(userId).ContinueWith(up =>
            {
                var r = up.Result;
                if (r != null)
                    r.Password = null;
                return r;
            });
        }

        public Task<UserProfile> FindPublicProfileByUsernameAsync(string username)
        {
            return userRepository.FindByUsernameAsync(username).ContinueWith(up =>
            {
                var r = up.Result;
                if (r != null)
                    r.Password = null;
                return r;
            });
        }

        public async Task LogoutAsync(string id)
        {
            await userSessionRepository.DeleteAsync(id);
        }

    }
}
