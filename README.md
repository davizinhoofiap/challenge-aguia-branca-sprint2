# Challenge Grupo Aguia Branca - Sprint 2
FIAP - Engenharia de Software

Alunos: Davi, Victor e Fernando

---

## E ai, pessoal! Guia rapido de configuracao e execucao

Este repositorio unificado contem o ecossistema completo da Sprint 2 dividido em duas pastas principais:
- `/backend`: API REST em C# .NET 8 com integracao ao MongoDB Atlas na nuvem e motor de analise de viabilidade por IA.
- `/app-mobile`: Aplicativo Android nativo em Kotlin com Jetpack Compose, Material Design 3 e arquitetura MVVM + Hilt.

---

## 1. Como Clonar o Repositorio

Para baixar o projeto na sua maquina, abra o terminal e rode:

```bash
git clone https://github.com/davizinhoofiap/challenge-aguia-branca-sprint2.git
cd challenge-aguia-branca-sprint2
```

---

## 2. Configuracao do MongoDB Atlas na Nuvem

Para que ninguem da equipe precise subir banco local ou restaurar dump no MongoDB Compass, usamos o cluster centralizado no Atlas:

1. **Liberar IP no Atlas (Network Access):**
   - Acesse o painel do MongoDB Atlas > `Network Access`.
   - Clique em `Add IP Address` e adicione o IP da sua conexao (ou configure `0.0.0.0/0` para permitir acesso de qualquer lugar durante os testes).
2. **Usuario da Equipe (Database Access):**
   - No Atlas, va em `Database Access` e utilize o usuario de leitura/escrita criado para o projeto.
   - NUNCA suba senhas ou credenciais reais para o Git! As credenciais reais sao enviadas diretamente no chat privado da equipe.

---

## 3. Configurando e Rodando o Backend (.NET 8)

### Onde colar a Connection String do Mongo Atlas:
1. Va ate a pasta `backend/src/`.
2. Duplique o arquivo de exemplo `appsettings.Example.json` e renomeie a copia para `appsettings.json` (este arquivo ja esta protegido no `.gitignore` para nao vazar senhas).
3. Cole a string de conexao real no campo `ConnectionString`:
   ```json
   "MongoDbSettings": {
     "ConnectionString": "mongodb+srv://<USUARIO>:<SENHA>@cluster0.xxxxx.mongodb.net/?retryWrites=true&w=majority",
     "DatabaseName": "InovacaoAguiaBrancaDB"
   }
   ```
   *(Obs: Voce tambem pode definir a variavel de ambiente `MONGODB_URI` se preferir).*

### Como Iniciar o Servidor:

- **Opcao 1 (1 clique no Windows):**
  De dois cliques no arquivo `iniciar_backend.bat` na raiz do projeto. Ele verifica o .NET 8, cria o `appsettings.json` automaticamente se faltar e sobe a API.

- **Opcao 2 (Via Terminal):**
  ```bash
  cd backend/src
  dotnet run --urls "http://0.0.0.0:5000"
  ```

### Como testar se subiu:
- Abra o navegador em: `http://localhost:5000` ou `http://localhost:5000/swagger`
- Se o Swagger carregar com os endpoints de Autenticacao, Ideias, Projetos e Dashboard, o backend esta 100% ativo e conectado a nuvem.

### Como ver os dados no MongoDB Compass (Opcional):
- Abra o MongoDB Compass.
- Cole a mesma string de conexao do Atlas (`mongodb+srv://...`).
- Clique em `Connect` para visualizar as collections `Usuarios`, `Ideias`, `Projetos` e `Estrategias`.

---

## 4. Configurando e Rodando o App Android (`/app-mobile`)

### Passo a passo:
1. Abra o **Android Studio**.
2. Clique em **File > Open** e selecione a pasta `app-mobile` deste repositorio.
3. Aguarde o Android Studio realizar a sincronizacao automatica do Gradle (Sync Project with Gradle Files).

### Ajuste do IP da API (Centralizado em ApiConfig.kt):
O endereco do backend esta centralizado no arquivo:
`app-mobile/app/src/main/java/com/example/projetoguiabranca/data/network/ApiConfig.kt`

- **Se rodar no Emulador do Android Studio (PC):**
  Mantenha o padrao: `http://10.0.2.2:5000/api/` (o IP `10.0.2.2` e o alias do emulador para o localhost da sua maquina).
- **Se rodar no Celular Fisico via Cabo USB (Recomendado):**
  Conecte o aparelho com a depuracao USB ativada e rode no terminal do PC:
  ```bash
  adb reverse tcp:5000 tcp:5000
  ```
  O app tem fallback automatico e se conecta a `127.0.0.1:5000` sem precisar mudar nada.
- **Se rodar no Celular Fisico via Wi-Fi:**
  Descubra o IP local do seu computador no terminal (`ipconfig` -> Endereco IPv4, ex: `192.168.1.15`).
  Altere `BASE_URL` em `ApiConfig.kt` para: `http://192.168.1.15:5000/api/`
  *(Importante: celular e computador devem estar na mesma rede Wi-Fi).*

### Executar o App:
- Selecione o dispositivo (emulador ou aparelho fisico) no topo do Android Studio e clique em **Run 'app' (Play verde)**.
- Se preferir instalar direto sem abrir o Android Studio, o executavel oficial pronto esta na raiz: `App_Inovacao_AguiaBranca.apk`.

---

## 5. Contas de Teste Prontas (Senha para todas: 123456)

O banco ja vem populado com usuarios de cada nivel hierarquico para testes imediatos:

| Perfil | E-mail | Senha | Funcionalidade |
| :--- | :--- | :--- | :--- |
| **Professor FIAP** | `professor@fiap.com.br` | `123456` | Acesso total de Lider executivo. Dashboard com ROI consolidado, esteira de aprovacao e projetos. |
| **Operador** | `operador@aguiabranca.com.br` | `123456` | Cadastro de ideias na operacao. Calculo automatico de viabilidade e Score por IA na hora. |
| **Gestor** | `gestor@aguiabranca.com.br` | `123456` | Esteira de avaliacao. Pode aprovar ou reprovar ideias e criar projetos. |
| **Lider** | `lider@aguiabranca.com.br` | `123456` | Visao macro, graficos financeiros e acompanhamento de inovacao corporativa. |

---

## 6. Estrutura do Repositorio

```
sprint2-aguia-branca/
├── .gitignore                     # Protecao contra cache de build e arquivos sensiveis
├── .env.example                   # Template seguro das variaveis de ambiente
├── README.md                      # Roteiro completo de execucao
├── iniciar_backend.bat            # Script para subir o backend com 1 clique
├── App_Inovacao_AguiaBranca.apk   # APK oficial sem flag TEST_ONLY
│
├── backend/                       # API REST C# .NET 8
│   ├── .env.example               # Exemplo de variaveis de ambiente
│   ├── .gitignore                 # Ignora bin, obj e appsettings.json
│   └── src/
│       ├── appsettings.Example.json  # Template de configuracao sem senhas
│       ├── Controllers/           # Auth, Ideias, Projetos, Dashboard, Estrategias
│       ├── Services/              # Regras de negocio e analise heuristica/Gemini IA
│       ├── Repositories/          # Conexao MongoDB Atlas e DatabaseSeeder
│       └── Program.cs             # Inicializacao com suporte a CORS, JWT e Swagger
│
├── app-mobile/                    # App Android Nativo em Kotlin
│   ├── .gitignore                 # Ignora .gradle, build e .idea
│   ├── build.gradle.kts           # Configuracao do Gradle 8.13
│   └── app/src/main/java/com/example/projetoguiabranca/
│       ├── data/network/ApiConfig.kt # IP da API centralizado para emulador e celular
│       ├── ui/screen/             # Telas em Jetpack Compose (Login, Home, Gestao, etc.)
│       └── ...
│
└── docs/                          # Documentacao complementar de arquitetura e endpoints
```
