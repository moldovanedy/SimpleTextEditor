using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations.Schema;

namespace SimpleTextEditorServer.Models;

[Table("Users")]
public class User
{
    public Guid Id { get; set; } = Guid.NewGuid();
    public string Username { get; set; } = string.Empty;
    public string Email { get; set; } = string.Empty;
    public string Password { get; set; } = string.Empty;
    public string AuthToken { get; set; } = string.Empty;
    
    public ICollection<File>? CreatedFiles { get; set; }
}