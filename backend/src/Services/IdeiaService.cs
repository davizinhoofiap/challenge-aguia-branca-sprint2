using InovacaoAguiaBranca.DTOs;
using InovacaoAguiaBranca.Models;
using InovacaoAguiaBranca.Repositories;
using MongoDB.Driver;

namespace InovacaoAguiaBranca.Services;

public class IdeiaService
{
    private readonly MongoDbContext _context;
    private readonly EstrategiaService _estrategiaService;
    private readonly GeminiAiService _aiService;
    private static readonly List<Ideia> _inMemoryIdeias = new();

    public IdeiaService(MongoDbContext context, EstrategiaService estrategiaService, GeminiAiService aiService)
    {
        _context = context;
        _estrategiaService = estrategiaService;
        _aiService = aiService;
        InicializarIdeiasPadrao();
    }

    private void InicializarIdeiasPadrao()
    {
        if (_inMemoryIdeias.Count == 0)
        {
            _inMemoryIdeias.AddRange(new[]
            {
                new Ideia
                {
                    Id = "66db5e330000000000000001",
                    Titulo = "Sensor de Calibragem Automática e Desgaste de Pneus",
                    Descricao = "Instalar sensores IoT nos eixos dos ônibus rodoviários para monitoramento contínuo da pressão e temperatura, reduzindo consumo de diesel e evitando estouros na rodovia.",
                    Categoria = "Eficiência e Manutenção",
                    AutorId = "66db5e110000000000000001",
                    AutorNome = "Carlos Operador",
                    Divisao = "Passageiros",
                    Status = "Aprovada",
                    EstrategiaId = "66db5e220000000000000001",
                    EstrategiaTitulo = "Eficiência Energética e Descarbonização da Frota",
                    ImpactoEstimado = "Alto",
                    Votos = 18,
                    Comentarios = 4,
                    DataCriacao = DateTime.UtcNow.AddDays(-10),
                    AiScore = 92,
                    AiPrioridade = "Alta",
                    AiAnalise = "Ideia com altíssimo retorno econômico direto em redução de consumo de combustível e segurança nas viagens do GAB.",
                    DataAvaliacaoIa = DateTime.UtcNow.AddDays(-10)
                },
                new Ideia
                {
                    Id = "66db5e330000000000000002",
                    Titulo = "Embarque por Reconhecimento Facial nas Rodoviárias",
                    Descricao = "Substituir a checagem manual de passagens por totens biométricos de embarque rápido nas plataformas de Vitória e Rio de Janeiro.",
                    Categoria = "Experiência do Cliente",
                    AutorId = "66db5e110000000000000001",
                    AutorNome = "Carlos Operador",
                    Divisao = "Passageiros",
                    Status = "Capturada",
                    EstrategiaId = "66db5e220000000000000002",
                    EstrategiaTitulo = "Experiência Digital do Passageiro",
                    ImpactoEstimado = "Médio",
                    Votos = 11,
                    Comentarios = 2,
                    DataCriacao = DateTime.UtcNow.AddDays(-3),
                    AiScore = 84,
                    AiPrioridade = "Alta",
                    AiAnalise = "Agiliza o fluxo de embarque e reduz filas nas rodoviárias centrais, modernizando a percepção de valor da marca.",
                    DataAvaliacaoIa = DateTime.UtcNow.AddDays(-3)
                }
            });
        }
    }

    public async Task<List<Ideia>> ObterTodasAsync(string? autorId = null, string? status = null, string? estrategiaId = null)
    {
        if (_context.IsConnected && _context.Ideias != null)
        {
            try
            {
                var builder = Builders<Ideia>.Filter;
                var filters = new List<FilterDefinition<Ideia>>();

                if (!string.IsNullOrWhiteSpace(autorId))
                    filters.Add(builder.Eq(i => i.AutorId, autorId));
                if (!string.IsNullOrWhiteSpace(status))
                    filters.Add(builder.Eq(i => i.Status, status));
                if (!string.IsNullOrWhiteSpace(estrategiaId))
                    filters.Add(builder.Eq(i => i.EstrategiaId, estrategiaId));

                var finalFilter = filters.Count > 0 ? builder.And(filters) : builder.Empty;
                var lista = await _context.Ideias.Find(finalFilter).SortByDescending(i => i.DataCriacao).ToListAsync();
                if (lista.Count > 0) return lista;
            }
            catch { }
        }

        var query = _inMemoryIdeias.AsQueryable();
        if (!string.IsNullOrWhiteSpace(autorId))
            query = query.Where(i => i.AutorId == autorId);
        if (!string.IsNullOrWhiteSpace(status))
            query = query.Where(i => i.Status.Equals(status, StringComparison.OrdinalIgnoreCase));
        if (!string.IsNullOrWhiteSpace(estrategiaId))
            query = query.Where(i => i.EstrategiaId == estrategiaId);

        return query.OrderByDescending(i => i.AiScore).ThenByDescending(i => i.DataCriacao).ToList();
    }

    public async Task<Ideia?> ObterPorIdAsync(string id)
    {
        if (_context.IsConnected && _context.Ideias != null)
        {
            try
            {
                var item = await _context.Ideias.Find(i => i.Id == id).FirstOrDefaultAsync();
                if (item != null) return item;
            }
            catch { }
        }
        return _inMemoryIdeias.FirstOrDefault(i => i.Id == id);
    }

    public async Task<Ideia> CriarAsync(CriarIdeiaRequest request, Usuario autor)
    {
        string? estrategiaTitulo = null;
        if (!string.IsNullOrWhiteSpace(request.EstrategiaId))
        {
            var estrategia = await _estrategiaService.ObterPorIdAsync(request.EstrategiaId);
            estrategiaTitulo = estrategia?.Titulo;
        }

        var ideia = new Ideia
        {
            Id = MongoDB.Bson.ObjectId.GenerateNewId().ToString(),
            Titulo = request.Titulo,
            Descricao = request.Descricao,
            Categoria = request.Categoria,
            AutorId = autor.Id ?? Guid.NewGuid().ToString(),
            AutorNome = autor.Nome,
            Divisao = request.Divisao,
            Status = "Capturada",
            EstrategiaId = request.EstrategiaId,
            EstrategiaTitulo = estrategiaTitulo,
            ImpactoEstimado = request.ImpactoEstimado,
            DataCriacao = DateTime.UtcNow
        };

        // Avaliação inteligente imediata da IA (Score e Priorização)
        var avaliacaoIa = await _aiService.AvaliarIdeiaAsync(ideia.Id, ideia.Titulo, ideia.Descricao, ideia.Categoria, estrategiaTitulo);
        ideia.AiScore = avaliacaoIa.AiScore;
        ideia.AiPrioridade = avaliacaoIa.AiPrioridade;
        ideia.AiAnalise = avaliacaoIa.AiAnalise;
        ideia.DataAvaliacaoIa = DateTime.UtcNow;

        if (_context.IsConnected && _context.Ideias != null)
        {
            try
            {
                await _context.Ideias.InsertOneAsync(ideia);
            }
            catch { }
        }

        _inMemoryIdeias.Add(ideia);
        return ideia;
    }

    public async Task<Ideia?> AtualizarStatusAsync(string id, string novoStatus)
    {
        var ideia = await ObterPorIdAsync(id);
        if (ideia == null) return null;

        ideia.Status = novoStatus;

        if (_context.IsConnected && _context.Ideias != null)
        {
            try
            {
                await _context.Ideias.ReplaceOneAsync(i => i.Id == id, ideia);
            }
            catch { }
        }

        return ideia;
    }

    public async Task<AvaliarIaResponse?> ReavaliarComIaAsync(string id)
    {
        var ideia = await ObterPorIdAsync(id);
        if (ideia == null) return null;

        var avaliacao = await _aiService.AvaliarIdeiaAsync(ideia.Id!, ideia.Titulo, ideia.Descricao, ideia.Categoria, ideia.EstrategiaTitulo);
        ideia.AiScore = avaliacao.AiScore;
        ideia.AiPrioridade = avaliacao.AiPrioridade;
        ideia.AiAnalise = avaliacao.AiAnalise;
        ideia.DataAvaliacaoIa = DateTime.UtcNow;

        if (_context.IsConnected && _context.Ideias != null)
        {
            try
            {
                await _context.Ideias.ReplaceOneAsync(i => i.Id == id, ideia);
            }
            catch { }
        }

        return avaliacao;
    }

    public async Task<bool> DeletarAsync(string id, string autorId, string perfil)
    {
        var ideia = await ObterPorIdAsync(id);
        if (ideia == null) return false;

        // Apenas o próprio autor ou Líder/Gestor pode deletar
        if (perfil == "Operador" && ideia.AutorId != autorId)
            return false;

        _inMemoryIdeias.Remove(ideia);

        if (_context.IsConnected && _context.Ideias != null)
        {
            try
            {
                await _context.Ideias.DeleteOneAsync(i => i.Id == id);
            }
            catch { }
        }

        return true;
    }
}
