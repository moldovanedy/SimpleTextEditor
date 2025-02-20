using System.Linq;
using System.Text.RegularExpressions;

namespace SimpleTextEditorServer.Validations;

public static partial class UserValidations
{
    public static string? ValidateUsername(string userName)
    {
        if (string.IsNullOrWhiteSpace(userName))
        {
            return "Username cannot be empty";
        }

        return userName.Length switch
        {
            < 3 => "Username is too short",
            > 255 => "Username is too long",
            _ => null
        };
    }

    public static string? ValidateEmail(string email)
    {
        MatchCollection matches = EmailRegex().Matches(email);
        if (matches.Count == 0 || matches.Count > 1 || matches[0].Value != email)
        {
            return "Invalid email";
        }

        return null;
    }

    public static string? ValidatePassword(string password)
    {
        switch (password.Length)
        {
            case < 6:
                return "Password is too short";
            case > 64:
                return "Password is too long";
        }

        if (!password.Any(char.IsDigit))
        {
            return "Password must contain at least one digit";
        }

        return password.Any(char.IsUpper) ? null : "Password must contain at least one upper case letter";
    }

    [GeneratedRegex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")]
    private static partial Regex EmailRegex();
}