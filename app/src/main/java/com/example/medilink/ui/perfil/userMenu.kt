package com.example.medilink.ui.perfil

import android.R.id
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medilink.R

// Colores
import com.example.medilink.ui.theme.AzulOscuro

enum class ProfileOptionType {
    EDITPROFILE,
    VINCULATE,
    LANGUAGE,
    LOCATION,
    SUBSCRIPTION,
    CLEAR_CACHE,
    CLEAR_HISTORY,
    LOGOUT
}

@Composable
fun ProfileScreen(
    idUsuario: String,
    userName: String = "Charlotte King",
    onBackClick: () -> Unit = {},
    onOptionClick: (ProfileOptionType) -> Unit = {}
) {
    val backgroundColor = Color(0xFFF5F5F5)
    val accentColor = Color(0xFFF44336)
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Imagen de fondo
        Image(
            painter = painterResource(id = R.drawable.bg3),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Mi Perfil",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // CARD PRINCIPAL
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar centrado
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.adulto_mayor),
                                contentDescription = "Avatar",
                                modifier = Modifier.matchParentSize()
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Nombre centrado debajo del avatar
                        Text(
                            text = userName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            color = Color(0xFF222222)
                        )

                        // id debajo del nombre
                        Text(
                            text = "id: $idUsuario",
                            fontSize = 25.sp,
                            color = Color(0xFF888888)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // LISTA DE OPCIONES
                        ProfileOptionRow(
                            icon = Icons.Default.Create,
                            label = "Editar Perfil",
                        ) { onOptionClick(ProfileOptionType.EDITPROFILE) }

                        ProfileOptionRow(
                            icon = Icons.Default.Face,
                            label = "Vincular Familiar",
                        ) { onOptionClick(ProfileOptionType.VINCULATE) }

                        ProfileOptionRow(
                            icon = Icons.Default.Face,
                            label = "Language",
                        ) { onOptionClick(ProfileOptionType.LANGUAGE) }

                        ProfileOptionRow(
                            icon = Icons.Default.LocationOn,
                            label = "Location",
                        ) { onOptionClick(ProfileOptionType.LOCATION) }

                        ProfileOptionRow(
                            icon = Icons.Outlined.Star,
                            label = "Subscription",
                        ) { onOptionClick(ProfileOptionType.SUBSCRIPTION) }

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        ProfileOptionRow(
                            icon = Icons.Default.Delete,
                            label = "Clear cache",
                        ) { onOptionClick(ProfileOptionType.CLEAR_CACHE) }

                        ProfileOptionRow(
                            icon = Icons.Default.Refresh,
                            label = "Clear history",
                        ) { onOptionClick(ProfileOptionType.CLEAR_HISTORY) }

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        ProfileOptionRow(
                            icon = Icons.Default.ExitToApp,
                            label = "Cerrar Sesión",
                            labelColor = AzulOscuro
                        ) { onOptionClick(ProfileOptionType.LOGOUT) }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileOptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    labelColor: Color = Color(0xFF222222),
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color(0xFF444444)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            fontSize = 23.sp,
            color = labelColor,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Outlined.KeyboardArrowRight,
            contentDescription = "Go to $label",
            tint = Color(0xFFAAAAAA)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    MaterialTheme {
        ProfileScreen(
            idUsuario = "demo",
            userName = "Charlotte King"
        )
    }
}
