namespace InovacaoAguiaBranca.DTOs;

public class CriarEstrategiaRequest
{
    public string Titulo { get; set; } = string.Empty;
    public string Descricao { get; set; } = string.Empty;
    public string Categoria { get; set; } = string.Empty;
    public string Campanha { get; set; } = string.Empty;
    public string Pilar { get; set; } = string.Empty;
    public int Prioridade { get; set; } = 1;
}

public class AtualizarEstrategiaRequest
{
    public string? Titulo { get; set; }
    public string? Descricao { get; set; }
    public string? Categoria { get; set; }
    public string? Campanha { get; set; }
    public string? Pilar { get; set; }
    public int? Prioridade { get; set; }
    public bool? Ativa { get; set; }
}
