using System.Security.Principal;
using my.messenger.common.Users;
using System.Security.Claims;

namespace my.messenger.backend
{
    public class UserSessionClaimIdentity : ClaimsIdentity
    {
        private UserSession session;

        public UserSessionClaimIdentity(UserSession session)
        {
            this.session = session;
        }
        
        public UserSession Session { get { return session; } }
    }

    
    public static class UserSessionIdentityExt
    {
        public static UserSessionClaimIdentity AsUserSessionIdentity(this IIdentity id)
        {
            return (UserSessionClaimIdentity)id;
        }
    }
}