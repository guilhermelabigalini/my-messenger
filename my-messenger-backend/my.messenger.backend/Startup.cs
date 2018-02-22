using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Options;
using my.messenger.common.Users;
using my.messenger.common.Users.Impl;
using Newtonsoft.Json;
using my.messenger.common.Messaging.Impl;
using my.messenger.common.Messaging;
using my.messenger.backend.WS;

namespace my.messenger.backend
{
    public class Startup
    {
        public Startup(IConfiguration configuration)
        {
            Configuration = configuration;
        }

        public IConfiguration Configuration { get; }

        // This method gets called by the runtime. Use this method to add services to the container.
        public void ConfigureServices(IServiceCollection services)
        {
            services.AddTransient<IGroupRepository, MongoGroupRepository>();
            services.AddTransient<IUserRepository, MongoUserRepository>();
            services.AddTransient<IUserSessionRepository, MongoUserSessionRepository>();
            services.AddTransient<IMessageSender, RabbitMQMessageSender>();
            services.AddTransient<IMessageReceiver, RabbitMQMessageReceiver>();
            services.AddTransient<UserService>();
            services.AddTransient<MessageService>();
            services.AddTransient<ChatMessageHandler>();
            services.AddTransient<GroupService>();

            services.AddMvc(config =>
                {
                    //var policy = new AuthorizationPolicyBuilder()
                    //                 .RequireAuthenticatedUser()
                    //                 .Build();
                    //config.Filters.Add(new SessionHeaderAuthorizeAttribute());
                })
                .AddJsonOptions(options =>
                {
                    options.SerializerSettings.NullValueHandling = NullValueHandling.Ignore;
                    options.SerializerSettings.DateFormatString = "yyyy'-'MM'-'dd'T'HH':'mm':'ss.fffK";
                });
        }

        // This method gets called by the runtime. Use this method to configure the HTTP request pipeline.
        public void Configure(IApplicationBuilder app, IHostingEnvironment env)
        {
            if (env.IsDevelopment())
            {
                app.UseDeveloperExceptionPage();
            }

            app.UseMiddleware(typeof(ErrorHandlingMiddleware));
            app.UseStaticFiles();
            app.UseMvc();
            app.UseWebSockets();
            app.UseChatMessageMiddleware(new ChatMessageMiddlewareOptions()
            {
                EndPoint = "/ws/messaging"
            });
        }
    }
}
