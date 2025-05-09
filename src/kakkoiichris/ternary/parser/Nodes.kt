package kakkoiichris.ternary.parser

import kakkoiichris.ternary.lexer.Location
import kakkoiichris.ternary.lexer.Symbol
import kakkoiichris.ternary.lexer.Token

typealias Exprs = List<Expr>

sealed class Expr(val loc: Location) {
    interface Visitor<X> {
        fun visit(expr: Expr) =
            expr.accept(this)
        
        fun visitValueExpr(expr: Value): X
        
        fun visitNameExpr(expr: Name): X
        
        fun visitUnaryExpr(expr: Unary): X
        
        fun visitBinaryExpr(expr: Binary): X
    }
    
    class Value(loc: Location, val value: Any) : Expr(loc) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitValueExpr(this)
    }
    
    class Name(loc: Location, val name: String) : Expr(loc) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitNameExpr(this)
    }
    
    class Unary(loc: Location, val operator: Operator, val operand: Expr) : Expr(loc) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitUnaryExpr(this)
        
        enum class Operator(val type: Symbol) {
            NEGATE(Symbol.DASH),
            NOT(Symbol.TILDE);
            
            companion object {
                operator fun get(type: Symbol) =
                    entries.first { it.type == type }
            }
        }
    }
    
    class Binary(loc: Location, val operator: Operator, val operandLeft: Expr, val operandRight: Expr) : Expr(loc) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitBinaryExpr(this)
        
        enum class Operator(val type: Symbol) {
            ASSIGN(Symbol.EQUAL),
            OR(Symbol.PIPE),
            AND(Symbol.AMPERSAND),
            EQUAL(Symbol.DOUBLE_EQUAL),
            NOT_EQUAL(Symbol.TILDE_EQUAL),
            LESS(Symbol.LESS),
            LESS_EQUAL(Symbol.LESS_EQUAL),
            GREATER(Symbol.GREATER),
            GREATER_EQUAL(Symbol.GREATER_EQUAL),
            IN(Symbol.GREATER_LESS),
            NOT_IN(Symbol.LESS_GREATER),
            RANGE(Symbol.DOUBLE_DOT),
            ADD(Symbol.PLUS),
            SUBTRACT(Symbol.DASH),
            MULTIPLY(Symbol.STAR),
            DIVIDE(Symbol.SLASH),
            MODULUS(Symbol.PERCENT);
            
            companion object {
                operator fun get(type: Symbol) =
                    entries.first { it.type == type }
            }
        }
    }
    
    abstract fun <X> accept(visitor: Visitor<X>): X
}

typealias Stmts = List<Stmt>

sealed class Stmt(val loc: Location) {
    interface Visitor<X> {
        fun visit(stmt: Stmt) =
            stmt.accept(this)
        
        fun visitBreakStmt(stmt: Break): X
        
        fun visitSkipStmt(stmt: Skip): X
        
        fun visitRedoStmt(stmt: Redo): X
        
        fun visitSendStmt(stmt: Send): X
        
        fun visitExitStmt(stmt: Exit): X
        
        fun visitFunctionStmt(stmt: Function): X
        
        fun visitGroupStmt(stmt: Chain): X
    }
    
    class Break(loc: Location) : Stmt(loc) {
        override fun <X> accept(visitor: Visitor<X>): X =
            visitor.visitBreakStmt(this)
    }
    
    class Skip(loc: Location) : Stmt(loc) {
        override fun <X> accept(visitor: Visitor<X>): X =
            visitor.visitSkipStmt(this)
    }
    
    class Redo(loc: Location) : Stmt(loc) {
        override fun <X> accept(visitor: Visitor<X>): X =
            visitor.visitRedoStmt(this)
    }
    
    class Send(loc: Location, val value:Expr) : Stmt(loc) {
        override fun <X> accept(visitor: Visitor<X>): X =
            visitor.visitSendStmt(this)
    }
    
    class Exit(loc: Location, val value:Expr) : Stmt(loc) {
        override fun <X> accept(visitor: Visitor<X>): X =
            visitor.visitExitStmt(this)
    }
    
    class Function(loc: Location) : Stmt(loc) {
        override fun <X> accept(visitor: Visitor<X>): X =
            visitor.visitFunctionStmt(this)
    }
    
    /*class Block(loc: Location, val stmts: Stmts, val final: Expr):Stmt(loc){
        override fun <X> accept(visitor: Visitor<X>): X =
            visitor.visitBlockStmt(this)
    }*/
    
    class Chain(loc: Location, val exprs: Exprs, val mode: Mode, val left: Stmts, val right: Stmts) : Stmt(loc) {
        override fun <X> accept(visitor: Visitor<X>): X =
            visitor.visitGroupStmt(this)
        
        enum class Mode {
            NONE,
            CONDITION,
            EXCEPTION,
        }
    }
    
    abstract fun <X> accept(visitor: Visitor<X>): X
}