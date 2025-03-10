using System.Diagnostics;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.RateLimiting;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Primitives;
using SimpleTextEditorServer.Models;

namespace SimpleTextEditorServer.Controllers.UserControllers;

[ApiController]
[Route("users")]
[EnableRateLimiting("generalLimiter")]
public class LogoutUser : ControllerBase
{
    [HttpPost("logout")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    [Produces("application/json")]
    public async Task<IActionResult> LogoutUserEndpoint()
    {
        StringValues authToken = Request.Headers.Authorization;
        if (authToken.Count == 0 || (!authToken[0]?.StartsWith("Bearer ") ?? false))
        {
            return Unauthorized("You are not logged in.");
        }

        string? token = authToken[0]?.Substring("Bearer ".Length);
        if (string.IsNullOrWhiteSpace(token))
        {
            return Unauthorized("You are not logged in.");
        }

        await using var ctx = new AppDbContext();
        
        try
        {
            User? user = await ctx.Users.FirstOrDefaultAsync(dbUser => dbUser.AuthToken == token);
            
            if (user == null)
            {
                return Unauthorized("You are not logged in.");
            }
            
            ctx.Entry(user).CurrentValues[nameof(Models.User.AuthToken)] = "";
            await ctx.SaveChangesAsync();
            return Ok();
        }
        catch (DbUpdateException ex)
        {
            Trace.TraceError(ex.Message);
            return StatusCode(500, "An unknown error occurred.");
        }
    }
}