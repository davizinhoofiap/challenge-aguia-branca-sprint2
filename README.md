# Challenge Grupo Aguia Branca - Sprint 2
FIAP - Engenharia de Software

Alunos: Davi, Victor e Fernando

---

## E ai, professor! Tudo bem?

Aqui esta a nossa entrega da Sprint 2 do Challenge do Grupo Aguia Branca. Pegamos todos os feedbacks que o senhor passou na Sprint 1 e refatoramos o projeto inteiro:

1. **Adeus cores apagadas:** Tiramos aquele tema dinamico do Android que desbotava e deixamos a interface com contraste alto, bordas nitidas e o azul oficial da Aguia Branca.
2. **Nova arquitetura de verdade:** Separamos o codigo em camadas (Controllers, Services, Repositories, ViewModels e DTOs) respeitando o SRP.
3. **APK 100% liberado:** O arquivo `App_Inovacao_AguiaBranca.apk` foi gerado sem a flag `TEST_ONLY`, pronto para instalar direto no seu aparelho.
4. **Backend e Nuvem:** Criamos uma API REST completa em C# .NET 8 conectada direto no MongoDB Atlas (`InovacaoAguiaBrancaDB`) com pontuacao de viabilidade por IA.

---

## Como testar em 2 passos rapidos

### 1. Subir o Backend (1 clique)
Para facilitar sua correcao sem precisar digitar comandos no terminal:
- De dois cliques no arquivo `iniciar_backend.bat` aqui na raiz.
- Ele ja acha o .NET 8, sobe o servidor na porta 5000 e conecta direto no cluster do MongoDB Atlas na nuvem.
- Se quiser ver os endpoints e testar pelo navegador, o Swagger fica em: `http://localhost:5000`

### 2. Abrir o App Android
- **No Emulador:** Abra a pasta `mobile` no Android Studio e de Play (o app ja aponta para `http://10.0.2.2:5000/api/` por padrao).
- **No Celular fisico:** Conecte o USB com depuracao ativa, rode `adb reverse tcp:5000 tcp:5000` e instale o APK `App_Inovacao_AguiaBranca.apk`.

---

## Contas de Teste (Senha para todas: 123456)

Criamos uma conta especial de Lider para o senhor avaliar tudo com acesso total:

| Perfil | E-mail | Senha | O que testar com essa conta |
| :--- | :--- | :--- | :--- |
| **Professor FIAP** | `professor@fiap.com.br` | `123456` | Acesso total de Lider. Ver Dashboard executivo com ROI, avaliar ideias e acompanhar metricas. |
| **Operador** | `operador@aguiabranca.com.br` | `123456` | Cadastrar ideias na operacao. Ao enviar, a IA ja calcula o Score (0 a 100) na hora. |
| **Gestor** | `gestor@aguiabranca.com.br` | `123456` | Esteira de avaliacao. Pode aprovar ou reprovar ideias e criar projetos. |
| **Lider** | `lider@aguiabranca.com.br` | `123456` | Visao estrategica corporativa e graficos financeiros do Dashboard. |

---

## Roteiro rapido para correcao

1. **Crie uma ideia com o Operador:**
   - Entre com `operador@aguiabranca.com.br` / `123456`.
   - Va em "Nova Ideia" e mande uma proposta operacional (ex: "Sensores de telemetria nos freios").
   - Na tela inicial, veja que a ideia ja aparece em "Minhas Ideias" com o Score de IA calculado.
2. **Aprove com o Gestor:**
   - Clique na portinha no canto superior direito para sair da conta.
   - Entre com `gestor@aguiabranca.com.br` / `123456`.
   - Va em "Avaliar Ideias", abra a ideia que acabou de criar e clique em "Aprovar".
   - Veja que ela ganha destaque verde e vai direto para a aba "Aprovadas".
3. **Veja o impacto no Dashboard:**
   - Faca logout e entre com `professor@fiap.com.br` (ou `lider@aguiabranca.com.br`).
   - Abra a tela "Dashboard": confira os indicadores de ROI medio (280%), reducao de custos e lucro obtido consolidados.

---

## Estrutura dos arquivos

```
Sprint2_Entrega/
├── iniciar_backend.bat            # Inicia o servidor .NET 8 com 1 clique
├── App_Inovacao_AguiaBranca.apk   # APK oficial sem flag TEST_ONLY
├── README.md                      # Este guia rapido
│
├── backend/                       # API REST em C# .NET 8 (MongoDB Atlas + JWT + IA)
├── mobile/                        # App Android nativo em Kotlin (Jetpack Compose + Hilt + Retrofit)
└── docs/                          # Documentacao complementar de arquitetura e endpoints
```
