using System;
using System.Diagnostics;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Primitives;
using SimpleTextEditorServer.DTO.Files;
using SimpleTextEditorServer.Models;

namespace SimpleTextEditorServer.Controllers.FileControllers;

[ApiController]
[Route("files/{fileId}")]
public class UpdateFile : ControllerBase
{
    [HttpPost("")]
    public async Task<IActionResult> UpdateFileByDiffEndpoint(string fileId, FileDiffDto fileDiffDto)
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

        ObjectRef<bool> isFileTooLarge = new(false);
        bool success = await FileManager.UpdateFileAsync(
            guid,
            DateTimeOffset.FromUnixTimeSeconds(dbFile.DateCreated),
            fileDiffDto.TextChange,
            fileDiffDto.IsAdded,
            fileDiffDto.Index,
            isFileTooLarge);

        if (!success)
        {
            return StatusCode(500, "Content update failed.");
        }
        
        ctx.Entry(dbFile).CurrentValues[nameof(Models.File.DateModified)] = DateTimeOffset.UtcNow.ToUnixTimeSeconds();
        
        try
        {
            await ctx.SaveChangesAsync();
        }
        catch (DbUpdateException ex)
        { 
            Trace.TraceError(ex.Message);
            return StatusCode(500, "An unknown error occurred.");
        }

        if (isFileTooLarge.Value)
        {
            return StatusCode(507, "File is too large. Subsequent additions will be skipped.");
        }
        
        return Ok();
    }

    [HttpPost("full-update")]
    public async Task<IActionResult> UpdateFileByFullContent(string fileId, FileFullUpdateDto updateDto)
    {
        if (!Guid.TryParse(fileId, out Guid guid))
        {
            return BadRequest("File ID is invalid.");
        }
        
        if (updateDto.Content.Length > 500_000)
        {
            return StatusCode(507, "File is too large. Update rejected.");
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

        bool success = await FileManager.FullyUpdateFileAsync(
            guid, 
            DateTimeOffset.FromUnixTimeSeconds(dbFile.DateCreated), 
            updateDto.Content);
        if (!success)
        {
            return StatusCode(500, "Content update failed.");
        }
        
        ctx.Entry(dbFile).CurrentValues[nameof(Models.File.DateModified)] = DateTimeOffset.UtcNow.ToUnixTimeSeconds();
        
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