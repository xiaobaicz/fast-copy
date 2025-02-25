package io.github.xiaobaicz.fcp

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.StandardOpenOption
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.io.path.Path

class CopyContext(
    val src: File,
    val dest: File,
    private val bufSize: Int = 512 * 1024 * 1024
) {
    companion object {
        private val task = ThreadPoolExecutor(
            0,
            Runtime.getRuntime().availableProcessors(),
            1,
            TimeUnit.MINUTES,
            SynchronousQueue(),
        ).asCoroutineDispatcher()
    }

    private var isClose = false

    var totalCount by mutableStateOf(0)
        private set

    var totalSize by mutableStateOf(0L)
        private set

    var remCount by mutableStateOf(0)
        private set

    var remSize by mutableStateOf(0L)
        private set

    var speed by mutableStateOf(0L)
        private set

    var time by mutableStateOf(0L)
        private set

    val timeFormat by derivedStateOf {
        val s = time / 1000
        return@derivedStateOf String.format("%02d:%02d", s / 60, s % 60)
    }

    val speedFormat by derivedStateOf {
        val k = speed / 1024
        if (k < 1024) return@derivedStateOf "${k}KB/s"
        val m = k / 1024
        return@derivedStateOf "${m}MB/s"
    }

    val totalSizeFormat by derivedStateOf {
        val k = totalSize / 1024
        if (k < 1024) return@derivedStateOf "${k}KB"
        val m = k / 1024
        return@derivedStateOf "${m}MB"
    }

    val remSizeFormat by derivedStateOf {
        val k = remSize / 1024
        if (k < 1024) return@derivedStateOf "${k}KB"
        val m = k / 1024
        return@derivedStateOf "${m}MB"
    }

    suspend fun run() {
        withContext(task) {
            init()
            launch {
                statistics()
            }
            start()
        }
    }

    private suspend fun statistics() {
        val start = System.currentTimeMillis()
        var lastSize = remSize
        var speedTime = 0L
        while (!isClose && remCount > 0) {
            delay(250L)
            time = System.currentTimeMillis() - start
            if (time - speedTime >= 1000L) {
                speed = lastSize - remSize
                lastSize = remSize
                speedTime = time
            }
        }
    }

    private inline fun forEach(file: (File) -> Unit) {
        val stack = arrayListOf(src)
        while (!isClose) {
            if (stack.isEmpty()) break
            val last = stack.removeLast()
            if (last.isDirectory) {
                stack.addAll(last.listFiles()?.toList() ?: listOf())
                continue
            }
            file(last)
        }
    }

    private fun init() {
        forEach {
            totalCount++
            totalSize += it.length()
        }
        remCount = totalCount
        remSize = totalSize
    }

    private fun start() {
        val baseSrcPath = src.parentFile.absolutePath
        val baseDestPath = dest.absolutePath
        val buf = ByteBuffer.allocate(bufSize)
        forEach {
            val srcPath = it.absolutePath
            val destPath = srcPath.replace(baseSrcPath, baseDestPath)
            File(destPath).parentFile?.apply { if (!exists()) mkdirs() }
            val srcChannel = FileChannel.open(Path(srcPath), StandardOpenOption.CREATE, StandardOpenOption.READ)
            val destChannel = FileChannel.open(Path(destPath), StandardOpenOption.CREATE, StandardOpenOption.WRITE)
            while (true) {
                val len = srcChannel.read(buf)
                if (len <= 0) break
                remSize -= len
                buf.flip()
                destChannel.write(buf)
                buf.compact()
            }
            destChannel.close()
            srcChannel.close()
            remCount--
        }
    }

    fun close() {
        isClose = true
    }

    override fun hashCode(): Int {
        return src.hashCode() + dest.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        other ?: return false
        if (other !is CopyContext) return false
        return src == other.src && dest == other.dest
    }
}