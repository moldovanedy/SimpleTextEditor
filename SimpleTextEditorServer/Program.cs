using System;
using System.Text;
using System.Threading.RateLimiting;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.RateLimiting;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using Microsoft.IdentityModel.Tokens;
using Newtonsoft.Json;

namespace SimpleTextEditorServer
{
    internal static class Program
    {
        public static string? ConnectionString { get; private set; }
        public static MySqlServerVersion ServerVersion { get; private set; } 
            = new(new Version(8, 4, 4));
        
        private static void Main(string[] args)
        {
            WebApplicationBuilder builder = WebApplication.CreateBuilder(args);
    
            builder.Configuration.AddEnvironmentVariables();
    
            builder.Services.AddControllers().AddNewtonsoftJson(options =>
            {
                options.SerializerSettings.ReferenceLoopHandling = ReferenceLoopHandling.Ignore;
            });
    
            builder.Services.AddRateLimiter(rateLimiterOptions => rateLimiterOptions
                .AddFixedWindowLimiter(
                    policyName: "authLimiter",
                    options =>
                    {
                        options.PermitLimit = 5;
                        options.Window = TimeSpan.FromSeconds(30);
                        options.QueueProcessingOrder = QueueProcessingOrder.OldestFirst;
                        options.QueueLimit = 2;
                    })
                .AddFixedWindowLimiter(
                    policyName: "generalLimiter",
                    options =>
                    {
                        options.PermitLimit = 3;
                        options.Window = TimeSpan.FromSeconds(5);
                        options.QueueProcessingOrder = QueueProcessingOrder.OldestFirst;
                        options.QueueLimit = 2;
                    }));
            
            //MySQL
            ConnectionString = builder.Configuration.GetValue<string>("DbConnectionString");
            ServerVersion = new MySqlServerVersion(new Version(8, 4, 4));

            builder.Services.AddDbContext<AppDbContext>(
                dbContextOptions => dbContextOptions
                    .UseMySql(ConnectionString, ServerVersion)
                    
                #if DEBUG
                    .LogTo(Console.WriteLine, LogLevel.Information)
                    .EnableSensitiveDataLogging()
                    .EnableDetailedErrors()
                #else
                    .LogTo(Console.WriteLine, LogLevel.Warning)
                #endif
                );
            
            //JWT
            builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
                   .AddJwtBearer(options =>
                   {
                       options.TokenValidationParameters = new TokenValidationParameters
                       {
                           ValidateIssuer = true,
                           ValidateAudience = true,
                           ValidateLifetime = true,
                           ValidateIssuerSigningKey = true,
                           ValidIssuer = builder.Configuration.GetValue<string>("JwtIssuer"),
                           ValidAudience = builder.Configuration.GetValue<string>("JwtIssuer"),
                           IssuerSigningKey = new SymmetricSecurityKey(
                               Encoding.UTF8.GetBytes(
                                   builder.Configuration.GetValue<string>("JwtSecret") ??
                                   throw new ApplicationException("JwtSecret not found")))
                       };
                   });
    
            builder.Services.AddProblemDetails();
    
            WebApplication app = builder.Build();
            app.UseRateLimiter();
            app.MapControllers();
    
            app.Run();
        }
    }
}
