package com.example.medilink.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medilink.ui.theme.CelesteVivido
import com.example.medilink.ui.theme.AzulNegro
import com.example.medilink.ui.theme.AzulOscuro
import com.example.medilink.ui.theme.CelesteClaro

data class Alert(
    val id: String,
    val mensaje: String,
    val estado: String
)

@Composable
fun AlertCard(
    alert: Alert,
    onMarkAsNotified: (String) -> Unit,
    onMarkAsResolved: (String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White,
        iconContentColor = Color.White,
        textContentColor = AzulNegro,
        titleContentColor = AzulOscuro,
        icon = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(color = CelesteVivido, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notificación de medicamento",
                    tint = Color.White
                )
            }
        },
        title = {
            Column {
                Text(
                    text = "Recordatorio de medicamento",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))

                // 🔹 Chip centrado horizontalmente
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Surface(
                        color = CelesteClaro,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = alert.estado,
                            modifier = Modifier
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AzulOscuro
                        )
                    }
                }
            }
        },
        text = {
            Text(
                text = alert.mensaje,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = AzulNegro,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )
        },
        confirmButton = {
            Button(
                onClick = { onMarkAsNotified(alert.id) },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CelesteVivido,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Notificada",
                    fontSize = 14.sp
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = { onMarkAsResolved(alert.id) },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = AzulOscuro
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 1.dp,
                    brush = androidx.compose.ui.graphics.SolidColor(AzulOscuro)
                )
            ) {
                Text(
                    text = "Resuelta",
                    fontSize = 14.sp
                )
            }
        }
    )
}

@Composable
fun AlertsList(
    alerts: List<Alert>,
    onMarkAsNotified: (String) -> Unit,
    onMarkAsResolved: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        alerts.forEach { alert ->
            AlertCard(
                alert = alert,
                onMarkAsNotified = onMarkAsNotified,
                onMarkAsResolved = onMarkAsResolved,
                onDismissRequest = { /* opcional */ }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AlertCardPreview() {
    val exampleAlert = Alert(
        id = "1",
        mensaje = "Te toca tomar Loratadina 10 mg.",
        estado = "PENDIENTE"
    )

    AlertsList(
        alerts = listOf(exampleAlert),
        onMarkAsNotified = { },
        onMarkAsResolved = { }
    )
}
