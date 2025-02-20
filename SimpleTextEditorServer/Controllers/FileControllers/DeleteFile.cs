using System;
using System.Collections.Generic;
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
public class DeleteFile : ControllerBase
{
    [HttpDelete("{fileId}")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    [Produces("application/json")]
    public async Task<IActionResult> DeleteFileEndpoint(string fileId)
    {
        if (Guid.TryParse(fileId, out Guid guid))
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

        await using (var ctx = new AppDbContext())
        {
            File? file = await ctx.Files.FirstOrDefaultAsync(dbFile => dbFile.Id == guid);
            
            if (file == null)
            {
                return NotFound("The file with the specified ID was not found.");
            }

            ctx.Files.Remove(file);

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
        
        return Ok();
    }
}