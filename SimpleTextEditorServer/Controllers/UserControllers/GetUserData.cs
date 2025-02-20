using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.RateLimiting;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Primitives;
using Newtonsoft.Json.Linq;
using SimpleTextEditorServer.Models;

namespace SimpleTextEditorServer.Controllers.UserControllers;

[ApiController]
[Route("users")]
[EnableRateLimiting("generalLimiter")]
public class GetUserData : ControllerBase
{
    [HttpGet]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    [Produces("application/json")]
    public async Task<IActionResult> GetUserDataEndpoint()
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
            
        //TODO: check token against the DB
        await using var ctx = new AppDbContext();
        User? user = await ctx.Users.FirstOrDefaultAsync(dbUser => dbUser.AuthToken == token);
        if (user == null)
        {
            return Unauthorized("You are not logged in.");
        }

        return Ok(
            new JObject(
                new JProperty("username", user.Username),
                new JProperty("email", user.Email)));
    }
}