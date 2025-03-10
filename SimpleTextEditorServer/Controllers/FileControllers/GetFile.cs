using System;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.RateLimiting;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Primitives;
using SimpleTextEditorServer.Models;

namespace SimpleTextEditorServer.Controllers.FileControllers;

[ApiController]
[Route("files")]
[EnableRateLimiting("generalLimiter")]
public class GetFile : ControllerBase
{
    [HttpGet("{fileId}")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    [Produces("application/json")]
    public async Task<IActionResult> GetFileEndpoint(string fileId)
    {
        if (!Guid.TryParse(fileId, out Guid guid))
        {
            return BadRequest("File ID is invalid.");
        }
        
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
        File? dbFile = await ctx.Files.Where(x => x.Id == guid).FirstOrDefaultAsync();
        if (dbFile == null)
        {
            return NotFound("File not found.");
        }
        
        string? content = 
            await FileManager.GetFileContentAsync(guid, DateTimeOffset.FromUnixTimeSeconds(dbFile.DateCreated));
        if (content == null)
        {
            return NotFound("File not found.");
        }

        return Ok(content);
    }
}