using InovacaoAguiaBranca.Configurations;
using InovacaoAguiaBranca.Models;
using Microsoft.Extensions.Options;
using MongoDB.Driver;

namespace InovacaoAguiaBranca.Repositories;

public class MongoDbContext
{
    private readonly IMongoDatabase? _database;
    private readonly bool _isConnected = false;

    public MongoDbContext(IOptions<MongoDbSettings> settings)
    {
        var envConnStr = Environment.GetEnvironmentVariable("MONGODB_URI");
        var envDbName = Environment.GetEnvironmentVariable("MONGODB_DATABASE");

        var connStr = !string.IsNullOrWhiteSpace(envConnStr) ? envConnStr : settings.Value.ConnectionString;
        var dbName = !string.IsNullOrWhiteSpace(envDbName) ? envDbName : settings.Value.DatabaseName;

        if (!string.IsNullOrWhiteSpace(connStr))
        {
            try
            {
                var client = new MongoClient(connStr);
                _database = client.GetDatabase(string.IsNullOrWhiteSpace(dbName) ? "InovacaoAguiaBrancaDB" : dbName);
                _isConnected = true;
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[MongoDB Warning] Não foi possível conectar ao MongoDB Atlas: {ex.Message}");
            }
        }
        else
        {
            Console.WriteLine("[MongoDB Info] String de conexão vazia. Configure appsettings.json ou MONGODB_URI com sua URL do MongoDB Atlas.");
        }
    }

    public bool IsConnected => _isConnected && _database != null;

    public IMongoCollection<Usuario>? Usuarios => _database?.GetCollection<Usuario>("Usuarios");
    public IMongoCollection<Estrategia>? Estrategias => _database?.GetCollection<Estrategia>("Estrategias");
    public IMongoCollection<Ideia>? Ideias => _database?.GetCollection<Ideia>("Ideias");
    public IMongoCollection<Projeto>? Projetos => _database?.GetCollection<Projeto>("Projetos");
}
