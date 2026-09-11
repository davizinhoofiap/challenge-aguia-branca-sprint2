using InovacaoAguiaBranca.DTOs;

namespace InovacaoAguiaBranca.Services;

public class DashboardService
{
    private readonly IdeiaService _ideiaService;
    private readonly ProjetoService _projetoService;
    private readonly EstrategiaService _estrategiaService;

    public DashboardService(IdeiaService ideiaService, ProjetoService projetoService, EstrategiaService estrategiaService)
    {
        _ideiaService = ideiaService;
        _projetoService = projetoService;
        _estrategiaService = estrategiaService;
    }

    public async Task<DashboardSummaryResponse> ObterResumoExecutivoAsync()
    {
        var ideias = await _ideiaService.ObterTodasAsync();
        var projetos = await _projetoService.ObterTodosAsync();
        var estrategias = await _estrategiaService.ObterTodasAsync();

        int totalIdeias = ideias.Count;
        int ideiasAprovadas = ideias.Count(i => i.Status == "Aprovada" || i.Status == "EmProjeto" || i.Status == "Concluida");
        int totalProjetos = projetos.Count;
        int projetosAtivos = projetos.Count(p => p.Status == "EmAndamento" || p.Status == "Planejamento");
        int projetosConcluidos = projetos.Count(p => p.Status == "Concluido");

        double totalInvestido = projetos.Sum(p => p.Investimento);
        double totalEconomia = projetos.Sum(p => p.ReducaoCustos ?? 0);
        double totalLucro = projetos.Sum(p => p.LucroObtido ?? 0);

        var projetosComRoi = projetos.Where(p => p.Roi.HasValue).ToList();
        double roiMedio = projetosComRoi.Count > 0 ? projetosComRoi.Average(p => p.Roi!.Value) : 0.0;

        var projetosComProdutividade = projetos.Where(p => p.GanhoProdutividade.HasValue).ToList();
        double produtividadeMedia = projetosComProdutividade.Count > 0 ? projetosComProdutividade.Average(p => p.GanhoProdutividade!.Value) : 0.0;

        var retornoPorEstrategia = estrategias.Select(e =>
        {
            var projetosDaEstrategia = projetos.Where(p => p.EstrategiaId == e.Id).ToList();
            var roiProjetos = projetosDaEstrategia.Where(p => p.Roi.HasValue).ToList();

            return new RetornoPorEstrategiaDto
            {
                EstrategiaId = e.Id ?? string.Empty,
                Titulo = e.Titulo,
                QuantidadeProjetos = projetosDaEstrategia.Count,
                TotalInvestimento = projetosDaEstrategia.Sum(p => p.Investimento),
                TotalEconomia = projetosDaEstrategia.Sum(p => p.ReducaoCustos ?? 0),
                RoiMedio = roiProjetos.Count > 0 ? Math.Round(roiProjetos.Average(p => p.Roi!.Value), 1) : 0.0
            };
        }).ToList();

        var metricas = new List<MetricaItemDto>
        {
            new() { Label = "ROI Médio", Valor = $"{roiMedio:F1}%", Variacao = "+18% vs trimestre anterior", Positivo = true },
            new() { Label = "Economia Operacional", Valor = $"R$ {totalEconomia:N0}", Variacao = "+24% redução de custos", Positivo = true },
            new() { Label = "Lucro Gerado", Valor = $"R$ {totalLucro:N0}", Variacao = "+32% novas receitas", Positivo = true },
            new() { Label = "Aumento de Produtividade", Valor = $"{produtividadeMedia:F1}%", Variacao = "+5.4% no ano", Positivo = true },
            new() { Label = "Taxa de Conversão de Ideias", Valor = totalIdeias > 0 ? $"{(double)ideiasAprovadas / totalIdeias * 100:F0}%" : "0%", Variacao = "Triagem acelerada com IA", Positivo = true }
        };

        return new DashboardSummaryResponse
        {
            TotalIdeias = totalIdeias,
            IdeiasAprovadas = ideiasAprovadas,
            TotalProjetos = totalProjetos,
            ProjetosAtivos = projetosAtivos,
            ProjetosConcluidos = projetosConcluidos,
            TotalInvestido = totalInvestido,
            TotalEconomiaGerada = totalEconomia,
            TotalLucroObtido = totalLucro,
            RoiMedioPercentual = Math.Round(roiMedio, 1),
            GanhoProdutividadeMedio = Math.Round(produtividadeMedia, 1),
            Metricas = metricas,
            RetornoPorEstrategia = retornoPorEstrategia
        };
    }
}
