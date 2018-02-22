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
    [Route("api/Group")]
    [TypeFilter(typeof(SessionHeaderAuthorizeAttribute))]
    public class GroupController : Controller
    {
        private GroupService groupService;

        public GroupController(GroupService groupService)
        {
            this.groupService = groupService;
        }

        [HttpGet("{groupId}")]
        public async Task<Group> IndexGetAsync(String groupId)
        {
            return await groupService.GetAsync(groupId);
        }

        [HttpPost]
        public async Task<ActionResult> IndexPostAsync([FromBody]Group name)
        {
            var session = User.Identity.AsUserSessionIdentity().Session;

            Group group = await groupService.RegisterAsync(name.Name, session);

            return Created("/api/group/" + group.Id, null);
        }
        
        [HttpPost("{groupId}/Remove")]
        public async Task<ActionResult> RemoveAsync(String groupId, [FromBody]GroupMemberChangeRequest request)
        {
            request.GroupId = groupId;
            var session = User.Identity.AsUserSessionIdentity().Session;

            await groupService.RemoveMemberAsync(request, session);

            return Ok();
        }

        [HttpPost("{groupId}/Add")]
        public async Task<ActionResult> AddAsync(String groupId, [FromBody]GroupMemberChangeRequest request)
        {
            request.GroupId = groupId;
            var session = User.Identity.AsUserSessionIdentity().Session;

            await groupService.AddMemberAsync(request, session);

            return Ok();
        }
    }
}