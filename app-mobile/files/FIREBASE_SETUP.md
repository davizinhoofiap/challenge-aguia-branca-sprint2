# 🔥 Guia de Integração Firebase — Inovação GAB

## Passo 1 — Criar o projeto no Firebase Console

1. Acesse [console.firebase.google.com](https://console.firebase.google.com)
2. Clique em **"Adicionar projeto"**
3. Nome sugerido: `inovacao-gab`
4. Ative o Google Analytics (opcional)

---

## Passo 2 — Adicionar o app Android

1. No Console Firebase → **"Adicionar app"** → Android
2. Preencha:
   - **Package name:** `com.aguiabranca.inovacao`
   - **Apelido:** `Inovação GAB Android`
   - **SHA-1:** obtenha com o comando abaixo (necessário para Google Sign-In):
     ```bash
     ./gradlew signingReport
     ```
3. Baixe o arquivo `google-services.json`
4. **Coloque o arquivo em:** `app/google-services.json` (raiz do módulo app)

---

## Passo 3 — Configurar o build.gradle do projeto raiz

No arquivo `build.gradle.kts` **do projeto** (não do módulo app), adicione:

```kotlin
plugins {
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.google.dagger.hilt.android") version "2.52" apply false
    id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false
}
```

---

## Passo 4 — Usar o build.gradle do módulo app

Substitua seu `app/build.gradle.kts` pelo arquivo **`build.gradle.firebase.kts`**
gerado neste projeto (renomeie removendo o `.firebase`).

---

## Passo 5 — Configurar o AndroidManifest.xml

Adicione dentro de `<application>`:

```xml
<!-- Nome da Application class com Hilt -->
<application
    android:name=".InovacaoApp"
    ...>

    <!-- Permissão de internet (obrigatória para Firebase) -->
</application>
```

E fora de `<application>`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

---

## Passo 6 — Ativar Firebase Authentication

1. No Console Firebase → **Authentication → Começar**
2. Ative os provedores:
   - ✅ **E-mail/Senha**
   - ✅ **Google** (necessita o SHA-1 do Passo 2)
3. Em **Google**, copie o **Web Client ID** — você precisará dele no `NavGraphFirebase.kt`:
   ```kotlin
   webClientId = "123456789-abc...apps.googleusercontent.com"
   ```

---

## Passo 7 — Criar o banco Firestore

1. No Console Firebase → **Firestore Database → Criar banco de dados**
2. Escolha **modo de produção** (regras seguras por padrão)
3. Selecione a região: `southamerica-east1` (São Paulo) ✅

### Estrutura das coleções

```
firestore/
├── usuarios/
│   └── {uid}/           ← criado automaticamente no primeiro login
│       ├── nome: string
│       ├── cargo: string
│       ├── divisao: string
│       └── perfil: "OPERACIONAL" | "TATICO" | "ESTRATEGICO"
│
├── orientacoes/
│   └── {id}/
│       ├── titulo: string
│       ├── descricao: string
│       ├── pilar: string
│       ├── dataCriacao: string
│       └── prioridade: number (1–5)
│
├── ideias/
│   └── {id}/
│       ├── titulo: string
│       ├── descricao: string
│       ├── categoria: string
│       ├── autorId: string  ← uid do usuário
│       ├── autorNome: string
│       ├── divisao: string
│       ├── status: "CAPTURADA" | "EM_AVALIACAO" | "APROVADA" | ...
│       ├── dataCriacao: string (ISO 8601)
│       ├── impactoEstimado: "ALTO" | "MEDIO" | "BAIXO"
│       ├── votos: number
│       └── comentarios: number
│
└── projetos/
    └── {id}/
        ├── titulo: string
        ├── descricao: string
        ├── ideiaOrigemId: string
        ├── responsavel: string
        ├── equipe: array<string>
        ├── status: "PLANEJAMENTO" | "EM_ANDAMENTO" | "CONCLUIDO" | "PAUSADO"
        ├── progresso: number (0.0 a 1.0)
        ├── dataInicio: string
        ├── dataPrevisao: string
        ├── roi: number | null
        ├── reducaoCustos: number | null
        └── ganhoProdutividade: number | null
```

---

## Passo 8 — Fazer deploy das regras de segurança

```bash
# Instale o Firebase CLI se não tiver
npm install -g firebase-tools

# Login
firebase login

# Inicialize o projeto (na raiz do projeto Android)
firebase init firestore

# Deploy das regras
firebase deploy --only firestore:rules
```

Use o arquivo `firestore.rules` gerado neste projeto.

---

## Passo 9 — Popular dados iniciais (opcional)

No Console Firebase → Firestore → **"Adicionar documento"** na coleção `orientacoes`
com os campos do schema acima para ter dados na tela Home.

Ou crie um script de seed:

```kotlin
// Cole e rode temporariamente na MainActivity para popular o banco
private fun popularDadosIniciais(db: FirebaseFirestore) {
    db.collection("orientacoes").add(
        mapOf(
            "titulo"      to "Digitalização da Última Milha",
            "descricao"   to "Priorizar soluções que reduzam o tempo de entrega.",
            "pilar"       to "Direcionamento",
            "dataCriacao" to "2025-01-10",
            "prioridade"  to 5
        )
    )
}
```

---

## Checklist final

- [ ] `google-services.json` copiado para `app/`
- [ ] Plugins adicionados no `build.gradle` raiz
- [ ] `build.gradle` do app atualizado com Firebase + Hilt
- [ ] `android:name=".InovacaoApp"` no `AndroidManifest.xml`
- [ ] Authentication ativado (E-mail + Google)
- [ ] SHA-1 cadastrado no Console Firebase
- [ ] Web Client ID inserido no `NavGraphFirebase.kt`
- [ ] Firestore criado na região `southamerica-east1`
- [ ] Regras de segurança (`firestore.rules`) em deploy
- [ ] App compila e loga com sucesso 🚀
