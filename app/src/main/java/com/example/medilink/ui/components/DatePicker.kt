package com.example.medilink.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.rememberDateRangePickerState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview

import com.example.medilink.ui.theme.CelesteVivido
import com.example.medilink.ui.theme.AzulNegro
import com.example.medilink.ui.theme.Azul
import com.example.medilink.ui.theme.AzulOscuro
import com.example.medilink.ui.theme.CelesteClaro
import com.example.medilink.ui.theme.CelesteVivido2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerRange(
    startDateDisplay: String,
    endDateDisplay: String,
    onDatesSelected: (startDisplay: String, endDisplay: String, startBackend: String, endBackend: String) -> Unit
) {
    var openDialog by remember { mutableStateOf(false) }
    val dateRangeState = rememberDateRangePickerState()

    val displayFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val backendFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        Text(
            text = "Fechas del tratamiento",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable { openDialog = true }
        ) {
            OutlinedTextField(
                value = if (startDateDisplay.isNotEmpty() && endDateDisplay.isNotEmpty())
                    "$startDateDisplay  -  $endDateDisplay"
                else
                    "",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.matchParentSize(),
                placeholder = {
                    Text(
                        "Selecciona rango de fechas",
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Elegir rango de fechas",
                        tint = AzulNegro
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.Black,
                    disabledBorderColor = Color.Gray,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                enabled = false,
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = AzulNegro
                )
            )
        }
    }

    if (openDialog) {
        DatePickerDialog(
            onDismissRequest = { openDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val startMillis = dateRangeState.selectedStartDateMillis
                        val endMillis = dateRangeState.selectedEndDateMillis

                        if (startMillis != null && endMillis != null) {
                            val startDate = Date(startMillis)
                            val endDate = Date(endMillis)

                            val startDisplay = displayFormat.format(startDate)
                            val endDisplay = displayFormat.format(endDate)

                            val startBackend = backendFormat.format(startDate)
                            val endBackend = backendFormat.format(endDate)

                            onDatesSelected(startDisplay, endDisplay, startBackend, endBackend)
                        }

                        openDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Azul
                    ),
                    shape = RoundedCornerShape(8.dp)

                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { openDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Azul
                    ),
                ) {
                    Text("Cancelar")
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = Color.White
            )
        ) {
            DateRangePicker(
                state = dateRangeState,
                title = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AzulOscuro, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                            .padding(vertical = 16.dp, horizontal = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = CelesteVivido,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Selecciona el rango de fechas",
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                },
                colors = DatePickerDefaults.colors(
                    // Header
                    titleContentColor = AzulOscuro,
                    headlineContentColor = AzulNegro,

                    // Calendario
                    todayContentColor = AzulNegro,
                    todayDateBorderColor = CelesteVivido2,
                    selectedDayContainerColor = Azul,
                    selectedDayContentColor = Color.White,
                    dayContentColor = AzulOscuro,
                    weekdayContentColor = AzulNegro,

                    dayInSelectionRangeContainerColor = CelesteClaro,
                    dayInSelectionRangeContentColor = AzulNegro,

                    // Botones año/mes
                    yearContentColor = AzulOscuro,
                    selectedYearContainerColor = CelesteVivido,
                    selectedYearContentColor = Color.White,
                    currentYearContentColor = Azul,
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DatePickerRangePreview() {
    var startDateDisplay by remember { mutableStateOf("") }
    var endDateDisplay by remember { mutableStateOf("") }
    var startDateBackend by remember { mutableStateOf("") }
    var endDateBackend by remember { mutableStateOf("") }

    MaterialTheme {
        DatePickerRange(
            startDateDisplay = startDateDisplay,
            endDateDisplay = endDateDisplay
        ) { startDisp, endDisp, startBack, endBack ->
            startDateDisplay = startDisp
            endDateDisplay = endDisp
            startDateBackend = startBack
            endDateBackend = endBack
        }
    }
}