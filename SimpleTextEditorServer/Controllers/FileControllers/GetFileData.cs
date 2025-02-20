using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.RateLimiting;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Primitives;
using Newtonsoft.Json.Linq;
using SimpleTextEditorServer.Models;

namespace SimpleTextEditorServer.Controllers.FileControllers;

[ApiController]
[Route("files")]
[EnableRateLimiting("generalLimiter")]
public class GetFileData : ControllerBase
{
    [HttpGet("{fileId}")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    [Produces("application/json")]
    public async Task<IActionResult> GetFileDataEndpoint(string fileId)
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

        await using var ctx = new AppDbContext();
        File? file = await ctx.Files.FirstOrDefaultAsync(dbFile => dbFile.Id == guid);
            
        if (file == null)
        {
            return NotFound("The file with the specified ID was not found.");
        }

        return Ok(new JObject(
            new JProperty("Id", file.Id),
            new JProperty("Name", file.Name),
            new JProperty("Length", file.Length),
            new JProperty("DateCreated", file.DateCreated),
            new JProperty("DateModified", file.DateModified)));
    }
    
    [HttpGet]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status401Unauthorized)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    [ProducesResponseType(StatusCodes.Status500InternalServerError)]
    [Produces("application/json")]
    public async Task<IActionResult> GetFileDataEndpoint()
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
        List<File> files = await ctx.Files.Where(dbFile => dbFile.CreatedBy.AuthToken == token).ToListAsync();

        JArray json = [];
        foreach (File file in files)
        {
            json.Add(
                new JObject(
                    new JProperty("Id", file.Id),
                    new JProperty("Name", file.Name),
                    new JProperty("Length", file.Length),
                    new JProperty("DateCreated", file.DateCreated),
                    new JProperty("DateModified", file.DateModified)));
        }
        
        return Ok(json);
    }
}