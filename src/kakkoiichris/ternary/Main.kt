package kakkoiichris.ternary

import kakkoiichris.ternary.lexer.Lexer
import kakkoiichris.ternary.parser.Parser
import kakkoiichris.ternary.script.Script
import kakkoiichris.ternary.util.Source
import java.io.File
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

fun main(args: Array<String>) = when (args.size) {
    0    -> repl()
    
    1    -> file(args[0])
    
    else -> error("Usage: ternary (fileName)?")
}

private fun repl() {
    println(
        """
    |*************************************************
    |* Ternary REPL                                  *
    |* Copyright (C) 2019, Christian Bryce Alexander *
    |*************************************************
    |
    |""".trimMargin()
    )
    
    while (true) {
        print("?(:) ")
        val text = readlnOrNull()?.takeIf { it.isNotBlank() } ?: break
        println()
        
        val source = Source("?(:)", text)
        
        run(source)
    }
}

private fun file(path: String) {
    val file = File(path)
    
    val name = file.name
    val text = file.readText()
    
    val source = Source(name, text)
    
    run(source)
}

@OptIn(ExperimentalTime::class)
private fun run(source: Source) {
    try {
        val (program, compileTime) = measureTimedValue { source.compile() }
        
        println("[PARSER] $compileTime ms")
        
        val script = Script(program)
        
        val (result, runTime) = measureTimedValue { script.run() }
        
        println("[SCRIPT] $runTime ms")
        
        println("[DONE] $result")
    }
    catch (e: Exception) {
        e.printStackTrace()
        
        Thread.sleep(15)
    }
}