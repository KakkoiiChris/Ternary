package kakkoiichris.ternary.util

class Stack<T> {
    private val elements = mutableListOf<T>()

    val count get() = elements.size

    fun isEmpty() = elements.isEmpty()

    fun push(item: T) = elements.add(item)

    fun pop(): T? = if (isEmpty())
        null
    else
        elements.removeAt(count - 1)

    fun peek(): T? = elements.lastOrNull()

    fun clear() = elements.clear()

    override fun toString(): String = elements.toString()
}

fun <T> Stack<T>.push(items: Collection<T>) = items.forEach { push(it) }