using System.Threading.Tasks;

namespace my.messenger.common.Users
{
    public interface IUserSessionRepository
    {
        Task<UserSession> FindByIdAsync(string id);
        Task<UserSession> FindByUserIdAsync(string userId);
        Task InsertAsync(UserSession user);
        Task DeleteAsync(string id);
    }
}