using InovacaoAguiaBranca.DTOs;
using InovacaoAguiaBranca.Models;
using InovacaoAguiaBranca.Repositories;
using MongoDB.Driver;

namespace InovacaoAguiaBranca.Services;

public class ProjetoService
{
    private readonly MongoDbContext _context;
    private readonly EstrategiaService _estrategiaService;
    private static readonly List<Projeto> _inMemoryProjetos = new();

    public ProjetoService(MongoDbContext context, EstrategiaService estrategiaService)
    {
        _context = context;
        _estrategiaService = estrategiaService;
        InicializarProjetosPadrao();
    }

    private void InicializarProjetosPadrao()
    {
        if (_inMemoryProjetos.Count == 0)
        {
            _inMemoryProjetos.AddRange(new[]
            {
                new Projeto
                {
                    Id = "66db5e440000000000000001",
                    Titulo = "Piloto de Telemetria e Pressão de Pneus em Tempo Real",
                    Descricao = "Instalação do sistema em 40 ônibus da linha Vitória x Belo Horizonte para validação dos ganhos de combustível.",
                    IdeiaOrigemId = "66db5e330000000000000001",
                    EstrategiaId = "66db5e220000000000000001",
                    EstrategiaTitulo = "Eficiência Energética e Descarbonização da Frota",
                    Responsavel = "Mariana Gestora",
                    Equipe = new() { "Mariana Gestora", "Carlos Operador", "Eng. Marcos" },
                    Status = "EmAndamento",
                    Etapa = "Piloto",
                    Progresso = 65.0,
                    Investimento = 85000.0,
                    Roi = 280.0,
                    LucroObtido = 140000.0,
                    ReducaoCustos = 95000.0,
                    GanhoProdutividade = 18.5,
                    DataInicio = DateTime.UtcNow.AddMonths(-2),
                    DataPrevisao = DateTime.UtcNow.AddMonths(1)
                },
                new Projeto
                {
                    Id = "66db5e440000000000000002",
                    Titulo = "Modernização Digital do Atendimento Rodoviário",
                    Descricao = "Reformulação do sistema de autoatendimento e check-in digital.",
                    EstrategiaId = "66db5e220000000000000002",
                    EstrategiaTitulo = "Experiência Digital do Passageiro",
                    Responsavel = "Mariana Gestora",
                    Equipe = new() { "Mariana Gestora", "Dev Team" },
                    Status = "Planejamento",
                    Etapa = "Ideação",
                    Progresso = 25.0,
                    Investimento = 40000.0,
                    Roi = 150.0,
                    ReducaoCustos = 30000.0,
                    GanhoProdutividade = 12.0,
                    DataInicio = DateTime.UtcNow.AddDays(-20),
                    DataPrevisao = DateTime.UtcNow.AddMonths(4)
                }
            });
        }
    }

    public async Task<List<Projeto>> ObterTodosAsync(string? status = null, string? estrategiaId = null)
    {
        if (_context.IsConnected && _context.Projetos != null)
        {
            try
            {
                var builder = Builders<Projeto>.Filter;
                var filters = new List<FilterDefinition<Projeto>>();

                if (!string.IsNullOrWhiteSpace(status))
                    filters.Add(builder.Eq(p => p.Status, status));
                if (!string.IsNullOrWhiteSpace(estrategiaId))
                    filters.Add(builder.Eq(p => p.EstrategiaId, estrategiaId));

                var finalFilter = filters.Count > 0 ? builder.And(filters) : builder.Empty;
                var lista = await _context.Projetos.Find(finalFilter).ToListAsync();
                if (lista.Count > 0) return lista;
            }
            catch { }
        }

        var query = _inMemoryProjetos.AsQueryable();
        if (!string.IsNullOrWhiteSpace(status))
            query = query.Where(p => p.Status.Equals(status, StringComparison.OrdinalIgnoreCase));
        if (!string.IsNullOrWhiteSpace(estrategiaId))
            query = query.Where(p => p.EstrategiaId == estrategiaId);

        return query.OrderByDescending(p => p.DataInicio).ToList();
    }

    public async Task<Projeto?> ObterPorIdAsync(string id)
    {
        if (_context.IsConnected && _context.Projetos != null)
        {
            try
            {
                var item = await _context.Projetos.Find(p => p.Id == id).FirstOrDefaultAsync();
                if (item != null) return item;
            }
            catch { }
        }
        return _inMemoryProjetos.FirstOrDefault(p => p.Id == id);
    }

    public async Task<Projeto> CriarAsync(CriarProjetoRequest request)
    {
        string? estrategiaTitulo = null;
        if (!string.IsNullOrWhiteSpace(request.EstrategiaId))
        {
            var estrategia = await _estrategiaService.ObterPorIdAsync(request.EstrategiaId);
            estrategiaTitulo = estrategia?.Titulo;
        }

        var projeto = new Projeto
        {
            Id = MongoDB.Bson.ObjectId.GenerateNewId().ToString(),
            Titulo = request.Titulo,
            Descricao = request.Descricao,
            IdeiaOrigemId = request.IdeiaOrigemId,
            EstrategiaId = request.EstrategiaId,
            EstrategiaTitulo = estrategiaTitulo,
            Responsavel = request.Responsavel,
            Equipe = request.Equipe ?? new(),
            Status = "Planejamento",
            Etapa = request.Etapa,
            Progresso = 0.0,
            Investimento = request.Investimento,
            DataInicio = DateTime.UtcNow,
            DataPrevisao = request.DataPrevisao ?? DateTime.UtcNow.AddMonths(3)
        };

        if (_context.IsConnected && _context.Projetos != null)
        {
            try
            {
                await _context.Projetos.InsertOneAsync(projeto);
            }
            catch { }
        }

        _inMemoryProjetos.Add(projeto);
        return projeto;
    }

    public async Task<Projeto?> AtualizarAsync(string id, AtualizarProjetoRequest request)
    {
        var projeto = await ObterPorIdAsync(id);
        if (projeto == null) return null;

        if (request.Titulo != null) projeto.Titulo = request.Titulo;
        if (request.Descricao != null) projeto.Descricao = request.Descricao;
        if (request.Responsavel != null) projeto.Responsavel = request.Responsavel;
        if (request.Equipe != null) projeto.Equipe = request.Equipe;
        if (request.Status != null) projeto.Status = request.Status;
        if (request.Etapa != null) projeto.Etapa = request.Etapa;
        if (request.Progresso.HasValue) projeto.Progresso = request.Progresso.Value;
        if (request.Investimento.HasValue) projeto.Investimento = request.Investimento.Value;
        if (request.Roi.HasValue) projeto.Roi = request.Roi.Value;
        if (request.LucroObtido.HasValue) projeto.LucroObtido = request.LucroObtido.Value;
        if (request.ReducaoCustos.HasValue) projeto.ReducaoCustos = request.ReducaoCustos.Value;
        if (request.GanhoProdutividade.HasValue) projeto.GanhoProdutividade = request.GanhoProdutividade.Value;
        if (request.DataPrevisao.HasValue) projeto.DataPrevisao = request.DataPrevisao.Value;
        if (request.DataConclusao.HasValue) projeto.DataConclusao = request.DataConclusao.Value;

        if (_context.IsConnected && _context.Projetos != null)
        {
            try
            {
                await _context.Projetos.ReplaceOneAsync(p => p.Id == id, projeto);
            }
            catch { }
        }

        return projeto;
    }

    public async Task<bool> DeletarAsync(string id)
    {
        var projeto = await ObterPorIdAsync(id);
        if (projeto == null) return false;

        _inMemoryProjetos.Remove(projeto);

        if (_context.IsConnected && _context.Projetos != null)
        {
            try
            {
                await _context.Projetos.DeleteOneAsync(p => p.Id == id);
            }
            catch { }
        }

        return true;
    }
}
