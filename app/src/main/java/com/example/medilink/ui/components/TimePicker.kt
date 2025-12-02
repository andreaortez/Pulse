package com.example.medilink.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import java.util.Calendar

// Colores
import com.example.medilink.ui.theme.Celeste
import com.example.medilink.ui.theme.AzulNegro
import com.example.medilink.ui.theme.Azul
import com.example.medilink.ui.theme.AzulOscuro
import com.example.medilink.ui.theme.CelesteVivido2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialTimePicker(
    onConfirm: (TimePickerState) -> Unit,
    onDismiss: () -> Unit,
) {
    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )

    TimePickerDialog(
        onDismiss = { onDismiss() },
        onConfirm = { onConfirm(timePickerState) }
    ) {
        TimePicker(
            state = timePickerState,
            colors = TimePickerDefaults.colors(
                clockDialColor = CelesteVivido2,
                selectorColor = Azul,
                timeSelectorSelectedContainerColor = Celeste,
                timeSelectorSelectedContentColor = Color.White,
                timeSelectorUnselectedContainerColor =  Color.White,
                timeSelectorUnselectedContentColor = AzulOscuro
            )
        )
    }
}

@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancelar", color = AzulNegro)
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text("Aceptar", color = Azul)
            }
        },
        text = { content() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun TimePickerDialogPreview() {
    MaterialTheme {
        DialTimePicker(
            onConfirm = {},
            onDismiss = {}
        )
    }
}