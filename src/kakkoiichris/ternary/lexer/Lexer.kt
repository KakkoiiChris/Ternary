package kakkoiichris.ternary.lexer

import kakkoiichris.ternary.util.Source
import kakkoiichris.ternary.util.illegalCharacterError
import kakkoiichris.ternary.util.invalidNumberError

class Lexer(private val source: Source) : Iterator<Token<*>> {
    companion object {
        private const val NUL = '\u0000'
    }

    private val keywords = Keyword.entries.associateBy { it.rep }

    private val literals = mapOf(
        "true" to 1.0,
        "false" to 0.0,
        "nan" to Double.NaN,
        "infinity" to Double.POSITIVE_INFINITY
    ).mapValues { Value(it) }

    private var pos = 0
    private var row = 1
    private var col = 1

    override fun hasNext() =
        pos <= source.text.length

    override fun next(): Token<*> {
        while (!atEndOfFile()) {
            if (match("''")) {
                skipLineComment()

                continue
            }

            if (match('\'')) {
                skipBlockComment()

                continue
            }

            if (match { isWhitespace() }) {
                skipWhitespace()

                continue
            }

            return when {
                match { isDigit() }  -> number()

                match { isLetter() } -> word()

                match('"')           -> string()

                else                 -> symbol()
            }
        }

        return Token(here(), End)
    }

    private fun peek(offset: Int = 0) =
        if (pos + offset in source.text.indices)
            source.text[pos + offset]
        else
            NUL

    private fun look(length: Int = 1) = buildString {
        repeat(length) { i ->
            append(peek(i))
        }
    }

    private fun match(char: Char) =
        peek() == char

    private fun match(predicate: Char.() -> Boolean) =
        peek().predicate()

    private fun match(string: String) =
        look(string.length) == string

    private fun step(offset: Int = 1) {
        repeat(offset) {
            when {
                match("\r\n")            -> {
                    row++
                    col = 1
                    pos++
                }

                match { this in "\r\n" } -> {
                    row++
                    col = 1
                }

                else                     -> col++
            }

            pos++
        }
    }

    private fun skip(char: Char) =
        if (match(char)) {
            step()
            true
        }
        else false

    private fun skip(predicate: Char.() -> Boolean) =
        if (match(predicate)) {
            step()
            true
        }
        else false

    private fun skip(string: String) =
        if (match(string)) {
            step()
            true
        }
        else false

    private fun atEndOfFile() =
        match(NUL)

    private fun here() =
        Location(source.name, row, col)

    private fun skipLineComment() {
        skip("''")

        while (!skip('\n')) {
            step()
        }
    }

    private fun skipBlockComment() {
        skip('\'')

        while (!skip('\'')) {
            if (atEndOfFile()) {
                TODO("EOF IN BLOCK COMMENT")
            }

            step()
        }
    }

    private fun skipWhitespace() {
        while (skip { isWhitespace() }) Unit
    }

    private fun StringBuilder.take() {
        append(peek())

        step()
    }

    private fun number(): Token<Value> {
        val location = here()

        val result = buildString {
            do {
                take()
            }
            while (match { isDigit() })

            if (match('.')) {
                do {
                    take()
                }
                while (match { isDigit() })
            }

            if (match { this in "Ee" }) {
                take()

                do {
                    take()
                }
                while (match { isDigit() })
            }
        }

        val number = result.toDoubleOrNull() ?: invalidNumberError(result, location)

        return Token(location, Value(number))
    }

    private fun word(): Token<*> {
        val location = here()

        val result = buildString {
            do {
                take()
            }
            while (match { isLetterOrDigit() || this == '_' })
        }

        val type = when (result) {
            in keywords -> keywords[result]!!

            in literals -> literals[result]!!

            else        -> Name(result)
        }

        return Token(location, type)
    }

    private fun unicode(length: Int): Char {
        val value = look(length).toIntOrNull(16) ?: TODO("INVALID UNICODE")

        step(length)

        return value.toChar()
    }

    private fun string(): Token<Value> {
        val location = here()

        skip('"')

        val result = buildString {
            loop@ while (!skip('"')) {
                if (skip('\\')) {
                    append(
                        when {
                            skip('\\') -> '\\'

                            skip('"')  -> '"'

                            skip('b')  -> '\b'

                            skip('f')  -> '\u000c'

                            skip('n')  -> '\n'

                            skip('r')  -> '\r'

                            skip('t')  -> '\t'

                            skip('x')  -> unicode(2)

                            skip('u')  -> unicode(4)

                            else       -> TODO("INVALID CHAR")
                        }
                    )
                }
                else {
                    take()
                }
            }
        }

        return Token(location, Value(result))
    }

    private fun symbol(): Token<Symbol> {
        val location = here()

        val op = when {
            skip('+') -> when {
                skip('=') -> Symbol.PLUS_EQUAL

                else      -> Symbol.PLUS
            }

            skip('-') -> when {
                skip('=') -> Symbol.DASH_EQUAL

                else      -> Symbol.DASH
            }

            skip('*') -> when {
                skip('=') -> Symbol.STAR_EQUAL

                else      -> Symbol.STAR
            }

            skip('/') -> when {
                skip('=') -> Symbol.SLASH_EQUAL

                else      -> Symbol.SLASH
            }

            skip('%') -> when {
                skip('=') -> Symbol.PERCENT_EQUAL

                else      -> Symbol.PERCENT
            }

            skip('(') -> Symbol.LEFT_PAREN

            skip(')') -> Symbol.RIGHT_PAREN

            skip('[') -> Symbol.LEFT_SQUARE

            skip(']') -> Symbol.RIGHT_SQUARE

            skip('{') -> Symbol.LEFT_BRACE

            skip('}') -> Symbol.RIGHT_BRACE

            skip('<') -> when {
                skip('>') -> Symbol.LESS_GREATER

                skip('=') -> Symbol.LESS_EQUAL

                else      -> Symbol.LESS
            }

            skip('>') -> when {
                skip('<') -> Symbol.GREATER_LESS

                skip('=') -> Symbol.GREATER_EQUAL

                else      -> Symbol.GREATER
            }

            skip('=') -> when {
                skip('=') -> Symbol.DOUBLE_EQUAL

                else      -> Symbol.EQUAL
            }

            skip('~') -> when {
                skip('=') -> Symbol.TILDE_EQUAL

                else      -> Symbol.TILDE
            }

            skip('&') -> Symbol.AMPERSAND

            skip('|') -> Symbol.PIPE

            skip(',') -> Symbol.COMMA

            skip('?') -> Symbol.QUESTION

            skip('!') -> Symbol.EXCLAMATION

            skip(':') -> when {
                skip(':') -> Symbol.DOUBLE_COLON

                else      -> Symbol.COLON
            }

            skip(';') -> Symbol.SEMICOLON

            skip('.') -> when {
                skip('.') -> Symbol.DOUBLE_DOT

                else      -> Symbol.DOT
            }

            else      -> illegalCharacterError(peek(), here())
        }

        return Token(location, op)
    }
}