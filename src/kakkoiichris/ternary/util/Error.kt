package kakkoiichris.ternary.util

import kakkoiichris.ternary.lexer.Location
import kakkoiichris.ternary.lexer.Token
import kakkoiichris.ternary.lexer.TokenType
import kakkoiichris.ternary.parser.Expr

class TernaryError(val subMessage: String, val loc: Location) : Throwable() {
    private val stackTrace = mutableListOf<String>()
    
    operator fun plusAssign(trace: String) {
        stackTrace += trace
    }
    
    override fun toString() = buildString {
        append("Ternary Error: $subMessage $loc\n")
        
        for (trace in stackTrace) {
            append("@ $trace\n")
        }
    }
}

fun emptyAssignmentError(loc: Location): Nothing =
    throw TernaryError("No value is assigned!", loc)

fun illegalCharacterError(char: Char, loc: Location): Nothing =
    throw TernaryError("Character '$char' is illegal!", loc)

fun illegalTokenTypeError(type: TokenType, loc: Location): Nothing =
    throw TernaryError("Token type '$type' is illegal!", loc)

fun invalidNumberError(literal: String, loc: Location): Nothing =
    throw TernaryError("Number literal $literal is invalid!", loc)

fun invalidUnaryOperandError(operand: Any, operator: Expr.Unary.Operator, loc: Location): Nothing =
    throw TernaryError("Unary operand '$operand' for '$operator' operator is invalid!", loc)

fun invalidLeftOperandError(operand: Any, operator: Expr.Binary.Operator, loc: Location): Nothing =
    throw TernaryError("Left operand '$operand' for '$operator' operator is invalid!", loc)

fun invalidRightOperandError(operand: Any, operator: Expr.Binary.Operator, loc: Location): Nothing =
    throw TernaryError("Right operand '$operand' for '$operator' operator is invalid!", loc)

fun undefinedSymbolError(name: String, loc: Location): Nothing =
    throw TernaryError("Symbol '$name' is undefined!", loc)

fun unexpectedError(message: String): Nothing =
    error(message)