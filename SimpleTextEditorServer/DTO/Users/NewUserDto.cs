namespace SimpleTextEditorServer.DTO.Users;

public record NewUserDto(
    string Username, 
    string Email, 
    string Password);