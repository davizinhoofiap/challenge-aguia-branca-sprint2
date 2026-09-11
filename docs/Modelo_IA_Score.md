# Funcionalidade Diferencial de IA: Score e Priorização Inteligente de Ideias

> **Atendimento ao Requisito Plus do Edital:**  
> *"Diferencial – IA (Plus): Integração com IA para pontuação e priorização das iniciativas/ideias de inovação dos colaboradores, auxiliando na seleção dos futuros projetos - automatização."*

---

## 1. Modelo de IA Utilizado
- **Modelo:** Google Gemini (`gemini-1.5-flash`)
- **Provedor:** Google AI Studio / Google Generative Language API
- **Formato de Integração:** Requisições REST JSON autenticadas por API Key via `HttpClient` no backend .NET 8 (`GeminiAiService.cs`).

### Por que o `gemini-1.5-flash`?
1. **Baixa latência:** Respostas em menos de 1 segundo, permitindo que o colaborador receba o feedback do score imediatamente após submeter sua ideia no aplicativo.
2. **Capacidade semântica multimodal e contextual:** O modelo consegue interpretar nuances operacionais do setor de transporte de passageiros, logística e manutenção de frotas rodoviárias.
3. **Custo zero:** Nível gratuito generoso (*free tier* da Google AI Studio).

---

## 2. Como Funciona o Algoritmo de Pontuação (Score de 0 a 100)

Quando uma ideia é enviada pelo operador no aplicativo (ou avaliada pelo gestor), o backend dispara um prompt estruturado para o Gemini contendo:
1. **Título da Ideia**
2. **Descrição detalhada e problema enfrentado**
3. **Categoria operacional** (ex: Manutenção, Atendimento, Eficiência Energética)
4. **Estratégia institucional vigente** do Grupo Águia Branca à qual a ideia está atrelada.

### O Prompt de Engenharia:
```text
Você é um especialista sênior em Inovação Corporativa do Grupo Águia Branca (líder em transporte rodoviário, comércio e logística).
Avalie a seguinte ideia proposta por um colaborador operacional:

- TÍTULO: {titulo}
- DESCRIÇÃO: {descricao}
- CATEGORIA: {categoria}
- ESTRATÉGIA VIGENTE ALINHADA: {estrategiaTitulo}

Critérios de avaliação:
1. Viabilidade técnica e operacional (facilidade de implementação no dia a dia).
2. Impacto potencial no negócio (redução de custos, segurança, satisfação do cliente ou produtividade).
3. Alinhamento com a estratégia do Grupo Águia Branca.

Responda ESTRITAMENTE em formato JSON:
{
  "score": 85,
  "prioridade": "Alta",
  "analise": "Breve explicação de até 2 frases destacando pontos fortes e oportunidades."
}
```

---

## 3. Retorno e Benefícios para a Gestão

- **Score Numérico (0 a 100):** Permite ordenação e ranking automático de todas as ideias no painel do Gestor. As ideias com maior potencial sobem ao topo do funil.
- **Prioridade Automatizada:** Classificação imediata em `Alta`, `Média` ou `Baixa`.
- **Parecer Técnico da IA (`AiAnalise`):** Auxilia o gestor na tomada de decisão sobre aprovar ou converter a ideia diretamente em um **Projeto Real**.
- **Engajamento Operacional:** O colaborador na ponta (motorista, mecânico, atendente) sente que sua sugestão foi imediatamente lida e avaliada com critérios transparentes.
