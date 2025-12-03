package com.example.medilink.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    val gravedad: String,
    val estado: String
)

@Composable
fun AlertCard(
    alert: Alert,
    onMarkAsNotified: (String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val cardBackgroundColor = if (alert.gravedad == "CRITICA") CelesteVivido else CelesteClaro
    val buttonTextColor = if (alert.gravedad == "CRITICA") Color.White else AzulNegro

    AlertDialog(
        icon = {
            Icon(Icons.Default.Notifications, contentDescription = "Notificacion")
        },
        title = {
            Text(text = alert.estado)
        },
        text = {
            Text(
                text = alert.mensaje,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AzulNegro,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = { onMarkAsNotified(alert.id) },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulOscuro)
            ) {
                Text(
                    text = "Notificada",
                    color = buttonTextColor,
                    fontSize = 14.sp
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onMarkAsNotified(alert.id) },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulOscuro)
            ) {
                Text(
                    text = "Notificada",
                    color = buttonTextColor,
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
    onMarkAsResolved: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        alerts.forEach { alert ->
            AlertCard(
                alert = alert,
                onMarkAsNotified = onMarkAsNotified,
                onDismissRequest = onMarkAsResolved
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AlertCardPreview() {
    val exampleAlert = Alert(
        id = "1",
        mensaje = "Alerta crítica: el valor de la presión arterial es 180/120 mmHg",
        gravedad = "CRITICA",
        estado = "PENDIENTE"
    )

    AlertsList(
        alerts = listOf(exampleAlert),
        onMarkAsNotified = {  },
        onMarkAsResolved = {  }
    )
}