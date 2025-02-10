package io.github.xiaobaicz.fcp

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.nio.file.Path
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.io.path.*

object Utils {
    private val maxSize = Runtime.getRuntime().availableProcessors()

    private val queue = LinkedBlockingQueue<Runnable>()

    private val pool = ThreadPoolExecutor(0, maxSize, 60L, TimeUnit.SECONDS, queue).asCoroutineDispatcher()

    suspend fun copy(src: File, toDir: File, status: (Int, Long, Long) -> Unit) {
        if (!src.exists()) return
        withContext(pool) {
            val srcPath = if (src.isDirectory) src.absolutePath else src.parentFile.absolutePath
            val dirPath = if (src.isDirectory) "${toDir.absolutePath}/${src.name}" else toDir.absolutePath
            val list = arrayListOf(src)
            val map = HashMap<Path, Path>()
            var sum = 0L

            while (list.isNotEmpty()) {
                val last = list.removeLast()
                if (last.isDirectory) {
                    list.addAll(last.listFiles())
                    continue
                }
                val s = Path(last.absolutePath)
                val d = Path(last.absolutePath.replace(srcPath, dirPath))
                map[s] = d
                sum += s.fileSize()
            }

            var count = map.size
            var oldSum = sum

            launch(Dispatchers.IO) {
                while (count > 0) {
                    delay(1000)
                    status(count, sum, oldSum - sum)
                    oldSum = sum
                }
            }

            val mutex = Mutex()

            map.forEach { s, d ->
                launch {
                    if (!d.parent.exists()) {
                        d.parent.createDirectories()
                    }
                    s.copyTo(d, true)
                    d.setLastModifiedTime(s.getLastModifiedTime())
                    mutex.withLock {
                        sum -= s.fileSize()
                        count--
                    }
                }
            }
        }
    }
}