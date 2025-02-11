package io.github.xiaobaicz.fcp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Path
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.io.path.Path
import kotlin.io.path.copyTo
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.fileSize
import kotlin.io.path.getLastModifiedTime
import kotlin.io.path.setLastModifiedTime

class Util(val src: File, val toDir: File) {
    companion object {
        private val maxSize = Runtime.getRuntime().availableProcessors()
        private val queue = LinkedBlockingQueue<Runnable>()
        private val pool = ThreadPoolExecutor(0, maxSize, 60L, TimeUnit.SECONDS, queue).asCoroutineDispatcher()

        fun getRoot(): List<File> {
            return FileSystems.getDefault().rootDirectories.map { it.toFile() }.sorted()
        }
    }

    var remCount by mutableIntStateOf(0)
        private set
    var remSum by mutableLongStateOf(0L)
        private set
    var speed by mutableLongStateOf(0L)
        private set
    var time by mutableIntStateOf(0)
        private set

    val title get() = "${src.name}  =>  ${toDir.absolutePath}"

    suspend fun copy() {
        if (!src.exists()) return
        if (utilList.contains(this)) return
        utilList.add(this)
        withContext(pool) {
            val srcPath = if (src.isDirectory) src.absolutePath else src.parentFile.absolutePath
            val dirPath = if (src.isDirectory) "${toDir.absolutePath}/${src.name}" else toDir.absolutePath
            val list = arrayListOf(src)
            val map = HashMap<Path, Path>()

            var sum = 0L
            while (list.isNotEmpty()) {
                val last = list.removeLast()
                if (last.isDirectory) {
                    list.addAll(last.listFiles() ?: arrayOf())
                    continue
                }
                val s = Path(last.absolutePath)
                val d = Path(last.absolutePath.replace(srcPath, dirPath))
                map[s] = d
                sum += s.fileSize()
            }

            remCount = map.size
            remSum = sum

            launch(Dispatchers.IO) {
                var oldSum = remSum
                while (remCount > 0) {
                    delay(1000L)
                    time++
                    speed = oldSum - remSum
                    oldSum = remSum
                }
            }

            val mutex = Mutex()

            map.forEach { (s, d) ->
                launch {
                    if (!d.parent.exists()) {
                        d.parent.createDirectories()
                    }
                    s.copyTo(d, true)
                    d.setLastModifiedTime(s.getLastModifiedTime())
                    mutex.withLock {
                        remSum -= s.fileSize()
                        remCount--
                    }
                }
            }
        }
        utilList.remove(this)
    }

    override fun hashCode(): Int {
        return src.hashCode() + toDir.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        other ?: return false
        if (other !is Util) return false
        return src == other.src && toDir == other.toDir
    }
}