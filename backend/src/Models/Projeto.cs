using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;

namespace InovacaoAguiaBranca.Models;

public class Projeto
{
    [BsonId]
    [BsonRepresentation(BsonType.ObjectId)]
    public string? Id { get; set; }

    [BsonElement("titulo")]
    public string Titulo { get; set; } = string.Empty;

    [BsonElement("descricao")]
    public string Descricao { get; set; } = string.Empty;

    [BsonElement("ideiaOrigemId")]
    public string? IdeiaOrigemId { get; set; }

    [BsonElement("estrategiaId")]
    public string? EstrategiaId { get; set; }

    [BsonElement("estrategiaTitulo")]
    public string? EstrategiaTitulo { get; set; }

    [BsonElement("responsavel")]
    public string Responsavel { get; set; } = string.Empty;

    [BsonElement("equipe")]
    public List<string> Equipe { get; set; } = new();

    [BsonElement("status")]
    public string Status { get; set; } = "Planejamento"; // Planejamento, EmAndamento, Concluido, Pausado

    [BsonElement("etapa")]
    public string Etapa { get; set; } = "Ideação"; // Ideação, Prototipação, Piloto, Escala

    [BsonElement("progresso")]
    public double Progresso { get; set; } = 0.0; // 0 a 100%

    [BsonElement("investimento")]
    public double Investimento { get; set; } = 0.0;

    [BsonElement("roi")]
    public double? Roi { get; set; }

    [BsonElement("lucroObtido")]
    public double? LucroObtido { get; set; }

    [BsonElement("reducaoCustos")]
    public double? ReducaoCustos { get; set; }

    [BsonElement("ganhoProdutividade")]
    public double? GanhoProdutividade { get; set; }

    [BsonElement("dataInicio")]
    public DateTime DataInicio { get; set; } = DateTime.UtcNow;

    [BsonElement("dataPrevisao")]
    public DateTime DataPrevisao { get; set; } = DateTime.UtcNow.AddMonths(3);

    [BsonElement("dataConclusao")]
    public DateTime? DataConclusao { get; set; }
}
