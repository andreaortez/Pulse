package com.example.medilink.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medilink.R
import com.example.medilink.ui.theme.CelesteVivido
import com.example.medilink.ui.theme.CelesteClaro

import com.example.medilink.ui.Components.MedicineFormChip
import com.example.medilink.ui.Components.TimeReminderCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineScreen(
    onBackClick: () -> Unit = {},
    onDoneClick: () -> Unit = {}
) {
    var medicineName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") } // por si luego lo usas
    var amount by remember { mutableStateOf("1") }
    var duration by remember { mutableStateOf("2") }
    var selectedForm by remember { mutableStateOf(0) }
    var selectedTime by remember { mutableStateOf("08:00 a.m.") }

    val medicineIcons = listOf(
        "Cápsula" to R.drawable.ic_capsule,
        "Tableta" to R.drawable.ic_tablet,
        "Solución" to R.drawable.ic_solution,
        "Gotas" to R.drawable.ic_gutte
    )

    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF2196F3),
            Color(0xFF1565C0)
        )
    )

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(gradient)
            ) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Añadir Medicamentos",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        },
        bottomBar = {
            Button(
                onClick = onDoneClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .height(50.dp)
                    .background(gradient, shape = RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                )
            ) {
                Text("Añadir", color = Color.White, fontSize = 20.sp)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Nombre del medicamento
            Text(
                text = "Nombre del medicamento",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = medicineName,
                onValueChange = { medicineName = it },
                placeholder = { Text("Ibuprofeno, 200 mg") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(12.dp)
            )

            // Cantidad y duración
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // CANTIDAD
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Cantidad",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp),
                        color = Color.DarkGray,
                    )

                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        placeholder = { Text(amount) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        trailingIcon = {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(85.dp)
                                    .background(
                                        color = CelesteVivido,
                                        shape = RoundedCornerShape(
                                            topEnd = 12.dp,
                                            bottomEnd = 12.dp
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "pastilla(s)",
                                    color = Color.White,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    )
                }

                // DURACIÓN
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Duración",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp),
                        color = Color.DarkGray,
                    )

                    OutlinedTextField(
                        value = duration,
                        onValueChange = { duration = it },
                        placeholder = { Text(duration) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        trailingIcon = {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(60.dp)
                                    .background(
                                        color = CelesteVivido,
                                        shape = RoundedCornerShape(
                                            topEnd = 12.dp,
                                            bottomEnd = 12.dp
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "día(s)",
                                    color = Color.White,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    )
                }
            }

            // Tipo de medicamento
            Text(
                text = "Tipo de medicamento",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp),
                color = Color.DarkGray,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                medicineIcons.forEachIndexed { index, (form, iconRes) ->
                    val isSelected = selectedForm == index

                    Box(modifier = Modifier.weight(1f)) {
                        MedicineFormChip(
                            text = form,
                            iconRes = iconRes,
                            selected = isSelected,
                            onClick = { selectedForm = index }
                        )
                    }
                }
            }

            // Time selection
            TimeReminderCard(
                label = "Hora",
                time = selectedTime,
                onAddClick = {
                    // Aquí luego puedes abrir un TimePicker y actualizar selectedTime
                    // por ahora lo dejamos fijo o cambias a otro valor para probar
                    // selectedTime = "09:30"
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddMedicineScreenPreview() {
    MaterialTheme {
        AddMedicineScreen()
    }
}
