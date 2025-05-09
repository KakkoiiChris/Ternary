package kakkoiichris.ternary.util

import kakkoiichris.ternary.lexer.Lexer
import kakkoiichris.ternary.parser.Parser
import kakkoiichris.ternary.parser.Program

class Source(val name: String, val text: String){
    fun compile():Program{
        val lexer = Lexer(this)
        
        val parser = Parser(lexer)
        
        return parser.parse()
    }
}