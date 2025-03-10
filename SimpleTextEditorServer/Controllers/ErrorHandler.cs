using Microsoft.AspNetCore.Mvc;

namespace SimpleTextEditorServer.Controllers;

[ApiController]
public class ErrorHandler : ControllerBase
{
    [Route("/error")]
    public IActionResult Error()
    {
        return StatusCode(500, "An unknown error has occurred on the server.");
    }
}