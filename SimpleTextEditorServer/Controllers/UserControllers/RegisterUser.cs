using System.Diagnostics;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.RateLimiting;
using Microsoft.EntityFrameworkCore;
using SimpleTextEditorServer.DTO.Users;
using SimpleTextEditorServer.Models;
using SimpleTextEditorServer.Validations;

namespace SimpleTextEditorServer.Controllers.UserControllers;

[ApiController]
[Route("users")]
[EnableRateLimiting("authLimiter")]
public class RegisterUser : ControllerBase
{
    [HttpPost]
    [ProducesResponseType(StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    [Produces("application/json")]
    public async Task<IActionResult> RegisterEndpoint(NewUserDto userDto)
    {
        string? errorMessage = UserValidations.ValidateUsername(userDto.Username);
        if (errorMessage != null)
        {
            return BadRequest(errorMessage);
        }
        
        errorMessage = UserValidations.ValidateEmail(userDto.Email);
        if (errorMessage != null)
        {
            return BadRequest(errorMessage);
        }
        
        errorMessage = UserValidations.ValidatePassword(userDto.Password);
        if (errorMessage != null)
        {
            return BadRequest(errorMessage);
        }

        var user = new User
        {
            Email = userDto.Email, Username = userDto.Username
        };
            
        PasswordHasher<User> passwordHasher = new();
        string hashedPassword = passwordHasher.HashPassword(user, userDto.Password);
        user.Password = hashedPassword;

        await using (var ctx = new AppDbContext())
        {
            if (await ctx.Users.AnyAsync(dbUser => dbUser.Email == userDto.Email))
            {
                return BadRequest("This email is already registered.");
            }

            await ctx.Users.AddAsync(user);

            try
            {
                await ctx.SaveChangesAsync();
            }
            catch (DbUpdateException e)
            {
                Trace.TraceError(e.Message);
                return StatusCode(500, "An unknown error occurred.");
            }
        }
        
        return Created();
    }
}