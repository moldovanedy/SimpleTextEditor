using System;
using System.ComponentModel.DataAnnotations.Schema;

namespace SimpleTextEditorServer.Models;

[Table("Files")]
public class File
{
    public Guid Id { get; set; } = Guid.NewGuid();
    public string Name { get; set; } = string.Empty;
    public long DateCreated { get; set; }
    public long DateModified { get; set; }
    
    public Guid UserId { get; set; } = Guid.Empty;
    public User CreatedBy { get; set; } = null!;
}