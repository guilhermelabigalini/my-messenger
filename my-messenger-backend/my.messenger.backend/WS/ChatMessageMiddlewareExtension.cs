using Microsoft.AspNetCore.Builder;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace my.messenger.backend.WS
{
    public static class ChatMessageMiddlewareExtension
    {
        public static IApplicationBuilder UseChatMessageMiddleware(
               this IApplicationBuilder builder, ChatMessageMiddlewareOptions options)
        {
            return builder.UseMiddleware<ChatMessageMiddleware>(options);
        }
    }
}
