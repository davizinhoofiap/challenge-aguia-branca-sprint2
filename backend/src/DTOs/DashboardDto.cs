namespace InovacaoAguiaBranca.DTOs;

public class MetricaItemDto
{
    public string Label { get; set; } = string.Empty;
    public string Valor { get; set; } = string.Empty;
    public string Variacao { get; set; } = string.Empty;
    public bool Positivo { get; set; } = true;
}

public class RetornoPorEstrategiaDto
{
    public string EstrategiaId { get; set; } = string.Empty;
    public string Titulo { get; set; } = string.Empty;
    public int QuantidadeProjetos { get; set; }
    public double TotalInvestimento { get; set; }
    public double TotalEconomia { get; set; }
    public double RoiMedio { get; set; }
}

public class DashboardSummaryResponse
{
    public int TotalIdeias { get; set; }
    public int IdeiasAprovadas { get; set; }
    public int TotalProjetos { get; set; }
    public int ProjetosAtivos { get; set; }
    public int ProjetosConcluidos { get; set; }
    public double TotalInvestido { get; set; }
    public double TotalEconomiaGerada { get; set; }
    public double TotalLucroObtido { get; set; }
    public double RoiMedioPercentual { get; set; }
    public double GanhoProdutividadeMedio { get; set; }
    public List<MetricaItemDto> Metricas { get; set; } = new();
    public List<RetornoPorEstrategiaDto> RetornoPorEstrategia { get; set; } = new();
}
