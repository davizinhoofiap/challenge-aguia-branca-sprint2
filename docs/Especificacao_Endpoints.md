# Especificação Completa dos Endpoints REST

Este documento atende ao requisito obrigatório de entrega da Sprint 2:
> *"Especificação dos endpoints (rota, método, payload, resposta)."*

---

## 1. Autenticação (`/api/auth`)

### 1.1 Login de Usuário
- **Rota:** `/api/auth/login`
- **Método:** `POST`
- **Acesso:** Público
- **Payload (Body):**
```json
{
  "email": "operador@aguiabranca.com.br",
  "senha": "123456"
}
```
- **Resposta (`200 OK`):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "usuarioId": "66db5e110000000000000001",
  "nome": "Carlos Operador",
  "email": "operador@aguiabranca.com.br",
  "perfil": "Operador",
  "cargo": "Motorista Instrutor",
  "divisao": "Passageiros",
  "expiracao": "2026-09-11T00:15:24Z"
}
```

---

## 2. Orientações Estratégicas (`/api/estrategias`)

### 2.1 Listar Estratégias
- **Rota:** `/api/estrategias`
- **Método:** `GET`
- **Acesso:** Autenticado (`Operador`, `Gestor`, `Lider`)
- **Query Params:** `?apenasAtivas=true` (opcional)
- **Resposta (`200 OK`):**
```json
[
  {
    "id": "66db5e220000000000000001",
    "titulo": "Eficiência Energética e Descarbonização da Frota",
    "descricao": "Otimizar o consumo de combustível e transição para energias limpas.",
    "categoria": "Sustentabilidade",
    "campanha": "Inova GAB 2026",
    "pilar": "Operação Sustentável",
    "prioridade": 1,
    "ativa": true,
    "dataCriacao": "2026-08-10T12:00:00Z"
  }
]
```

### 2.2 Criar Nova Estratégia
- **Rota:** `/api/estrategias`
- **Método:** `POST`
- **Acesso:** Exclusivo `Lider`
- **Payload (Body):**
```json
{
  "titulo": "Digitalização Total do Embarque",
  "descricao": "Agilizar a validação de bilhetes e experiência em solo.",
  "categoria": "Experiência do Cliente",
  "campanha": "Viagem do Futuro",
  "pilar": "Inovação Digital",
  "prioridade": 1
}
```
- **Resposta (`201 Created`):** Retorna o objeto criado com ID gerado.

---

## 3. Ideias de Inovação e Problemas (`/api/ideias`)

### 3.1 Cadastrar Nova Ideia (com IA integrada)
- **Rota:** `/api/ideias`
- **Método:** `POST`
- **Acesso:** Autenticado (`Operador`, `Gestor`, `Lider`)
- **Payload (Body):**
```json
{
  "titulo": "Sensor de Calibragem Automática e Desgaste de Pneus",
  "descricao": "Instalar sensores IoT nos eixos dos ônibus rodoviários para monitoramento contínuo da pressão e temperatura, reduzindo consumo de diesel.",
  "categoria": "Eficiência e Manutenção",
  "divisao": "Passageiros",
  "impactoEstimado": "Alto",
  "estrategiaId": "66db5e220000000000000001"
}
```
- **Resposta (`201 Created`):**
```json
{
  "id": "6aa1f75f180a5beda8baf63c",
  "titulo": "Sensor de Calibragem Automática e Desgaste de Pneus",
  "descricao": "Instalar sensores IoT nos eixos dos ônibus rodoviários...",
  "categoria": "Eficiência e Manutenção",
  "autorId": "66db5e110000000000000001",
  "autorNome": "Carlos Operador",
  "divisao": "Passageiros",
  "status": "Capturada",
  "impactoEstimado": "Alto",
  "aiScore": 92,
  "aiPrioridade": "Alta",
  "aiAnalise": "Ideia com altíssimo retorno econômico direto em redução de diesel e segurança.",
  "dataAvaliacaoIa": "2026-09-10T00:18:39Z",
  "dataCriacao": "2026-09-10T00:18:39Z"
}
```

### 3.2 Atualizar Status da Ideia (Aprovação / Priorização)
- **Rota:** `/api/ideias/{id}/status`
- **Método:** `PATCH`
- **Acesso:** `Gestor` ou `Lider`
- **Payload (Body):**
```json
{
  "status": "Aprovada"
}
```
- **Resposta (`200 OK`):** Retorna o objeto atualizado.

---

## 4. Projetos e Iniciativas (`/api/projetos`)

### 4.1 Cadastrar Projeto
- **Rota:** `/api/projetos`
- **Método:** `POST`
- **Acesso:** Exclusivo `Gestor`
- **Payload (Body):**
```json
{
  "titulo": "Piloto de Telemetria nos Eixos",
  "descricao": "Instalação em 40 veículos na rota Vitória x BH.",
  "ideiaOrigemId": "6aa1f75f180a5beda8baf63c",
  "responsavel": "Mariana Gestora",
  "equipe": ["Mariana Gestora", "Carlos Operador"],
  "etapa": "Piloto",
  "investimento": 85000.0
}
```
- **Resposta (`201 Created`)**

### 4.2 Atualizar Progresso e Resultados
- **Rota:** `/api/projetos/{id}`
- **Método:** `PUT`
- **Acesso:** Exclusivo `Gestor`
- **Payload (Body):**
```json
{
  "progresso": 75.0,
  "status": "EmAndamento",
  "roi": 280.0,
  "reducaoCustos": 95000.0,
  "lucroObtido": 140000.0,
  "ganhoProdutividade": 18.5
}
```
- **Resposta (`200 OK`)**

---

## 5. Dashboard Executivo (`/api/dashboard`)

### 5.1 Obter Resumo de Indicadores e Retorno por Estratégia
- **Rota:** `/api/dashboard/resumo`
- **Método:** `GET`
- **Acesso:** Exclusivo `Lider`
- **Resposta (`200 OK`):**
```json
{
  "totalIdeias": 3,
  "ideiasAprovadas": 1,
  "totalProjetos": 2,
  "projetosAtivos": 2,
  "totalInvestido": 125000.0,
  "totalEconomiaGerada": 125000.0,
  "totalLucroObtido": 140000.0,
  "roiMedioPercentual": 215.0,
  "ganhoProdutividadeMedio": 15.2,
  "metricas": [
    { "label": "ROI Médio", "valor": "215,0%", "variacao": "+18% vs trimestre anterior", "positivo": true },
    { "label": "Economia Operacional", "valor": "R$ 125.000", "variacao": "+24% redução de custos", "positivo": true }
  ],
  "retornoPorEstrategia": [
    {
      "estrategiaId": "66db5e220000000000000001",
      "titulo": "Eficiência Energética e Descarbonização da Frota",
      "quantidadeProjetos": 1,
      "totalInvestimento": 85000.0,
      "totalEconomia": 95000.0,
      "roiMedio": 280.0
    }
  ]
}
```
