package io.github.xiaobaicz.fcp.widgets

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropTransferAction
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.DragAndDropTransferable
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.awt.datatransfer.StringSelection

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
@Preview
fun SourceBox(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val textMeasurer = rememberTextMeasurer()
    FileListBox(modifier) { file ->
        FileBox(file, modifier = Modifier.dragAndDropSource({
            drawText(textMeasurer, file.name)
        }) {
            detectTapGestures(onLongPress = {
                val stringSelection = StringSelection(file.absolutePath)
                val transferable = DragAndDropTransferable(stringSelection)
                startTransfer(DragAndDropTransferData(transferable, listOf(DragAndDropTransferAction.Move)))
            }, onDoubleTap = {
                scope.launch {
                    open(file)
                }
            })
        })
    }
}