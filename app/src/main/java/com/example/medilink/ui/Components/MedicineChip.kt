package com.example.medilink.ui.Components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.medilink.ui.theme.CelesteVivido

@Composable
fun MedicineFormChip(
    text: String,
    iconRes: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    val background = if (selected) CelesteVivido else Color.White
    val contentColor = if (selected) Color.White else CelesteVivido
    val borderColor = if (selected) CelesteVivido else Color.LightGray

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = background,
        tonalElevation = if (selected) 4.dp else 1.dp,
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .height(90.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(35.dp),
                tint = contentColor
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = contentColor
            )
        }
    }
}
