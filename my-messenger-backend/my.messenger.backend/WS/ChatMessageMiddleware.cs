using Microsoft.AspNetCore.Http;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net.WebSockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace my.messenger.backend.WS
{
    public class ChatMessageMiddleware
    {
        private readonly RequestDelegate next;
        private readonly ChatMessageMiddlewareOptions options;
        private readonly IServiceProvider serviceProvider;

        public ChatMessageMiddleware(RequestDelegate next, ChatMessageMiddlewareOptions options, IServiceProvider serviceProvider)
        {
            this.next = next;
            this.options = options;
            this.serviceProvider = serviceProvider;
        }

        public async Task Invoke(HttpContext context)
        {
            if (!context.WebSockets.IsWebSocketRequest)
            {
                await next(context);
                return;
            }

            if (context.Request.Path == options.EndPoint)
            {
                if (context.WebSockets.IsWebSocketRequest)
                {
                    WebSocket webSocket = await context.WebSockets.AcceptWebSocketAsync();
                    await Receive(context, webSocket);
                }
                else
                {
                    context.Response.StatusCode = StatusCodes.Status400BadRequest;
                }
            }
            else
            {
                await next(context);
            }

            //var socket = await context.WebSockets.AcceptWebSocketAsync();
            //var id = _socketManager.AddSocket(socket);

            //await Receive(socket, async (result, buffer) =>
            //{
            //    if (result.MessageType == WebSocketMessageType.Close)
            //    {
            //        await _socketManager.RemoveSocket(id);
            //        return;
            //    }
            //});
        }

        private async Task Receive(HttpContext context, WebSocket webSocket)
        {
            var tmpBuffer = new byte[1024 * 4];
            var buffer = new StringBuilder();

            ChatMessageHandler handler = null;
            try
            {
                handler = serviceProvider.GetService(typeof(ChatMessageHandler)) as ChatMessageHandler;

                await handler.AfterConnectionOpenedAsync(context, webSocket);

                WebSocketReceiveResult result = await webSocket.ReceiveAsync(new ArraySegment<byte>(tmpBuffer), CancellationToken.None);
                while (!result.CloseStatus.HasValue)
                {
                    buffer.Append(Encoding.UTF8.GetString(tmpBuffer, 0, result.Count));

                    if (result.EndOfMessage)
                    {
                        await handler.HandleMessageAsync(context, webSocket, buffer.ToString());
                        buffer.Clear();
                    }

                    //await webSocket.SendAsync(new ArraySegment<byte>(buffer, 0, result.Count), result.MessageType, result.EndOfMessage, CancellationToken.None);

                    result = await webSocket.ReceiveAsync(new ArraySegment<byte>(tmpBuffer), CancellationToken.None);
                }

                await webSocket.CloseAsync(result.CloseStatus.Value, result.CloseStatusDescription, CancellationToken.None);
            }
            finally
            {
                if (handler != null)
                    await handler.AfterConnectionClosedAsync(context, webSocket);
            }
        }
    }

    public static class WebSocketHelper
    {
        public static async Task RespondText(this WebSocket socket, string data)
        {
            var bytes = Encoding.UTF8.GetBytes(data);

            await socket.SendAsync(new ArraySegment<byte>(bytes, 0, bytes.Length), WebSocketMessageType.Text, true, CancellationToken.None);
        }
    }
}
