# Challenge Grupo Aguia Branca - Sprint 2
FIAP

Alunos: Davi, Victor e Fernando

---

## entrega completa da Sprint 2 do Challenge do Grupo Aguia Branca. Refatoramos todo o projeto para atender 100% dos requisitos do edital, conectando o aplicativo nativo em Kotlin a um backend real em C# .NET 8 com banco NoSQL no MongoDB Atlas e analise inteligente de viabilidade por IA.

Para facilitar  correcao e permitir que teste tudo na sua maquina em menos de 2 minutos sem precisar criar conta ou configurar banco local, deixamos tudo pre-configurado na nuvem.

---

## Como testar em 2 passos rapidos

### Passo 1: Subir o Backend (1 clique)
- De dois cliques no arquivo `iniciar_backend.bat` na raiz do projeto.
- O script detecta o .NET 8, conecta direto no nosso cluster do MongoDB Atlas na nuvem e sobe o servidor na porta 5000.
- Se preferir rodar via terminal:
  ```bash
  cd backend/src
  dotnet run --urls "http://0.0.0.0:5000"
  ```
- Para conferir os endpoints no Swagger, acesse no navegador: `http://localhost:5000/`

*(Caso queira olhar os dados no MongoDB Compass, nossa connection string oficial e: `mongodb+srv://davizinhofiap:DtNasc%23070704@cluster0.nljwos1.mongodb.net/?retryWrites=true&w=majority` e o banco e o `InovacaoAguiaBrancaDB`).*

### Passo 2: Abrir o App Android
- **No Emulador do Android Studio:** Abra a pasta `app-mobile`, aguarde o sync do Gradle e clique em Run (Play). O app ja vem configurado por padrao para se comunicar com o seu PC atraves do endereco `http://10.0.2.2:5000/api/`.
- **No Celular Fisico via Cabo USB:** Conecte o celular com depuracao USB ativa e rode no terminal do seu computador:
  ```bash
  adb reverse tcp:5000 tcp:5000
  ```
  O app possui fallback automatico para `127.0.0.1:5000`.
- **Instalacao Direta do APK:** Se nao quiser abrir o Android Studio, o executavel oficial pronto para instalar esta na raiz: `App_Inovacao_AguiaBranca.apk` (gerado sem a flag testOnly).

---

## Contas de Teste Prontas (Senha para todas: 123456)

Ja deixamos o banco na nuvem populado com usuarios para cada papel do edital:

| Perfil | E-mail | Senha | O que testar com essa conta |
| :--- | :--- | :--- | :--- |
| **Professor FIAP** | `professor@fiap.com.br` | `123456` | Acesso executivo de Lider. Visualizacao do Dashboard com ROI (280%), reducao de custos, esteira de aprovacao e projetos. |
| **Operador** | `operador@aguiabranca.com.br` | `123456` | Cadastro de ideias da operacao. O Score e parecer tecnico de viabilidade sao calculados na hora pela IA. |
| **Gestor** | `gestor@aguiabranca.com.br` | `123456` | Esteira de triagem. Avaliar, aprovar ou reprovar ideias e converter ideias aprovadas em projetos. |
| **Lider** | `lider@aguiabranca.com.br` | `123456` | Acompanhamento estrategico, indicadores consolidados e graficos por pilar corporativo. |

---

## Roteiro rapido sugerido para a correcao

1. **Cadastre uma ideia com o Operador:**
   - Entre com `operador@aguiabranca.com.br` / `123456`.
   - Va em "Nova Ideia", selecione uma estrategia vigente (ex: Seguranca Operacional) e envie uma proposta.
   - Veja que na tela inicial ela ja aparece com o Score de IA calculado (0 a 100). Clique no card para abrir os detalhes e a analise tecnica da IA.
2. **Aprove e crie o Projeto com o Gestor:**
   - Clique na portinha no canto superior direito para sair da conta.
   - Entre com `gestor@aguiabranca.com.br` / `123456`.
   - Va em "Avaliar Ideias", abra a ideia criada e clique em "Aprovar".
   - Veja que a ideia ganha destaque verde e se move para a aba "Aprovadas".
3. **Consulte o impacto no Dashboard Executivo:**
   - Faca logout e entre com a conta `professor@fiap.com.br` / `123456`.
   - Acesse o "Dashboard" e veja os indicadores de ROI medio consolidado, lucro gerado, reducao de custos e o grafico de retorno por estrategia sincronizados direto do MongoDB Atlas.

---

## Como a IA foi implementada (Diferencial Plus do Edital)

Atendendo ao requisito opcional de inovacao com IA do edital, integramos o servico `GeminiAiService.cs`:
- Conectado a API do Google Gemini (`gemini-1.5-flash`).
- Ao submeter uma ideia, a IA analisa a viabilidade operacional para o Grupo Aguia Branca, o alinhamento com a estrategia escolhida e o potencial de retorno.
- Retorna automaticamente o Score de viabilidade, o nivel de prioridade (Alta, Media ou Baixa) e uma sintese tecnica para apoiar a decisao do gestor.
- Conta com motor de fallback heuristico para garantir que o sistema nunca trave caso a rede externa oscile.

---

## Estrutura dos Arquivos do Repositorio

```
challenge-aguia-branca-sprint2/
├── .gitignore                     # Protecao contra arquivos de cache e build
├── .env.example                   # Template das variaveis de ambiente
├── README.md                      # Este guia rapido
├── iniciar_backend.bat            # Executavel em 1 clique para subir o servidor
├── App_Inovacao_AguiaBranca.apk   # APK oficial pronto para teste
│
├── backend/                       # API REST em C# .NET 8
│   ├── .gitignore                 # Ignora bin e obj
│   └── src/
│       ├── Controllers/           # Auth, Ideias, Projetos, Dashboard, Estrategias
│       ├── Services/              # Regras de negocio e analise por IA
│       ├── Repositories/          # Conexao MongoDB Atlas e DatabaseSeeder
│       ├── Models/                # Entidades NoSQL
│       ├── DTOs/                  # Objetos de transferencia de dados
│       └── Program.cs             # Configuracao de JWT, CORS e Swagger
│
├── app-mobile/                    # App Android Nativo em Kotlin
│   ├── .gitignore                 # Ignora .gradle e build
│   ├── build.gradle.kts           # Gradle 8.13 e AGP 8.13.2
│   └── app/src/main/java/com/example/projetoguiabranca/
│       ├── data/network/ApiConfig.kt # IP da API centralizado para emulador e celular
│       ├── ui/screen/             # Telas em Jetpack Compose
│       └── ...
│
└── docs/                          # Documentacao complementar
    ├── Arquitetura_Backend.md     # Diagrama em camadas Mermaid
    ├── Especificacao_Endpoints.md # Rotas, metodos, payloads e responses
    └── Modelo_IA_Score.md         # Detalhamento do modelo de IA
```
