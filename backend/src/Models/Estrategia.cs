using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;

namespace InovacaoAguiaBranca.Models;

public class Estrategia
{
    [BsonId]
    [BsonRepresentation(BsonType.ObjectId)]
    public string? Id { get; set; }

    [BsonElement("titulo")]
    public string Titulo { get; set; } = string.Empty;

    [BsonElement("descricao")]
    public string Descricao { get; set; } = string.Empty;

    [BsonElement("categoria")]
    public string Categoria { get; set; } = string.Empty; // ex: Eficiência Operacional, Experiência do Cliente, Sustentabilidade

    [BsonElement("campanha")]
    public string Campanha { get; set; } = string.Empty; // ex: Inova GAB 2026

    [BsonElement("pilar")]
    public string Pilar { get; set; } = string.Empty;

    [BsonElement("prioridade")]
    public int Prioridade { get; set; } = 1;

    [BsonElement("ativa")]
    public bool Ativa { get; set; } = true;

    [BsonElement("dataCriacao")]
    public DateTime DataCriacao { get; set; } = DateTime.UtcNow;

    [BsonElement("criadoPor")]
    public string CriadoPor { get; set; } = string.Empty;
}
