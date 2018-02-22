using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using my.messenger.common.Messaging;

namespace my.messenger.backend.Controllers
{
    [Produces("application/json")]
    [Route("api/Message")]
    [TypeFilter(typeof(SessionHeaderAuthorizeAttribute))]
    public class MessageController : Controller
    {
        private MessageService messageService;

        public MessageController(MessageService messageService)
        {
            this.messageService = messageService;
        }

        [HttpGet]
        public async Task<ActionResult> IndexAsync(int count = 1)
        {
            var session = User.Identity.AsUserSessionIdentity().Session;

            return Ok(await messageService.Dequeue(session.UserId, count));
        }

        [HttpPost]
        public async Task<ActionResult> IndexAsync([FromBody]TransmittedMessage message)
        {
            var session = User.Identity.AsUserSessionIdentity().Session;

            Message msg = new Message();
            msg.Body = (message.Body);
            msg.To = (message.To);
            msg.Type = (message.Type);
            msg.SentAt = DateTime.Now;
            msg.FromUserId = session.UserId;

            await messageService.SendAsync(msg);

            return Ok();
        }
    }
}