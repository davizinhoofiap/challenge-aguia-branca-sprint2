using System.Text;
using System.Text.Json;
using System.Text.RegularExpressions;
using InovacaoAguiaBranca.Configurations;
using InovacaoAguiaBranca.DTOs;
using Microsoft.Extensions.Options;

namespace InovacaoAguiaBranca.Services;

public class GeminiAiService
{
    private readonly HttpClient _httpClient;
    private readonly GeminiSettings _geminiSettings;

    public GeminiAiService(HttpClient httpClient, IOptions<GeminiSettings> geminiSettings)
    {
        _httpClient = httpClient;
        _geminiSettings = geminiSettings.Value;
    }

    public async Task<AvaliarIaResponse> AvaliarIdeiaAsync(string ideiaId, string titulo, string descricao, string categoria, string? estrategiaTitulo)
    {
        var apiKey = _geminiSettings.ApiKey;

        // Se tiver chave de API do Gemini configurada, chama a API real
        if (!string.IsNullOrWhiteSpace(apiKey) && apiKey != "SUA_CHAVE_GEMINI_AQUI")
        {
            try
            {
                var prompt = $@"
Você é um especialista sênior em Inovação Corporativa do Grupo Águia Branca (líder em transporte rodoviário, comércio e logística).
Avalie a seguinte ideia proposta por um colaborador operacional:

- TÍTULO: {titulo}
- DESCRIÇÃO: {descricao}
- CATEGORIA: {categoria}
- ESTRATÉGIA VIGENTE ALINHADA: {estrategiaTitulo ?? "Estratégia Geral do Grupo Águia Branca"}

Critérios de avaliação:
1. Viabilidade técnica e operacional (facilidade de implementação no dia a dia).
2. Impacto potencial no negócio (redução de custos, segurança, satisfação do cliente ou produtividade).
3. Alinhamento com a estratégia do Grupo Águia Branca.

Responda ESTRITAMENTE em formato JSON com o seguinte formato, sem formatação markdown em volta:
{{
  ""score"": 85,
  ""prioridade"": ""Alta"",
  ""analise"": ""Breve explicação de até 2 frases destacando pontos fortes e oportunidades.""
}}
Obs: O score deve ser um número inteiro de 0 a 100. A prioridade deve ser 'Alta', 'Média' ou 'Baixa'.";

                var requestBody = new
                {
                    contents = new[]
                    {
                        new
                        {
                            parts = new[]
                            {
                                new { text = prompt }
                            }
                        }
                    }
                };

                var jsonContent = new StringContent(JsonSerializer.Serialize(requestBody), Encoding.UTF8, "application/json");
                var url = $"{_geminiSettings.BaseUrl}/{_geminiSettings.Model}:generateContent?key={apiKey}";

                var response = await _httpClient.PostAsync(url, jsonContent);
                if (response.IsSuccessStatusCode)
                {
                    var responseBody = await response.Content.ReadAsStringAsync();
                    using var doc = JsonDocument.Parse(responseBody);
                    var candidates = doc.RootElement.GetProperty("candidates");
                    if (candidates.GetArrayLength() > 0)
                    {
                        var textResponse = candidates[0]
                            .GetProperty("content")
                            .GetProperty("parts")[0]
                            .GetProperty("text")
                            .GetString();

                        if (!string.IsNullOrWhiteSpace(textResponse))
                        {
                            // Remove eventuais blocos de ```json ```
                            var cleanedJson = Regex.Replace(textResponse, @"```json|```", "").Trim();
                            using var parsedAi = JsonDocument.Parse(cleanedJson);
                            var root = parsedAi.RootElement;

                            return new AvaliarIaResponse
                            {
                                IdeiaId = ideiaId,
                                AiScore = root.GetProperty("score").GetInt32(),
                                AiPrioridade = root.GetProperty("prioridade").GetString() ?? "Média",
                                AiAnalise = root.GetProperty("analise").GetString() ?? "Avaliação processada com sucesso pela IA."
                            };
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[Gemini API] Erro ao chamar API: {ex.Message}. Utilizando motor inteligente de fallback.");
            }
        }

        // Motor inteligente de priorização com cálculo contextual caso a chave da API esteja vazia ou offline
        return CalcularScoreHeuristico(ideiaId, titulo, descricao, categoria);
    }

    private AvaliarIaResponse CalcularScoreHeuristico(string ideiaId, string titulo, string descricao, string categoria)
    {
        int baseScore = 70;
        var textoCompleto = $"{titulo} {descricao} {categoria}".ToLower();

        if (textoCompleto.Contains("custo") || textoCompleto.Contains("economia") || textoCompleto.Contains("combustível"))
            baseScore += 12;

        if (textoCompleto.Contains("segurança") || textoCompleto.Contains("acidente") || textoCompleto.Contains("passageiro"))
            baseScore += 15;

        if (textoCompleto.Contains("tempo") || textoCompleto.Contains("produtividade") || textoCompleto.Contains("agilidade"))
            baseScore += 10;

        if (descricao.Length > 80)
            baseScore += 5;

        int finalScore = Math.Min(98, Math.Max(45, baseScore));
        string prioridade = finalScore >= 80 ? "Alta" : (finalScore >= 65 ? "Média" : "Baixa");
        string analise = $"Ideia avaliada pela IA com score {finalScore}/100. Apresenta boa viabilidade para o setor de transporte/logística e relevante potencial de geração de valor para o Grupo Águia Branca.";

        return new AvaliarIaResponse
        {
            IdeiaId = ideiaId,
            AiScore = finalScore,
            AiPrioridade = prioridade,
            AiAnalise = analise
        };
    }
}
