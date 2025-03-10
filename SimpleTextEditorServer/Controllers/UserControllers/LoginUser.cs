using System;
using System.Diagnostics;
using System.IdentityModel.Tokens.Jwt;
using System.Text;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.RateLimiting;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using Newtonsoft.Json.Linq;
using SimpleTextEditorServer.DTO.Users;
using SimpleTextEditorServer.Models;

namespace SimpleTextEditorServer.Controllers.UserControllers;

[ApiController]
[Route("users")]
[EnableRateLimiting("generalLimiter")]
public class LoginUser : ControllerBase
{
    [HttpPost("login")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    [Produces("application/json")]
    public async Task<IActionResult> LoginUserEndpoint(LoginUserDto userDto)
    {
        await using var ctx = new AppDbContext();
        User? user = await ctx.Users.FirstOrDefaultAsync(dbUser => dbUser.Email == userDto.Email);
        if (user == null)
        {
            return BadRequest("Email or password is incorrect.");
        }
        
        PasswordHasher<User> passwordHasher = new();
        if (
            passwordHasher.VerifyHashedPassword(new User(), user.Password, userDto.Password) 
            != PasswordVerificationResult.Success)
        {
            return BadRequest("Email or password is incorrect.");
        }

        try
        {
            SymmetricSecurityKey securityKey = new(Encoding.UTF8.GetBytes(Program.JwtSecret));
            SigningCredentials credentials = new(securityKey, SecurityAlgorithms.HmacSha256);
            JwtSecurityToken securityToken = new(
                issuer: Program.JwtIssuer,
                audience: Program.JwtIssuer,
                claims: null,
                notBefore: null,
                expires: DateTime.Now.AddMonths(6),
                credentials);
            string finalToken = new JwtSecurityTokenHandler().WriteToken(securityToken);
                
            ctx.Entry(user).CurrentValues[nameof(Models.User.AuthToken)] = finalToken;
            await ctx.SaveChangesAsync();
                
            return Ok(new JObject(
                new JProperty("authToken", finalToken)));
        }
        catch (DbUpdateException ex)
        {
            Trace.TraceError(ex.Message);
            return StatusCode(500, "An unknown error occurred.");
        }
    }
}