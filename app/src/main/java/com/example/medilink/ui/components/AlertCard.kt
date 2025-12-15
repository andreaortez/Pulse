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
import androidx.compose.ui.text.style.TextAlign
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
    val estado: String,
    val adultoMayorId: String? = null,
    val fechaHora: String? = null,
    val tipoAlerta: String? = null,
)

@Composable
fun AlertCard(
    alert: Alert,
    onMarkAsNotified: (String) -> Unit,
    onMarkAsResolved: (String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val titulo = when (alert.tipoAlerta?.uppercase()) {
        "SIGNOS_VITALES" -> "Alerta de signos vitales"
        else -> "Recordatorio de medicamento"
    }

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
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = titulo,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))

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
                            color = AzulOscuro,
                            textAlign = TextAlign.Center
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
                modifier = Modifier
                    .padding(top = 4.dp, bottom = 8.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                if(alert.tipoAlerta?.uppercase() != "SIGNOS_VITALES"){
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

                    Spacer(modifier = Modifier.width(12.dp))
                }

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
            }
        },
        dismissButton = {}
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
                onDismissRequest = {  }
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
        estado = "PENDIENTE",
        tipoAlerta = "SIGNOS_VITALES",
    )

    AlertsList(
        alerts = listOf(exampleAlert),
        onMarkAsNotified = { },
        onMarkAsResolved = { }
    )
}
