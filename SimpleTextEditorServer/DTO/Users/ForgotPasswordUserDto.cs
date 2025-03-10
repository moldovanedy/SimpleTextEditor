namespace SimpleTextEditorServer.DTO.Users;

public record ForgotPasswordUserDto(
    string Email, 
    string NewPassword);