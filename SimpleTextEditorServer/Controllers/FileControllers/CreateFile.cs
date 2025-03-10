using System;
using System.Diagnostics;
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
public class CreateFile : ControllerBase
{
    [HttpPost]
    [ProducesResponseType(StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    [Produces("application/json")]
    public async Task<IActionResult> CreateFileEndpoint([FromBody] string fileName)
    {
        if (string.IsNullOrEmpty(fileName) || string.IsNullOrWhiteSpace(fileName))
        {
            return BadRequest("File name is required and cannot be empty or consist only of white space.");
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

        await using (var ctx = new AppDbContext())
        {
            //if the user already has a file with the same name, deny it
            if (await ctx.Files.AnyAsync(dbFile => dbFile.CreatedBy.AuthToken == token && dbFile.Name == fileName))
            {
                return BadRequest("You already created a file with the same name.");
            }
            
            var userData = await ctx.Users
                .Select(user => new { user.Id, user.AuthToken })
                .FirstOrDefaultAsync(dbUser => dbUser.AuthToken == token);

            if (userData == null)
            {
                return Unauthorized("You are not logged in.");
            }

            DateTimeOffset timestamp = DateTimeOffset.UtcNow;
            
            var file = new File
            {
                Name = fileName,
                UserId = userData.Id,
                DateCreated = timestamp.ToUnixTimeSeconds(),
                DateModified = timestamp.ToUnixTimeSeconds()
            };

            bool ioSuccess = FileManager.CreateFile(file.Id, timestamp);
            if (!ioSuccess)
            {
                return StatusCode(500, "An unknown error occurred.");
            }
            
            await ctx.Files.AddAsync(file);

            try
            {
                await ctx.SaveChangesAsync();
            }
            catch (DbUpdateException ex)
            {
                FileManager.DeleteFile(file.Id, timestamp);
                
                Trace.TraceError(ex.Message);
                return StatusCode(500, "An unknown error occurred.");
            }
        }
        return Created();
    }
}