# Aplicativo Android Kotlin - Sprint 2 (Grupo Águia Branca)

Aplicativo móvel nativo em **Kotlin** com interface moderna em **Jetpack Compose**, totalmente integrado ao Backend REST em **.NET 8** e com inteligência artificial para triagem e priorização de ideias.

---

## Correcoes Criticas Realizadas em Relacao a Sprint 1

### 1. Interface e Experiencia do Usuario (Contraste e Legibilidade)
- **Problema anterior:** Dificuldade na leitura de textos digitados e componentes na tela.
- **Solucao implementada:**
  - Desativacao de cores dinamicas do sistema operacional (`dynamicColor = false`) que causavam desbotamento em alguns aparelhos.
  - Paleta com Azul Marinho institucional de alto contraste (`AzulPrimario = #0F3E7A`), fundos claros limpos (`CinzaFundo = #F4F6F9`) e texto primario nitido (`TextoPrimario = #111827`).
  - Todos os campos de entrada (`OutlinedTextField`) foram padronizados com cores explicitas de foco, texto escuro e bordas nitidas, garantindo leitura perfeita tanto em modo claro quanto em modo escuro.

### 2. Arquitetura e Codigo Fonte (Principio de Responsabilidade Unica - SRP)
- **Problema anterior:** Classes acumulando multiplas funcoes em um unico repositorio/servico.
- **Solucao implementada:**
  - Eliminacao do repositorio monolitico e desacoplamento em repositorios especializados por dominio:
    - `AuthRepository`: Autenticacao e gestao de sessao com JWT via `SessionManager`.
    - `EstrategiaRepository`: Consulta e criacao de orientacoes estrategicas da empresa.
    - `IdeiaRepository`: Envio e listagem de ideias com pontuacao e parecer gerados pela IA.
    - `ProjetoRepository`: Gestao de iniciativas, status, etapas e indicadores de ROI.
    - `DashboardRepository`: Metricas agregadas e consolidacao para a lideranca.
  - Cada ViewModel agora reside em seu proprio arquivo (`HomeViewModel`, `CapturaIdeiaViewModel`, `GestaoIdeiasViewModel`, `ProjetosViewModel`, `DashboardViewModel`) e injeta exclusivamente o repositorio necessario.

### 3. Geracao do APK Oficial (Eliminacao da flag `TEST_ONLY`)
- **Problema anterior:** O APK enviado na Sprint 1 foi gerado com a flag `android:testOnly="true"`, impedindo instalacao direta.
- **Solucao implementada:**
  - Configurado `android.injected.testOnly=false` no `gradle.properties`.
  - Habilitado trafego claro HTTP (`android:usesCleartextTraffic="true"`) no `AndroidManifest.xml` para conexao com a API local em ambiente de testes.
  - Compilacao via tarefa oficial do Gradle (veja abaixo).

---

## Conexao com o Backend .NET 8

O aplicativo consome a API REST atraves do **Retrofit 2** e **OkHttp**:
- **URL padrao no emulador Android:** `http://10.0.2.2:5000/api/`
- **URL padrao em dispositivo fisico:** `http://<IP_DO_SEU_PC>:5000/api/` (ou via redirecionamento ADB `adb reverse tcp:5000 tcp:5000`)
- **Cabecalho de Autenticacao:** O `AuthInterceptor` anexa automaticamente o cabecalho `Authorization: Bearer <TOKEN>` em todas as requisicoes autenticadas apos o login.

---

## Como Compilar e Gerar o APK Oficial

Para gerar o arquivo APK instalavel sem a flag `TEST_ONLY`, execute no terminal dentro da pasta `mobile`:

```bash
./gradlew assembleDebug
```
*(No Windows PowerShell: `.\gradlew.bat assembleDebug`)*

O APK final sera gerado em:
```
app/build/outputs/apk/debug/app-debug.apk
```
Este arquivo pode ser transferido diretamente para qualquer celular Android ou instalado via comando:
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## Perfis Disponiveis para Demonstracao (Senha: 123456)
- **Operador:** `operador@aguiabranca.com.br`
- **Gestor:** `gestor@aguiabranca.com.br`
- **Lider:** `lider@aguiabranca.com.br`
