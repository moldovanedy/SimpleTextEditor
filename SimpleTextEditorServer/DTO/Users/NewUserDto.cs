namespace SimpleTextEditorServer.DTO.Users;

public record NewUserDto(
    string Email, 
    string Password);