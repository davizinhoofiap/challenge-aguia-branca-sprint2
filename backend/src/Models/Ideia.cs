using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;

namespace InovacaoAguiaBranca.Models;

public class Ideia
{
    [BsonId]
    [BsonRepresentation(BsonType.ObjectId)]
    public string? Id { get; set; }

    [BsonElement("titulo")]
    public string Titulo { get; set; } = string.Empty;

    [BsonElement("descricao")]
    public string Descricao { get; set; } = string.Empty;

    [BsonElement("categoria")]
    public string Categoria { get; set; } = string.Empty;

    [BsonElement("autorId")]
    public string AutorId { get; set; } = string.Empty;

    [BsonElement("autorNome")]
    public string AutorNome { get; set; } = string.Empty;

    [BsonElement("divisao")]
    public string Divisao { get; set; } = "Passageiros";

    [BsonElement("status")]
    public string Status { get; set; } = "Capturada"; // Capturada, EmAvaliacao, Aprovada, Reprovada, EmProjeto, Concluida

    [BsonElement("estrategiaId")]
    public string? EstrategiaId { get; set; }

    [BsonElement("estrategiaTitulo")]
    public string? EstrategiaTitulo { get; set; }

    [BsonElement("impactoEstimado")]
    public string ImpactoEstimado { get; set; } = "Médio"; // Alto, Médio, Baixo

    [BsonElement("votos")]
    public int Votos { get; set; } = 0;

    [BsonElement("comentarios")]
    public int Comentarios { get; set; } = 0;

    [BsonElement("dataCriacao")]
    public DateTime DataCriacao { get; set; } = DateTime.UtcNow;

    // Campos enriquecidos com Inteligência Artificial (Gemini API)
    [BsonElement("aiScore")]
    public int AiScore { get; set; } = 0; // 0 a 100

    [BsonElement("aiPrioridade")]
    public string AiPrioridade { get; set; } = "Não Avaliado"; // Alta, Média, Baixa

    [BsonElement("aiAnalise")]
    public string AiAnalise { get; set; } = string.Empty;

    [BsonElement("dataAvaliacaoIa")]
    public DateTime? DataAvaliacaoIa { get; set; }
}
