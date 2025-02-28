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
import java.util.concurrent.LinkedBlockingQueue
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
            LinkedBlockingQueue(),
        ).asCoroutineDispatcher()

        private val info = ThreadPoolExecutor(
            0,
            1,
            1,
            TimeUnit.MINUTES,
            LinkedBlockingQueue(),
        ).asCoroutineDispatcher()
    }

    private val baseSrcPath = src.parentFile.absolutePath

    private val baseDestPath = dest.absolutePath

    private var isClose = false

    private var isEnd = false

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
            statistics()
            launch(info) {
                monitor()
            }
            start()
        }
    }

    private fun statistics() {
        forEach(true) {
            totalCount++
            totalSize += it.length()
        }
        remCount = totalCount
        remSize = totalSize
    }

    private suspend fun monitor() {
        val start = System.currentTimeMillis()
        var lastSize = remSize
        var speedTime = 0L
        while (!isClose && !isEnd) {
            delay(250L)
            time = System.currentTimeMillis() - start
            if (time - speedTime >= 1000L) {
                speed = lastSize - remSize
                lastSize = remSize
                speedTime = time
            }
        }
    }

    private fun start() {
        val buf = ByteBuffer.allocate(bufSize)
        forEach {
            val srcPath = it.absolutePath
            val destPath = srcPath.replace(baseSrcPath, baseDestPath)
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
        isEnd = true
    }

    private inline fun forEach(mkdir: Boolean = false, file: (File) -> Unit) {
        val stack = arrayStack(src)
        while (!isClose) {
            if (stack.isEmpty) break
            val last = stack.pop()
            if (last.isDirectory) {
                if (mkdir) {
                    val destPath = last.absolutePath.replace(baseSrcPath, baseDestPath)
                    val destDir = File(destPath)
                    if (!destDir.exists()) {
                        destDir.mkdirs()
                    }
                }
                stack.push(last.listFiles() ?: arrayOf())
                continue
            }
            file(last)
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