package navegacion.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MatchCard(
    partido: Partido,
    resultadoJugador: ResultadoJugador? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val borderColor = when (partido.estatus) {
        EstatusPartido.FINALIZADO -> {
            if (resultadoJugador != null) {
                // Determinar resultado
                val golesJugador = if (partido.equipoLocal.id == 1) partido.golesLocal else partido.golesVisitante
                val golesRival = if (partido.equipoLocal.id == 1) partido.golesVisitante else partido.golesLocal

                when {
                    golesJugador > golesRival -> Success // Victoria
                    golesJugador < golesRival -> Error // Derrota
                    else -> Warning // Empate
                }
            } else {
                Success
            }
        }
        EstatusPartido.EN_VIVO -> Error
        EstatusPartido.PROGRAMADO -> Info
        else -> CardBorder
    }

    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${partido.torneoNombre} • JORNADA ${partido.jornada}",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                    fontWeight = FontWeight.SemiBold
                )

                StatusBadge(partido.estatus)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Teams and Score
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Equipo Local
                TeamInfo(
                    nombre = partido.equipoLocal.nombre,
                    modifier = Modifier.weight(1f)
                )

                // Score
                ScoreDisplay(
                    partido = partido,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                // Equipo Visitante
                TeamInfo(
                    nombre = partido.equipoVisitante.nombre,
                    modifier = Modifier.weight(1f),
                    alignEnd = true
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = CardBorder)
            Spacer(modifier = Modifier.height(12.dp))

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "📍 ${partido.cancha}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                if (resultadoJugador != null) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (resultadoJugador.goles > 0) {
                            Text(
                                text = "⚽ ${resultadoJugador.goles}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        if (resultadoJugador.asistencias > 0) {
                            Text(
                                text = "🎯 ${resultadoJugador.asistencias}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        if (resultadoJugador.tarjetasAmarillas > 0) {
                            Text(
                                text = "🟨 ${resultadoJugador.tarjetasAmarillas}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                } else {
                    Text(
                        text = formatFechaPartido(partido),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        // Borde lateral de color
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(borderColor)
        )
    }
}

@Composable
private fun TeamInfo(
    nombre: String,
    modifier: Modifier = Modifier,
    alignEnd: Boolean = false
) {
    Row(
        modifier = modifier,
        horizontalArrangement = if (alignEnd) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (alignEnd) {
            Text(
                text = nombre,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.width(10.dp))
        }

        Box(
            modifier = Modifier
                .size(35.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(PrimaryPurple),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = nombre.first().toString(),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (!alignEnd) {
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = nombre,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ScoreDisplay(
    partido: Partido,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (partido.estatus) {
            EstatusPartido.PROGRAMADO -> {
                Text(
                    text = "VS",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextTertiary,
                    fontWeight = FontWeight.SemiBold
                )
            }
            else -> {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = partido.golesLocal.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "-",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextTertiary
                    )
                    Text(
                        text = partido.golesVisitante.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
        }

        if (partido.estatus == EstatusPartido.EN_VIVO) {
            Text(
                text = "Min. ${partido.minuto}'",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun StatusBadge(estatus: EstatusPartido) {
    val (backgroundColor, textColor, text) = when (estatus) {
        EstatusPartido.FINALIZADO -> Triple(Color(0xFFC6F6D5), Color(0xFF22543D), "Finalizado")
        EstatusPartido.EN_VIVO -> Triple(Color(0xFFFED7D7), Color(0xFF742A2A), "EN VIVO")
        EstatusPartido.PROGRAMADO -> Triple(Color(0xFFBEE3F8), Color(0xFF2C5282), "Programado")
        else -> Triple(CardBorder, TextSecondary, "Cancelado")
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun formatFechaPartido(partido: Partido): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val diaSemana = partido.fechaHora.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("es"))
    val dia = partido.fechaHora.dayOfMonth
    val mes = partido.fechaHora.month.getDisplayName(TextStyle.SHORT, Locale("es"))
    val hora = partido.fechaHora.format(formatter)

    return "$diaSemana, $dia $mes • $hora"
}
