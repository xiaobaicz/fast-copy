package io.github.xiaobaicz.fcp

interface Stack<T> {
    val isEmpty: Boolean

    fun push(t: T)

    fun push(t: Collection<T>) {
        for (i in t) {
            push(i)
        }
    }

    fun push(t: Array<T>) {
        for (i in t) {
            push(i)
        }
    }

    fun pop(): T

    fun peek(): T

    fun safePop(): T? {
        if (isEmpty) return null
        return pop()
    }

    fun safePeek(): T? {
        if (isEmpty) return null
        return peek()
    }
}

class ArrayStack<T> : Stack<T> {
    private var cap = 8

    private var ptr = 0

    private var raw = newRaw(cap)

    override val isEmpty: Boolean get() = ptr == 0

    private val isFull: Boolean get() = ptr == cap

    private fun newRaw(cap: Int): Array<Any?> {
        return Array(cap) { null }
    }

    @Suppress("UNCHECKED_CAST")
    override fun pop(): T {
        return raw[--ptr] as T
    }

    @Suppress("UNCHECKED_CAST")
    override fun peek(): T {
        return raw[ptr - 1] as T
    }

    override fun push(t: T) {
        if (isFull) {
            val dest = newRaw(cap shl 1)
            System.arraycopy(raw, 0, dest, 0, cap)
            cap = cap shl 1
            raw = dest
        }
        raw[ptr++] = t
    }
}

fun <T> arrayStack(vararg e: T): Stack<T> = ArrayStack<T>().apply {
    for (t in e) {
        push(t)
    }
}