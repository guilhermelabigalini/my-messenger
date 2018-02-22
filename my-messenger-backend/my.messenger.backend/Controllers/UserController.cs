using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using my.messenger.common.Users;
using Microsoft.AspNetCore.Authorization;

namespace my.messenger.backend.Controllers
{
    [Produces("application/json")]
    [Route("api/User")]
    [AllowAnonymous]
    public class UserController : Controller
    {
        private UserService userService;

        public UserController(UserService userService)
        {
            this.userService = userService;
        }

        [HttpGet()]
        public async Task<ActionResult> GetByIdAsync(string userId)
        {
            var up = await userService.FindPublicProfileByUserIdAsync(userId);

            if (up == null)
                return NotFound();

            return Ok(up);
        }

        // GET api/User/user123
        [HttpGet("{username}")]
        public async Task<ActionResult> GetAsync(string username)
        {
            var up = await userService.FindPublicProfileByUsernameAsync(username);

            if (up == null)
                return NotFound();

            return Ok(up);
        }

        // POST api/User/register
        [HttpPost()]
        [Route("Register")]
        public async Task<CreatedResult> RegisterAsync([FromBody]UserProfile profile)
        {
            await userService.RegisterAsync(profile);

            return Created("api/user/" + profile.Username, null);
        }

        // /api/user/login
        [HttpPost()]
        [Route("Login")]
        public async Task<OkObjectResult> LoginAsync([FromBody]LoginRequest login)
        {
            return Ok(await userService.LoginAsync(login));
        }
    }
}