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
using SimpleTextEditorServer.Validations;

namespace SimpleTextEditorServer.Controllers.UserControllers;

[ApiController]
[Route("users")]
[EnableRateLimiting("authLimiter")]
public class ForgotPassword : ControllerBase
{
    [HttpPost("forgot-password")]
    [ProducesResponseType(StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    [Produces("application/json")]
    public async Task<IActionResult> ForgotPasswordEndpoint(ForgotPasswordUserDto userDto)
    {
        string? errorMessage = UserValidations.ValidatePassword(userDto.NewPassword);
        if (errorMessage != null)
        {
            return BadRequest(errorMessage);
        }

        await using var ctx = new AppDbContext();
        PasswordHasher<User> passwordHasher = new();
        string hashedPassword = passwordHasher.HashPassword(new User(), userDto.NewPassword);
            
        User? user = await ctx.Users.FirstOrDefaultAsync(dbUser => dbUser.Email == userDto.Email);
        if (user == null)
        {
            return NotFound("This email does not exist.");
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
            ctx.Entry(user).CurrentValues[nameof(Models.User.Password)] = hashedPassword;
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