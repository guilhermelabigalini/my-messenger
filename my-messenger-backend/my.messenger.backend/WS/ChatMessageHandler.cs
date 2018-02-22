using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Logging;
using my.messenger.common.Messaging;
using my.messenger.common.Messaging.Impl;
using my.messenger.common.Users;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.WebSockets;
using System.Threading;
using System.Threading.Tasks;

namespace my.messenger.backend.WS
{
    public class ChatMessageHandler
    {
        private MessageService messageService;
        private UserService userService;
        private UserSession userSession;
        private ISubscriber subscriber;
        private ILogger<ChatMessageHandler> logger;

        public ChatMessageHandler(UserService userService, MessageService messageService, ILogger<ChatMessageHandler> logger)
        {
            this.messageService = messageService;
            this.userService = userService;
            this.logger = logger;
        }

        public async Task AfterConnectionOpenedAsync(HttpContext context, WebSocket webSocket)
        {
        }

        public async Task AfterConnectionClosedAsync(HttpContext context, WebSocket webSocket)
        {
            if (subscriber != null)
            {
                subscriber.Close();
                subscriber = null;
            }
        }

        public async Task HandleMessageAsync(HttpContext context, WebSocket webSocket, string message)
        {
            StreamMessage strmMsg = JsonUtil.FromString<StreamMessage>(message);

            //await webSocket.RespondText("hi : " + message);

            try
            {
                switch (strmMsg.StreamMessageType)
                {
                    case StreamMessageType.Singin:

                        String token = strmMsg.TokenId;
                        UserSession us = await userService.GetSessionAsync(token);

                        if (us != null)
                        {
                            logger.LogInformation("registering message listener for " + us);

                            ISubscriber sub = messageService.Subscribe(us.UserId, new WSMessageConsumer(webSocket));
                            this.userSession = us;
                            this.subscriber = sub;

                            await webSocket.RespondText(JsonUtil.ToString(StreamMessageResponse.ofOk()));

                        }
                        else
                        {
                            logger.LogWarning("unable to find a valid session with id " + token);
                            await webSocket.CloseAsync(WebSocketCloseStatus.InvalidMessageType, "badtoken", CancellationToken.None);
                        }

                        break;
                    case StreamMessageType.Ping:
                        logger.LogInformation("Get PING");
                        await webSocket.RespondText(JsonUtil.ToString(StreamMessageResponse.ofOk()));
                        break;
                    case StreamMessageType.Info:
                        logger.LogInformation("Get Info");
                        if (this.userSession != null)
                        {
                            await webSocket.RespondText(JsonUtil.ToString(StreamMessageResponse.ofUserSession(this.userSession)));
                        }
                        else
                        {
                            await webSocket.RespondText(JsonUtil.ToString(StreamMessageResponse.ofOk()));
                        }
                        break;
                    case StreamMessageType.Message:
                        break;
                }
            }
            catch (Exception ex)
            {
                await webSocket.CloseAsync(WebSocketCloseStatus.InvalidMessageType, "exception", CancellationToken.None);
                logger.LogError(ex.ToString());
            }
        }
    }

    internal class WSMessageConsumer : IMessageConsumer
    {
        private WebSocket webSocket;

        public WSMessageConsumer(WebSocket webSocket)
        {
            this.webSocket = webSocket;
        }

        public bool CanHandle()
        {
            return webSocket.State == WebSocketState.Open;
        }

        public async void Handle(Message msg)
        {
            StreamMessageResponse response = StreamMessageResponse.ofMessage(msg);
            await webSocket.RespondText(JsonUtil.ToString(response));
        }
    }
}
