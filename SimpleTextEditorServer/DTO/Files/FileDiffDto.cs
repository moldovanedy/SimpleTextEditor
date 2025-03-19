namespace SimpleTextEditorServer.DTO.Files;

public record FileDiffDto(string TextChange, bool IsAdded, int Index);