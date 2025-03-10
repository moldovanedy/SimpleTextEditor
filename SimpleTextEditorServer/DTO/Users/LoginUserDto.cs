namespace SimpleTextEditorServer.DTO.Users;

public record LoginUserDto(
    string Email, 
    string Password);