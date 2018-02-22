using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Authorization.Infrastructure;
using Microsoft.AspNetCore.Mvc.Authorization;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc.Filters;
using Microsoft.AspNetCore.Mvc;
using my.messenger.common.Users;

namespace my.messenger.backend
{
    public class SessionHeaderAuthorizeAttribute : Attribute, IAsyncAuthorizationFilter
    {
        private UserService userService;

        public SessionHeaderAuthorizeAttribute(UserService userService)
        {
            this.userService = userService;
        }

        public async Task OnAuthorizationAsync(AuthorizationFilterContext context)
        {
            var authHeader = context.HttpContext.Request.Headers["Authorization"];
            bool ok = false;
            if (authHeader.Any())
            {
                var value = authHeader[0];
                if (value.StartsWith("Bearer "))
                {
                    String[] pieces = value.Split(" ");

                    if (pieces.Length == 2)
                    {
                        var session = await userService.GetSessionAsync(pieces[1]);
                        if (session != null)
                        {
                            context.HttpContext.User = new System.Security.Claims.ClaimsPrincipal(new UserSessionClaimIdentity(session));
                            ok = true;
                        }
                    }
                }
            }
            
            if (!ok)
                context.Result = new UnauthorizedResult();
        }
    }
}
