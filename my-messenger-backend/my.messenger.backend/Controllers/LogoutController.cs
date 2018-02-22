using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using my.messenger.common.Users;

namespace my.messenger.backend.Controllers
{
    [Produces("application/json")]
    [Route("api/Logout")]
    [TypeFilter(typeof(SessionHeaderAuthorizeAttribute))]
    public class LogoutController : Controller
    {
        private UserService userService;

        public LogoutController(UserService userService)
        {
            this.userService = userService;
        }

        // GET api/values
        [HttpGet]
        [HttpPost]
        public async Task<ActionResult> IndexAsync()
        {
            var session = User.Identity.AsUserSessionIdentity().Session;
            await userService.LogoutAsync(session.Id);
            return Ok();
        }
    }
}