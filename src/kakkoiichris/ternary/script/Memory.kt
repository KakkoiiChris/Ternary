package kakkoiichris.ternary.script

import kakkoiichris.ternary.util.Stack

class Memory {
    private val globalScope = Scope()
    private val stack = Stack<Frame>()
    
    operator fun get(name: String) =
        stack.peek()?.find(name) ?: globalScope[name]
    
    operator fun set(name: String, reference: Reference) {
        if (stack.isEmpty()) {
            globalScope[name] = reference
        }
        else {
            stack.peek()?.thisScope?.set(name, reference)
        }
    }
    
    fun beginFrame() = stack.push(Frame())
    
    fun endFrame() = stack.pop()
    
    fun beginScope() = stack.peek()?.beginScope()
    
    fun endScope() = stack.peek()?.endScope()
    
    fun clear() {
        globalScope.clear()
        
        while (!stack.isEmpty()) {
            stack.pop()?.clear()
        }
        
        stack.clear()
    }
}

class Frame : MutableList<Scope> by mutableListOf() {
    val thisScope: Scope get() = get(0)
    
    fun find(name: String) = firstOrNull { it[name] != null }?.get(name)
    
    fun beginScope() = add(0, Scope())
    
    fun endScope() = removeAt(0)
}

class Scope : MutableMap<String, Reference> by mutableMapOf()

data class Reference(var value: Any)

fun Any.fromRef() =
    if (this is Reference) this.value else this