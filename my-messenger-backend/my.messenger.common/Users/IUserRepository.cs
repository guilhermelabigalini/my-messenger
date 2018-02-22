using System.Threading.Tasks;

namespace my.messenger.common.Users
{
    public interface IUserRepository
    {
        Task<UserProfile> FindByUsernameAsync(string username);
        Task<UserProfile> FindByIdAsync(string username);
        Task InsertAsync(UserProfile user);
        Task DeleteByIdAsync(string id);
    }
}