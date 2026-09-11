using InovacaoAguiaBranca.DTOs;
using InovacaoAguiaBranca.Models;
using InovacaoAguiaBranca.Repositories;
using MongoDB.Driver;

namespace InovacaoAguiaBranca.Services;

public class EstrategiaService
{
    private readonly MongoDbContext _context;
    private static readonly List<Estrategia> _inMemoryEstrategias = new();

    public EstrategiaService(MongoDbContext context)
    {
        _context = context;
        InicializarEstrategiasPadrao();
    }

    private void InicializarEstrategiasPadrao()
    {
        if (_inMemoryEstrategias.Count == 0)
        {
            _inMemoryEstrategias.AddRange(new[]
            {
                new Estrategia
                {
                    Id = "66db5e220000000000000001",
                    Titulo = "Eficiência Energética e Descarbonização da Frota",
                    Descricao = "Otimizar o consumo de combustível e transição para energias limpas nos trajetos intermunicipais.",
                    Categoria = "Sustentabilidade",
                    Campanha = "Inova GAB 2026",
                    Pilar = "Operação Sustentável",
                    Prioridade = 1,
                    Ativa = true,
                    DataCriacao = DateTime.UtcNow.AddDays(-30),
                    CriadoPor = "Diretoria de Operações"
                },
                new Estrategia
                {
                    Id = "66db5e220000000000000002",
                    Titulo = "Experiência Digital do Passageiro",
                    Descricao = "Aprimorar a jornada do cliente desde a compra do bilhete até o pós-viagem nos serviços de passageiros.",
                    Categoria = "Experiência do Cliente",
                    Campanha = "Viagem do Futuro",
                    Pilar = "Excelência ao Cliente",
                    Prioridade = 2,
                    Ativa = true,
                    DataCriacao = DateTime.UtcNow.AddDays(-15),
                    CriadoPor = "Gerência de Inovação"
                },
                new Estrategia
                {
                    Id = "66db5e220000000000000003",
                    Titulo = "Segurança Operacional e Zero Acidentes",
                    Descricao = "Implementação de tecnologias de telemetria e suporte ao motorista para máxima segurança viária.",
                    Categoria = "Segurança",
                    Campanha = "Segurança em Primeiro Lugar",
                    Pilar = "Respeito às Pessoas",
                    Prioridade = 1,
                    Ativa = true,
                    DataCriacao = DateTime.UtcNow.AddDays(-5),
                    CriadoPor = "Comitê de Segurança"
                }
            });
        }
    }

    public async Task<List<Estrategia>> ObterTodasAsync(bool apenasAtivas = false)
    {
        if (_context.IsConnected && _context.Estrategias != null)
        {
            try
            {
                var filtro = apenasAtivas ? Builders<Estrategia>.Filter.Eq(e => e.Ativa, true) : Builders<Estrategia>.Filter.Empty;
                var lista = await _context.Estrategias.Find(filtro).ToListAsync();
                if (lista.Count > 0) return lista;
            }
            catch { }
        }

        return apenasAtivas
            ? _inMemoryEstrategias.Where(e => e.Ativa).OrderBy(e => e.Prioridade).ToList()
            : _inMemoryEstrategias.OrderByDescending(e => e.DataCriacao).ToList();
    }

    public async Task<Estrategia?> ObterPorIdAsync(string id)
    {
        if (_context.IsConnected && _context.Estrategias != null)
        {
            try
            {
                var item = await _context.Estrategias.Find(e => e.Id == id).FirstOrDefaultAsync();
                if (item != null) return item;
            }
            catch { }
        }
        return _inMemoryEstrategias.FirstOrDefault(e => e.Id == id);
    }

    public async Task<Estrategia> CriarAsync(CriarEstrategiaRequest request, string autor)
    {
        var estrategia = new Estrategia
        {
            Id = MongoDB.Bson.ObjectId.GenerateNewId().ToString(),
            Titulo = request.Titulo,
            Descricao = request.Descricao,
            Categoria = request.Categoria,
            Campanha = request.Campanha,
            Pilar = request.Pilar,
            Prioridade = request.Prioridade,
            Ativa = true,
            DataCriacao = DateTime.UtcNow,
            CriadoPor = autor
        };

        if (_context.IsConnected && _context.Estrategias != null)
        {
            try
            {
                await _context.Estrategias.InsertOneAsync(estrategia);
            }
            catch { }
        }

        _inMemoryEstrategias.Add(estrategia);
        return estrategia;
    }

    public async Task<Estrategia?> AtualizarAsync(string id, AtualizarEstrategiaRequest request)
    {
        var estrategia = await ObterPorIdAsync(id);
        if (estrategia == null) return null;

        if (request.Titulo != null) estrategia.Titulo = request.Titulo;
        if (request.Descricao != null) estrategia.Descricao = request.Descricao;
        if (request.Categoria != null) estrategia.Categoria = request.Categoria;
        if (request.Campanha != null) estrategia.Campanha = request.Campanha;
        if (request.Pilar != null) estrategia.Pilar = request.Pilar;
        if (request.Prioridade.HasValue) estrategia.Prioridade = request.Prioridade.Value;
        if (request.Ativa.HasValue) estrategia.Ativa = request.Ativa.Value;

        if (_context.IsConnected && _context.Estrategias != null)
        {
            try
            {
                await _context.Estrategias.ReplaceOneAsync(e => e.Id == id, estrategia);
            }
            catch { }
        }

        return estrategia;
    }

    public async Task<bool> DeletarAsync(string id)
    {
        var item = await ObterPorIdAsync(id);
        if (item == null) return false;

        _inMemoryEstrategias.Remove(item);

        if (_context.IsConnected && _context.Estrategias != null)
        {
            try
            {
                await _context.Estrategias.DeleteOneAsync(e => e.Id == id);
            }
            catch { }
        }

        return true;
    }
}
