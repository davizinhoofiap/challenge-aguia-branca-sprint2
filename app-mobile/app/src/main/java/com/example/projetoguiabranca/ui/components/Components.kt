package com.example.projetoguiabranca.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.projetoguiabranca.data.model.*
import com.example.projetoguiabranca.ui.theme.*

@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = AzulPrimario)
    }
}

@Composable
fun ErrorState(mensagem: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
            Icon(Icons.Default.ErrorOutline, null,
                tint = CorErro, modifier = Modifier.size(48.dp))
            Spacer(Modifier.height(12.dp))
            Text(mensagem, style = MaterialTheme.typography.bodyLarge, color = TextoPrimario)
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = AzulPrimario)
            ) {
                Text("Tentar novamente", color = Color.White)
            }
        }
    }
}

@Composable
fun StatusChip(status: IdeiaStatus, modifier: Modifier = Modifier) {
    val (texto, cor) = when (status) {
        IdeiaStatus.CAPTURADA    -> "Capturada"    to AzulSecundario
        IdeiaStatus.EM_AVALIACAO -> "Em Avaliação" to CorAlerta
        IdeiaStatus.APROVADA     -> "Aprovada"     to CorSucesso
        IdeiaStatus.EM_PROJETO   -> "Em Projeto"   to AzulPrimario
        IdeiaStatus.CONCLUIDA    -> "Concluída"    to CorSucesso
        IdeiaStatus.REPROVADA    -> "Reprovada"    to CorErro
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = cor.copy(alpha = 0.15f)
    ) {
        Text(
            texto,
            color = cor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun ImpactoChip(impacto: String, modifier: Modifier = Modifier) {
    val cor = when (impacto.uppercase()) {
        "ALTO"  -> ImpactoAlto
        "MEDIO", "MÉDIO" -> ImpactoMedio
        else    -> ImpactoBaixo
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = cor.copy(alpha = 0.12f)
    ) {
        Row(
            Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(6.dp).clip(CircleShape).background(cor))
            Spacer(Modifier.width(4.dp))
            Text(
                impacto.lowercase().replaceFirstChar { it.uppercase() },
                color = cor,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun IaScoreChip(score: Int, prioridade: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = IaScoreFundo,
        border = BorderStroke(1.dp, IaScoreDestaque.copy(alpha = 0.3f))
    ) {
        Row(
            Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.AutoAwesome,
                contentDescription = "IA",
                tint = IaScoreDestaque,
                modifier = Modifier.size(12.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                "Score IA: $score/100 ($prioridade)",
                color = IaScoreDestaque,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun IdeiaCard(ideia: Ideia, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val isAprovada = ideia.status == IdeiaStatus.APROVADA
    val fundoCard = if (isAprovada) Color(0xFFF0FDF4) else CardBranco
    val bordaCard = if (isAprovada) BorderStroke(1.5.dp, CorSucesso) else BorderStroke(1.dp, CinzaBorda)

    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = fundoCard),
        elevation = CardDefaults.cardElevation(if (isAprovada) 3.dp else 2.dp),
        border = bordaCard
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusChip(ideia.status)
                ImpactoChip(ideia.impactoEstimado)
            }

            if (isAprovada) {
                Spacer(Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = CorSucesso
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color.White, modifier = Modifier.size(13.dp))
                        Spacer(Modifier.width(5.dp))
                        Text(
                            "Aprovada para Projeto",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            Text(
                ideia.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextoPrimario,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(6.dp))
            Text(
                ideia.descricao,
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            // Destaque do Score da IA se já avaliado
            if (ideia.aiScore > 0) {
                Spacer(Modifier.height(10.dp))
                IaScoreChip(score = ideia.aiScore, prioridade = ideia.aiPrioridade)
            }

            Spacer(Modifier.height(12.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Person,
                        null,
                        tint = TextoSecundario,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        ideia.autorNome,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = TextoSecundario
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.ThumbUp,
                            null,
                            tint = AzulPrimario,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(3.dp))
                        Text(
                            "${ideia.votos}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = TextoPrimario
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Comment,
                            null,
                            tint = TextoSecundario,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(3.dp))
                        Text(
                            "${ideia.comentarios}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = TextoPrimario
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetricaCard(
    label: String,
    valor: String,
    variacao: String,
    positivo: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBranco),
        elevation = CardDefaults.cardElevation(2.dp),
        border = BorderStroke(1.dp, CinzaBorda)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = TextoSecundario
            )
            Spacer(Modifier.height(8.dp))
            Text(
                valor,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = AzulPrimario
            )
            Spacer(Modifier.height(4.dp))
            if (variacao.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val cor = if (positivo) CorSucesso else CorErro
                    val icone = if (positivo) Icons.Default.TrendingUp else Icons.Default.TrendingDown
                    Icon(icone, null, tint = cor, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(3.dp))
                    Text(
                        variacao,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = cor
                    )
                }
            }
        }
    }
}

@Composable
fun ProjetoProgressBar(progresso: Float, modifier: Modifier = Modifier) {
    val animatedProgress by animateFloatAsState(
        targetValue = progresso,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label = "progresso"
    )
    Column(modifier) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                "Progresso",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario
            )
            Text(
                "${(animatedProgress * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = AzulPrimario
            )
        }
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = AzulPrimario,
            trackColor = AzulClaro
        )
    }
}

@Composable
fun ProjetoCard(projeto: Projeto, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val corStatus = when (projeto.status) {
        ProjetoStatus.EM_ANDAMENTO -> AzulPrimario
        ProjetoStatus.PLANEJAMENTO -> CorAlerta
        ProjetoStatus.CONCLUIDO    -> CorSucesso
        ProjetoStatus.PAUSADO      -> TextoSecundario
    }
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBranco),
        elevation = CardDefaults.cardElevation(2.dp),
        border = BorderStroke(1.dp, CinzaBorda)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    projeto.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextoPrimario,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = corStatus.copy(alpha = 0.15f)
                ) {
                    Text(
                        projeto.status.name.replace("_", " "),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = corStatus,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            ProjetoProgressBar(projeto.progresso)
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Responsável", style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
                    Text(projeto.responsavel, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = TextoPrimario)
                }
                Column {
                    Text("Previsão", style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
                    Text(projeto.dataPrevisao, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = TextoPrimario)
                }
            }
        }
    }
}

@Composable
fun HeaderUsuario(
    usuario: Usuario,
    modifier: Modifier = Modifier
) {
    val (labelPerfil, corPerfil) = when (usuario.perfil) {
        UserProfile.ESTRATEGICO -> "LÍDER" to IaScoreDestaque
        UserProfile.TATICO      -> "GESTOR" to CorAlerta
        UserProfile.OPERACIONAL -> "OPERADOR" to AzulPrimario
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Surface(shape = CircleShape, color = AzulPrimario, modifier = Modifier.size(48.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        usuario.nome.firstOrNull()?.toString() ?: "U",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Olá, ${usuario.nome.split(" ").first()}!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextoPrimario
                    )
                    Spacer(Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = corPerfil.copy(alpha = 0.12f)
                    ) {
                        Text(
                            labelPerfil,
                            color = corPerfil,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    "${usuario.cargo} · ${usuario.divisao}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoSecundario
                )
            }
        }
    }
}

// Campo de texto padronizado com contraste garantido para todos os formulários
@Composable
fun GabOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    supportingText: String? = null,
    singleLine: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextoSecundario) },
        placeholder = placeholder?.let { { Text(it, color = TextoDesabilitado) } },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError,
        supportingText = supportingText?.let { { Text(it, color = CorErro) } },
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextoPrimario,
            unfocusedTextColor = TextoPrimario,
            cursorColor = AzulPrimario,
            focusedBorderColor = AzulPrimario,
            unfocusedBorderColor = CinzaBorda,
            focusedContainerColor = CardBranco,
            unfocusedContainerColor = CardBranco,
            focusedLabelColor = AzulPrimario,
            unfocusedLabelColor = TextoSecundario
        )
    )
}

@Composable
fun OrientacaoCard(orientacao: OrientacaoEstrategica, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.width(260.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBranco),
        elevation = CardDefaults.cardElevation(2.dp),
        border = BorderStroke(1.dp, CinzaBorda)
    ) {
        Column(Modifier.padding(16.dp)) {
            Surface(shape = RoundedCornerShape(6.dp), color = AzulPrimario) {
                Text(
                    orientacao.pilar,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                orientacao.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextoPrimario
            )
            Spacer(Modifier.height(6.dp))
            Text(
                orientacao.descricao,
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(5) { i ->
                    Icon(
                        Icons.Default.Star,
                        null,
                        tint = if (i < orientacao.prioridade) CorAlerta else CinzaBorda,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Spacer(Modifier.width(6.dp))
                Text(
                    "Prioridade",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = TextoSecundario
                )
            }
        }
    }
}