package com.bestapp.zipbab.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SquareButton(modifier: Modifier, text: String, onClick: () -> Unit) {
    Button(
        modifier = modifier,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = MainColor), // Replace with your main_color
        shape = RoundedCornerShape(8.dp) // Replace with your desired corner radius
    ) {
        Text(
            text = text,
            color = LocalCustomColorsPalette.current.defaultBackgroundColor,
            fontSize = 20.sp,
            fontFamily = PretendardRegular,
        )
    }
}