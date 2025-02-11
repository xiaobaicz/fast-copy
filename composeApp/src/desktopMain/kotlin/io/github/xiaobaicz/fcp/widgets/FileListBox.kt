package io.github.xiaobaicz.fcp.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.xiaobaicz.fcp.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.io.File
import java.util.Arrays

@Composable
@Preview
fun FileListBox(modifier: Modifier = Modifier, dir: (File) -> Unit = {}, item: @Composable FileListScope.(File) -> Unit) {
    Column(modifier = modifier.fillMaxSize().padding(8.dp).shadow(8.dp, RoundedCornerShape(8.dp)).background(Color.White)) {
        val state = rememberLazyListState()
        val fileList = remember { FileList(state) }
        val first = fileList.files.first()
        if (first.name == "..") {
            dir(first.parentFile)
        }
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(8.dp), state = state) {
            items(fileList.files, File::getAbsolutePath) { file ->
                fileList.item(file)
            }
        }
    }
}

interface FileListScope {
    suspend fun open(dir: File)
}

private data class Position(
    val index: Int,
    val offset: Int,
)

private class FileList(val state: LazyListState) : FileListScope {
    val files = mutableStateListOf<File>().apply {
        addAll(Util.getRoot())
    }

    val positions = arrayListOf(Position(0, 0))

    override suspend fun open(dir: File) {
        withContext(Dispatchers.IO) {
            openImpl(dir)
        }
    }

    private suspend fun openImpl(dir: File) {
        if (!dir.isDirectory) return
        if (dir.name == "..") {
            val parent = dir.parentFile?.parentFile
            if (parent == null) {
                withContext(Dispatchers.Main) {
                    files.clear()
                    files.addAll(Util.getRoot())
                    state.scrollToItem(0)
                }
            } else {
                openDir(parent)
            }
            withContext(Dispatchers.Main) {
                val position = positions.removeLast()
                state.scrollToItem(position.index, position.offset)
            }
            return
        }
        val position = Position(state.firstVisibleItemIndex, state.firstVisibleItemScrollOffset)
        positions.addLast(position)
        openDir(dir)
    }

    private suspend fun openDir(dir: File) {
        val back = File(dir, "..")
        val sub = dir.listFiles() ?: arrayOf()
        Arrays.sort(sub) { f1, f2 ->
            when {
                f1.isDirectory && f2.isDirectory -> f1.compareTo(f2)
                f1.isFile && f2.isFile -> f1.compareTo(f2)
                f1.isDirectory && f2.isFile -> -1
                else -> 1
            }
        }
        withContext(Dispatchers.Main) {
            files.clear()
            files.add(back)
            files.addAll(sub)
            state.scrollToItem(0)
        }
    }
}