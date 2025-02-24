package io.github.xiaobaicz.fcp.widgets

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun Title(title: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Text(title, style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold))
    }
}
