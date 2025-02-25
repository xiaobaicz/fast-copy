package io.github.xiaobaicz.fcp.widgets

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface Component {
    @Composable
    fun Content(modifier: Modifier)
}