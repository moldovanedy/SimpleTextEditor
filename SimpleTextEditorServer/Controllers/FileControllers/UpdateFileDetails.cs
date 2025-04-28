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
[Route("files/details")]
[EnableRateLimiting("generalLimiter")]
public class UpdateFileDetails : ControllerBase
{
    [HttpPut("{fileId}")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    [Produces("application/json")]
    public async Task<IActionResult> UpdateFileName(string fileId, [FromBody] string newName)
    {
        if (string.IsNullOrEmpty(newName) || string.IsNullOrWhiteSpace(newName))
        {
            return BadRequest("File name is required and cannot be empty or consist only of white space.");
        }
        
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
        
        ctx.Entry(dbFile).CurrentValues[nameof(Models.File.Name)] = newName;
        
        try
        {
            await ctx.SaveChangesAsync();
        }
        catch (DbUpdateException ex)
        { 
            Trace.TraceError(ex.Message);
            return StatusCode(500, "An unknown error occurred.");
        }
        
        return Ok();
    }
}