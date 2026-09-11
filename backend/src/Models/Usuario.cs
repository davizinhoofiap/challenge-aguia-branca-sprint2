using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;

namespace InovacaoAguiaBranca.Models;

public class Usuario
{
    [BsonId]
    [BsonRepresentation(BsonType.ObjectId)]
    public string? Id { get; set; }

    [BsonElement("nome")]
    public string Nome { get; set; } = string.Empty;

    [BsonElement("email")]
    public string Email { get; set; } = string.Empty;

    [BsonElement("senhaHash")]
    public string SenhaHash { get; set; } = string.Empty;

    [BsonElement("cargo")]
    public string Cargo { get; set; } = string.Empty;

    [BsonElement("divisao")]
    public string Divisao { get; set; } = "Passageiros"; // Passageiros, Comércio, Logística

    [BsonElement("perfil")]
    public string Perfil { get; set; } = "Operador"; // Operador, Gestor, Lider

    [BsonElement("dataCriacao")]
    public DateTime DataCriacao { get; set; } = DateTime.UtcNow;
}
