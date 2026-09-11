# Backend .NET 8 - Plataforma de Inovação Corporativa Grupo Águia Branca

API REST robusta desenvolvida para a **Sprint 2** do Challenge FIAP / Grupo Águia Branca.

## Tecnologias Utilizadas
- **C# / .NET 8 Web API**
- **MongoDB Atlas** (Banco de dados NoSQL na nuvem)
- **JWT (JSON Web Token)** com criptografia BCrypt para autenticação segura
- **Google Gemini API** (`gemini-1.5-flash`) para pontuação (Score 1 a 100) e priorização automática de ideias
- **Swagger / OpenAPI** com suporte a Bearer Token para testes interativos
- **CORS** habilitado para integração móvel (Android / Emulador)

---

## Estrutura da Arquitetura em Camadas
```
backend/
└── src/
    ├── Configurations/    # Mapeamento de settings (Mongo, JWT, Gemini)
    ├── Controllers/       # Endpoints REST (Auth, Estrategias, Ideias, Projetos, Dashboard)
    ├── DTOs/              # Modelos de transferência de dados (Request / Response)
    ├── Models/            # Entidades do MongoDB (Usuario, Estrategia, Ideia, Projeto)
    ├── Repositories/      # Contexto do MongoDB Atlas com tolerância a falhas
    ├── Services/          # Lógica de negócio, motor de scoring IA e emissão de JWT
    ├── appsettings.json   # Configurações de conexão e credenciais
    └── Program.cs         # Inicialização, injeção de dependências e middlewares
```

---

## Perfis de Acesso e Regras de Segurança (Roles)

1. **Operador (`ROLE: Operador`):**
   - Consulta orientações estratégicas vigentes da empresa.
   - Cadastra problemas e ideias de inovação (a IA avalia automaticamente gerando Score de 1 a 100).
   - Acompanha o status de suas próprias ideias.
2. **Gestor (`ROLE: Gestor`):**
   - Consulta estratégias da empresa.
   - Lista, prioriza e aprova/reprova ideias cadastradas por todos os colaboradores.
   - Cadastra projetos/iniciativas derivados de ideias e atualiza dados de progresso e resultados.
3. **Líder (`ROLE: Lider`):**
   - Gerencia com CRUD completo as estratégias e campanhas institucionais.
   - Consulta o andamento global dos projetos.
   - Acessa o **Dashboard Executivo** com métricas consolidadas (ROI, lucro gerado, redução de custos, prazos).

---

## Contas Padrão Pré-cadastradas para Testes
| Perfil | E-mail | Senha |
| :--- | :--- | :--- |
| **Operador** | `operador@aguiabranca.com.br` | `123456` |
| **Gestor** | `gestor@aguiabranca.com.br` | `123456` |
| **Líder** | `lider@aguiabranca.com.br` | `123456` |

---

## Como Executar

### 1. Pré-requisitos
- .NET 8 SDK instalado.
- Conexão de rede (para MongoDB Atlas e Gemini API).

### 2. Configurar o `appsettings.json` (Opcional)
Abra `src/appsettings.json` e configure suas chaves se desejar:
```json
{
  "MongoDbSettings": {
    "ConnectionString": "mongodb+srv://<usuario>:<senha>@cluster.mongodb.net/?retryWrites=true&w=majority",
    "DatabaseName": "InovacaoAguiaBrancaDB"
  },
  "GeminiSettings": {
    "ApiKey": "SUA_CHAVE_GEMINI_AQUI"
  }
}
```
> **Nota de Resiliência:** Caso a string do MongoDB ou a chave do Gemini não sejam fornecidas no momento da avaliação, a API opera automaticamente com banco em memória tolerante a falhas e motor de scoring contextual heurístico, garantindo 100% de disponibilidade sem quebrar a aplicação!

### 3. Rodar a API
No terminal, dentro de `backend/src/`:
```bash
dotnet run
```
A API iniciará em:
- **Swagger UI:** `http://localhost:5000/`

---

## Documentacao dos Endpoints REST

### Autenticacao (/api/auth)
- `POST /api/auth/login`: Autentica usuario e retorna Token JWT + perfil.
- `POST /api/auth/register`: Cadastro de novo colaborador.
- `GET /api/auth/me`: Retorna dados da sessao ativa.
- `GET /api/auth/usuarios`: Listagem de colaboradores (Gestor/Lider).

### Estrategias (/api/estrategias)
- `GET /api/estrategias`: Consulta orientacoes estrategicas da empresa (Todos).
- `POST /api/estrategias`: Criacao de nova orientacao (Exclusivo Lider).
- `PUT /api/estrategias/{id}`: Atualizacao de orientacao (Exclusivo Lider).
- `DELETE /api/estrategias/{id}`: Remocao de orientacao (Exclusivo Lider).

### Ideias de Inovacao (/api/ideias)
- `GET /api/ideias`: Consulta ideias (Operador ve as suas; Gestor/Lider veem todas).
- `POST /api/ideias`: Envio de nova ideia com calculo imediato de Score de 0 a 100 e priorizacao.
- `PATCH /api/ideias/{id}/status`: Aprovacao e priorizacao de ideia (Exclusivo Gestor/Lider).
- `POST /api/ideias/{id}/avaliar-ia`: Reavaliacao explicita da ideia com IA.
- `DELETE /api/ideias/{id}`: Remocao da ideia.

### Projetos e Iniciativas (/api/projetos)
- `GET /api/projetos`: Consulta projetos e status (Gestor/Lider).
- `POST /api/projetos`: Criacao de iniciativa a partir de ideia aprovada (Exclusivo Gestor).
- `PUT /api/projetos/{id}`: Atualizacao de progresso, ROI, custos e prazos (Exclusivo Gestor).
- `DELETE /api/projetos/{id}`: Remocao de iniciativa (Exclusivo Gestor).

### Dashboard Executivo (/api/dashboard)
- `GET /api/dashboard/resumo`: Metricas agregadas (ROI, reducao de custos, lucros, prazos e retorno por estrategia) (Exclusivo Lider).
