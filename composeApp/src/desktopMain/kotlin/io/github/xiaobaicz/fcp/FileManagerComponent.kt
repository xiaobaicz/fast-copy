package io.github.xiaobaicz.fcp

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fast_copy.composeapp.generated.resources.Res
import fast_copy.composeapp.generated.resources.doc
import fast_copy.composeapp.generated.resources.drive
import fast_copy.composeapp.generated.resources.floder
import fast_copy.composeapp.generated.resources.home
import fast_copy.composeapp.generated.resources.upper
import io.github.xiaobaicz.fcp.widgets.Component
import org.jetbrains.compose.resources.vectorResource
import java.io.File

class FileManagerComponent : Component, FileManager by FileManager.create() {
    private val items = mutableStateListOf<File>()

    private var currentDir by mutableStateOf<File?>(null)

    private var select by mutableStateOf<File?>(null)

    private var isShowHiddenState by mutableStateOf(isShowHidden)

    private var isOnlyDirState by mutableStateOf(isOnlyDir)

    init {
        openHome()
        reset(list)
    }

    private fun reset(list: List<File>) {
        select = null
        currentDir = dir
        items.clear()
        items.addAll(list)
    }

    @Composable
    private fun Toolbar(modifier: Modifier = Modifier) {
        Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
            Tool(vectorResource(Res.drawable.upper), "上一级") {
                back()
                reset(list)
            }
            Tool(vectorResource(Res.drawable.drive), "硬盘") {
                openDrive()
                reset(list)
            }
            Tool(vectorResource(Res.drawable.home), "主页") {
                openHome()
                reset(list)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(isShowHiddenState, {
                    isShowHiddenState = it
                    isShowHidden = it
                    reset(list)
                }, modifier = Modifier.size(40.dp).padding(8.dp))
                Text(
                    text = "隐藏文件",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(isOnlyDirState, {
                    isOnlyDirState = it
                    isOnlyDir = it
                    reset(list)
                }, modifier = Modifier.size(40.dp).padding(8.dp))
                Text(
                    text = "仅文件夹",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    @Composable
    private fun Tool(image: ImageVector, text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
        Box(modifier = modifier.clickable(onClick = onClick)) {
            Icon(image, text, modifier = Modifier.size(40.dp).padding(8.dp))
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun FileItem(file: File, modifier: Modifier = Modifier) {
        val isSelect = file == select
        val mod = Modifier.fillMaxWidth()
            .height(54.dp)
            .combinedClickable(onDoubleClick = {
                if (open(file)) reset(list)
            }) {
                select = file
            }
            .then(modifier)
            .let { if (isSelect) it.background(Color(0x11000000)) else it }
            .padding(horizontal = 8.dp)
        Row(modifier = mod, verticalAlignment = Alignment.CenterVertically) {
            val icon = if (file.isDirectory) Res.drawable.floder else Res.drawable.doc
            Image(vectorResource(icon), "文件", modifier = Modifier.padding(end = 8.dp).size(32.dp))
            Text(file.name.ifEmpty { file.absolutePath }, color = Color.Black, maxLines = 1)
        }
    }

    @Composable
    override fun Content(modifier: Modifier) {
        Column(modifier = modifier.shadow(8.dp, shape = RoundedCornerShape(8.dp)).background(Color.White)) {
            Toolbar(modifier = Modifier.padding(horizontal = 8.dp).padding(top = 8.dp))
            currentDir?.apply {
                Text(
                    absolutePath,
                    modifier = Modifier.padding(horizontal = 8.dp).padding(top = 8.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 8.dp)) {
                items(items, key = { it.absolutePath }, contentType = { it.isDirectory }) {
                    FileItem(it)
                }
            }
        }
    }
}
