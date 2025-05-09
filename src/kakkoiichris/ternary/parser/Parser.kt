package kakkoiichris.ternary.parser

import kakkoiichris.ternary.lexer.*
import kakkoiichris.ternary.util.illegalTokenTypeError
import kakkoiichris.ternary.util.unexpectedError

class Parser(private val lexer: Lexer) {
    private var currentToken = lexer.next()

    fun parse(): Program {
        val stmts = mutableListOf<Stmt>()

        while (!atEndOfFile()) {
            stmts += stmt()
        }

        mustSkip(End)

        return Program(stmts)
    }

    private fun peek() =
        currentToken

    private fun match(type: TokenType) =
        peek().type == type

    private fun matchAny(vararg types: TokenType): Boolean {
        for (type in types) {
            if (match(type)) {
                return true
            }
        }

        return false
    }

    private inline fun <reified T : TokenType> match() =
        T::class.isInstance(currentToken.type)

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T : TokenType> get(): Token<T> {
        val token = currentToken

        mustSkip(token.type)

        return token as Token<T>
    }

    private fun skip(type: TokenType) =
        if (match(type)) {
            step()
            true
        }
        else false

    private fun skipAny(vararg types: TokenType): Boolean {
        for (type in types) {
            if (skip(type)) {
                return true
            }
        }

        return false
    }

    private fun mustSkip(type: TokenType) {
        if (!skip(type)) {
            illegalTokenTypeError(peek().type, here())
        }
    }

    private fun step() {
        if (lexer.hasNext()) {
            currentToken = lexer.next()
        }
    }

    private fun atEndOfFile() =
        match(End)

    private fun here() =
        peek().location

    private fun stmt() = when {
        match(Keyword.BREAK) -> breakStmt()

        match(Keyword.REDO)  -> redoStmt()

        match(Keyword.SKIP)  -> skipStmt()

        match(Keyword.SEND)  -> sendStmt()

        match(Keyword.EXIT)  -> exitStmt()

        match(Keyword.FN)    -> fnStmt()

        else                 -> chainStmt()
    }

    private fun breakStmt(): Stmt.Break {
        val loc = here()

        mustSkip(Keyword.BREAK)

        return Stmt.Break(loc)
    }

    private fun redoStmt(): Stmt.Redo {
        val loc = here()

        mustSkip(Keyword.REDO)

        return Stmt.Redo(loc)
    }

    private fun skipStmt(): Stmt.Skip {
        val loc = here()

        mustSkip(Keyword.SKIP)

        return Stmt.Skip(loc)
    }

    private fun sendStmt(): Stmt.Send {
        val loc = here()

        mustSkip(Keyword.SEND)

        return Stmt.Send(loc, expr())
    }

    private fun exitStmt(): Stmt.Exit {
        val loc = here()

        mustSkip(Keyword.EXIT)

        return Stmt.Exit(loc, expr())
    }

    private fun fnStmt(): Stmt.Function {
        val loc = here()

        mustSkip(Keyword.FN)

        return Stmt.Function(loc)
    }

    private fun chainStmt(): Stmt.Chain {
        val loc = here()

        val exprs = mutableListOf<Expr>()

        do {
            exprs += expr()
        }
        while (skip(Symbol.COMMA))

        val left = mutableListOf<Stmt>()
        val right = mutableListOf<Stmt>()

        val mode = when {
            skip(Symbol.QUESTION)    -> Stmt.Chain.Mode.CONDITION

            skip(Symbol.EXCLAMATION) -> Stmt.Chain.Mode.EXCEPTION

            else                     -> Stmt.Chain.Mode.NONE
        }

        if (mode != Stmt.Chain.Mode.NONE) {
            mustSkip(Symbol.LEFT_PAREN)

            while (!skip(Symbol.COLON)) {
                left += stmt()
            }

            while (!skip(Symbol.RIGHT_PAREN)) {
                right += stmt()
            }
        }

        return Stmt.Chain(loc, exprs, mode, left, right)
    }

    private fun expr() = assignment()

    private fun assignment(): Expr {
        val node = or()

        return if (matchAny(
                Symbol.EQUAL,
                Symbol.PLUS_EQUAL,
                Symbol.DASH_EQUAL,
                Symbol.STAR_EQUAL,
                Symbol.SLASH_EQUAL,
                Symbol.PERCENT_EQUAL
            )
        ) {
            val (location, type) = get<Symbol>()

            Expr.Binary(location, Expr.Binary.Operator[type], node, assignment())
        }
        else {
            node
        }
    }

    private fun or(): Expr {
        var node = and()

        while (match(Symbol.PIPE)) {
            val (location, type) = get<Symbol>()

            node = Expr.Binary(location, Expr.Binary.Operator[type], node, and())
        }

        return node
    }

    private fun and(): Expr {
        var node = equality()

        while (match(Symbol.AMPERSAND)) {
            val (location, type) = get<Symbol>()

            node = Expr.Binary(location, Expr.Binary.Operator[type], node, equality())
        }

        return node
    }

    private fun equality(): Expr {
        var node = relational()

        while (matchAny(Symbol.DOUBLE_EQUAL, Symbol.TILDE_EQUAL)) {
            val (location, type) = get<Symbol>()

            node = Expr.Binary(location, Expr.Binary.Operator[type], node, relational())
        }

        return node
    }

    private fun relational(): Expr {
        var node = contains()

        while (matchAny(Symbol.LESS, Symbol.LESS_GREATER, Symbol.GREATER, Symbol.GREATER_EQUAL)) {
            val (location, type) = get<Symbol>()

            node = Expr.Binary(location, Expr.Binary.Operator[type], node, contains())
        }

        return node
    }

    private fun contains(): Expr {
        var node = range()

        while (matchAny(Symbol.GREATER_LESS, Symbol.LESS_GREATER)) {
            val (location, type) = get<Symbol>()

            node = Expr.Binary(location, Expr.Binary.Operator[type], node, range())
        }

        return node
    }

    private fun range(): Expr {
        var node = additive()

        while (match(Symbol.DOUBLE_DOT)) {
            val (location, type) = get<Symbol>()

            node = Expr.Binary(location, Expr.Binary.Operator[type], node, additive())
        }

        return node
    }

    private fun additive(): Expr {
        var node = multiplicative()

        while (matchAny(Symbol.PLUS, Symbol.DASH)) {
            val (location, type) = get<Symbol>()

            node = Expr.Binary(location, Expr.Binary.Operator[type], node, multiplicative())
        }

        return node
    }

    private fun multiplicative(): Expr {
        var node = prefix()

        while (matchAny(Symbol.STAR, Symbol.SLASH, Symbol.PERCENT)) {
            val (location, type) = get<Symbol>()

            node = Expr.Binary(location, Expr.Binary.Operator[type], node, prefix())
        }

        return node
    }

    private fun prefix(): Expr = if (matchAny(Symbol.DASH, Symbol.TILDE)) {
        val (location, type) = get<Symbol>()

        Expr.Unary(location, Expr.Unary.Operator[type], prefix())
    }
    else {
        terminal()
    }

    private fun terminal(): Expr {
        return when {
            match<Value>() -> {
                val (location, type) = get<Value>()

                Expr.Value(location, type.value)
            }

            match<Name>()  -> {
                val (location, type) = get<Name>()

                Expr.Name(location, type.name)
            }

            skip(Symbol.LEFT_PAREN) -> {
                val expr = expr()

                mustSkip(Symbol.RIGHT_PAREN)

                expr
            }

            else           -> unexpectedError("INVALID TERMINAL BEGINNING WITH '${peek().type}' TOKEN! (${peek().location})")
        }
    }
}