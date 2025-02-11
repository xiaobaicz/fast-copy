package io.github.xiaobaicz.fcp.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fast_copy.composeapp.generated.resources.Res
import fast_copy.composeapp.generated.resources.doc
import fast_copy.composeapp.generated.resources.floder
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.io.File

@Composable
@Preview
fun FileBox(file: File, modifier: Modifier = Modifier) {
    val mod = Modifier.fillMaxWidth()
        .height(54.dp)
        .clip(RoundedCornerShape(8.dp))
        .then(modifier)
        .padding(horizontal = 8.dp)
    Row(modifier = mod, verticalAlignment = Alignment.CenterVertically) {
        val icon = if (file.isDirectory) Res.drawable.floder else Res.drawable.doc
        Image(vectorResource(icon), "floder", modifier = Modifier.padding(end = 8.dp).size(32.dp))
        Text(if (file.name.isEmpty()) file.absolutePath else file.name, color = Color.Black)
    }
}