package io.github.xiaobaicz.fcp

import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Path

interface FileManager {
    val dir: File?
    val list: List<File>
    var isShowHidden: Boolean
    var isOnlyDir: Boolean
    fun open(dir: File): Boolean
    fun openDrive()
    fun openHome()
    fun back(): Boolean

    companion object {
        fun create(): FileManager = FileManagerImpl()
    }

    private class FileManagerImpl : FileManager {
        private val rootList get() = FileSystems.getDefault().rootDirectories.map(Path::toFile).sorted()

        override var dir: File? = null
            private set

        override var list: List<File> = rootList
            private set

        override var isShowHidden: Boolean = false
            set(value) {
                field = value
                openImpl(dir)
            }

        override var isOnlyDir: Boolean = false
            set(value) {
                field = value
                openImpl(dir)
            }

        private fun openImpl(dir: File?): Boolean {
            if (dir == null) {
                this.dir = null
                this.list = rootList
                return true
            }
            if (!dir.isDirectory) return false
            this.dir = dir
            this.list = dir.listFiles()
                .filter { file -> isShowHidden || !file.isHidden }
                .filter { file -> !isOnlyDir || file.isDirectory  }
                .sorted()
            return true
        }

        override fun open(dir: File): Boolean {
            return openImpl(dir)
        }

        override fun openDrive() {
            openImpl(null)
        }

        override fun openHome() {
            openImpl(File(System.getProperty("user.home")))
        }

        override fun back(): Boolean {
            val dir = dir ?: return false
            if (dir.parentFile == null) {
                this.dir = null
                this.list = rootList
                return true
            }
            return open(dir.parentFile)
        }
    }
}