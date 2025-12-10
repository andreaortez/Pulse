package com.example.medilink.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medilink.ui.perfil.Usuario

// Colores
import com.example.medilink.ui.theme.AzulNegro
import com.example.medilink.ui.theme.CelesteVivido

@Composable
fun UserCard(
    usuario: Usuario,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
        containerColor = Color.White
    ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar/Inicial
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(
                        if (usuario.tipo.contains("ADULTO_MAYOR", ignoreCase = true)) {
                            CelesteVivido.copy(alpha = 0.2f)
                        } else {
                            AzulNegro.copy(alpha = 0.2f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = usuario.nombre.first().uppercase(),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (usuario.tipo.contains("ADULTO_MAYOR", ignoreCase = true)) {
                            CelesteVivido
                        } else {
                            AzulNegro
                        }
                    )
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Información principal
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${usuario.nombre} ${usuario.apellido}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = AzulNegro
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Chip de tipo de usuario
                    val esAdultoMayor = usuario.tipo.contains("ADULTO_MAYOR", ignoreCase = true)

                    val chipBgColor = if (esAdultoMayor) {
                        CelesteVivido.copy(alpha = 0.12f)
                    } else {
                        AzulNegro.copy(alpha = 0.12f)
                    }

                    val chipTextColor = if (esAdultoMayor) {
                        CelesteVivido
                    } else {
                        AzulNegro
                    }

                    Box(
                        modifier = Modifier
                            .background(
                                color = chipBgColor,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = usuario.tipo.replace("_", " "),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = chipTextColor
                        )
                    }
                }
            }

            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Usuario",
                modifier = Modifier.size(24.dp),
                tint = AzulNegro.copy(alpha = 0.5f)
            )
        }
    }
}