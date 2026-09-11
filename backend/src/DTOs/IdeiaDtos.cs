namespace InovacaoAguiaBranca.DTOs;

public class CriarIdeiaRequest
{
    public string Titulo { get; set; } = string.Empty;
    public string Descricao { get; set; } = string.Empty;
    public string Categoria { get; set; } = string.Empty;
    public string Divisao { get; set; } = "Passageiros";
    public string? EstrategiaId { get; set; }
    public string ImpactoEstimado { get; set; } = "Médio";
}

public class AtualizarIdeiaStatusRequest
{
    public string Status { get; set; } = "EmAvaliacao"; // EmAvaliacao, Aprovada, Reprovada, EmProjeto, Concluida
}

public class AvaliarIaResponse
{
    public string IdeiaId { get; set; } = string.Empty;
    public int AiScore { get; set; }
    public string AiPrioridade { get; set; } = string.Empty;
    public string AiAnalise { get; set; } = string.Empty;
}
