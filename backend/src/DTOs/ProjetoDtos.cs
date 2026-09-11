namespace InovacaoAguiaBranca.DTOs;

public class CriarProjetoRequest
{
    public string Titulo { get; set; } = string.Empty;
    public string Descricao { get; set; } = string.Empty;
    public string? IdeiaOrigemId { get; set; }
    public string? EstrategiaId { get; set; }
    public string Responsavel { get; set; } = string.Empty;
    public List<string> Equipe { get; set; } = new();
    public string Etapa { get; set; } = "Ideação";
    public double Investimento { get; set; } = 0.0;
    public DateTime? DataPrevisao { get; set; }
}

public class AtualizarProjetoRequest
{
    public string? Titulo { get; set; }
    public string? Descricao { get; set; }
    public string? Responsavel { get; set; }
    public List<string>? Equipe { get; set; }
    public string? Status { get; set; } // Planejamento, EmAndamento, Concluido, Pausado
    public string? Etapa { get; set; } // Ideação, Prototipação, Piloto, Escala
    public double? Progresso { get; set; }
    public double? Investimento { get; set; }
    public double? Roi { get; set; }
    public double? LucroObtido { get; set; }
    public double? ReducaoCustos { get; set; }
    public double? GanhoProdutividade { get; set; }
    public DateTime? DataPrevisao { get; set; }
    public DateTime? DataConclusao { get; set; }
}
