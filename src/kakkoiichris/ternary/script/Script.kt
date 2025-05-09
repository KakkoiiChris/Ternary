package kakkoiichris.ternary.script

import kakkoiichris.ternary.parser.Expr
import kakkoiichris.ternary.parser.Program
import kakkoiichris.ternary.parser.Stmt
import kakkoiichris.ternary.util.emptyAssignmentError
import kakkoiichris.ternary.util.invalidUnaryOperandError

class Script(private val program: Program) : Expr.Visitor<Any>, Stmt.Visitor<Unit> {
    private val memory = Memory()

    fun run(): Any {
        var last: Any = Unit

        for (statement in program) {
            last = visit(statement)
        }

        return last
    }

    private fun Boolean.toDouble() = if (this) 1.0 else 0.0

    private fun Any.toBoolean() = when (this) {
        is Boolean -> this

        is Double  -> this != 0.0

        is String  -> isNotEmpty()

        else       -> TODO()
    }

    override fun visitValueExpr(expr: Expr.Value) = expr.value

    override fun visitNameExpr(expr: Expr.Name): Any {
        var symbol = memory[expr.name]

        if (symbol == null) {
            memory[expr.name] = Reference(Unit)

            symbol = memory[expr.name]
        }

        return symbol!!
    }

    override fun visitUnaryExpr(expr: Expr.Unary) = when (expr.operator) {
        Expr.Unary.Operator.NEGATE -> when (val operand = visit(expr.operand).fromRef()) {
            is Double -> -operand

            is String -> operand.reversed()

            else      -> invalidUnaryOperandError(expr, expr.operator, expr.loc)
        }

        Expr.Unary.Operator.NOT    -> when (val operand = visit(expr.operand).fromRef()) {
            is Boolean -> !operand

            else       -> invalidUnaryOperandError(expr, expr.operator, expr.loc)
        }
    }

    override fun visitBinaryExpr(expr: Expr.Binary) = when (expr.operator) {
        Expr.Binary.Operator.ASSIGN        -> when (val left = visit(expr.operandLeft)) {
            is Reference -> when (val right = visit(expr.operandRight).fromRef()) {
                Unit -> emptyAssignmentError(expr.operandRight.loc)

                else -> {
                    left.value = right

                    right
                }
            }

            else         -> TODO()
        }

        Expr.Binary.Operator.OR            -> when (val left = visit(expr.operandLeft).fromRef()) {
            is Double -> when (val right = visit(expr.operandRight).fromRef()) {
                is Double -> (left.toBoolean() || right.toBoolean()).toDouble()

                else      -> TODO()
            }

            else      -> TODO()
        }

        Expr.Binary.Operator.AND           -> when (val left = visit(expr.operandLeft).fromRef()) {
            is Double -> when (val right = visit(expr.operandRight).fromRef()) {
                is Double -> (left.toBoolean() && right.toBoolean()).toDouble()

                else      -> TODO()
            }

            else      -> TODO()
        }

        Expr.Binary.Operator.EQUAL         -> when (val left = visit(expr.operandLeft).fromRef()) {
            is Double -> when (val right = visit(expr.operandRight).fromRef()) {
                is Double -> (left == right).toDouble()

                else      -> TODO()
            }

            is String -> when (val right = visit(expr.operandRight).fromRef()) {
                is String -> (left == right).toDouble()

                else      -> TODO()
            }

            else      -> TODO()
        }

        Expr.Binary.Operator.NOT_EQUAL     -> when (val left = visit(expr.operandLeft).fromRef()) {
            is Double -> when (val right = visit(expr.operandRight).fromRef()) {
                is Double -> (left != right).toDouble()

                else      -> TODO()
            }

            is String -> when (val right = visit(expr.operandRight).fromRef()) {
                is String -> (left != right).toDouble()

                else      -> TODO()
            }

            else      -> TODO()
        }

        Expr.Binary.Operator.LESS          -> when (val left = visit(expr.operandLeft).fromRef()) {
            is Double -> when (val right = visit(expr.operandRight).fromRef()) {
                is Double -> (left < right).toDouble()

                else      -> TODO()
            }

            is String -> when (val right = visit(expr.operandRight).fromRef()) {
                is String -> (left < right).toDouble()

                else      -> TODO()
            }

            else      -> TODO()
        }

        Expr.Binary.Operator.LESS_EQUAL    -> when (val left = visit(expr.operandLeft).fromRef()) {
            is Double -> when (val right = visit(expr.operandRight).fromRef()) {
                is Double -> (left <= right).toDouble()

                else      -> TODO()
            }

            is String -> when (val right = visit(expr.operandRight).fromRef()) {
                is String -> (left <= right).toDouble()

                else      -> TODO()
            }

            else      -> TODO()
        }

        Expr.Binary.Operator.GREATER       -> when (val left = visit(expr.operandLeft).fromRef()) {
            is Double -> when (val right = visit(expr.operandRight).fromRef()) {
                is Double -> (left > right).toDouble()

                else      -> TODO()
            }

            is String -> when (val right = visit(expr.operandRight).fromRef()) {
                is String -> (left > right).toDouble()

                else      -> TODO()
            }

            else      -> TODO()
        }

        Expr.Binary.Operator.GREATER_EQUAL -> when (val left = visit(expr.operandLeft).fromRef()) {
            is Double -> when (val right = visit(expr.operandRight).fromRef()) {
                is Double -> (left >= right).toDouble()

                else      -> TODO()
            }

            is String -> when (val right = visit(expr.operandRight).fromRef()) {
                is String -> (left >= right).toDouble()

                else      -> TODO()
            }

            else      -> TODO()
        }

        Expr.Binary.Operator.IN            -> when (val left = visit(expr.operandLeft).fromRef()) {
            is String -> when (val right = visit(expr.operandRight).fromRef()) {
                is String -> (left in right).toDouble()

                else      -> TODO()
            }

            else      -> TODO()
        }

        Expr.Binary.Operator.NOT_IN        -> when (val left = visit(expr.operandLeft).fromRef()) {
            is String -> when (val right = visit(expr.operandRight).fromRef()) {
                is String -> (left !in right).toDouble()

                else      -> TODO()
            }

            else      -> TODO()
        }

        Expr.Binary.Operator.RANGE         -> TODO()

        Expr.Binary.Operator.ADD           -> when (val left = visit(expr.operandLeft).fromRef()) {
            is Double -> when (val right = visit(expr.operandRight).fromRef()) {
                is Double -> left + right

                else      -> TODO()
            }

            else      -> TODO()
        }

        Expr.Binary.Operator.SUBTRACT      -> when (val left = visit(expr.operandLeft).fromRef()) {
            is Double -> when (val right = visit(expr.operandRight).fromRef()) {
                is Double -> left - right

                else      -> TODO()
            }

            else      -> TODO()
        }

        Expr.Binary.Operator.MULTIPLY      -> when (val left = visit(expr.operandLeft).fromRef()) {
            is Double -> when (val right = visit(expr.operandRight).fromRef()) {
                is Double -> left * right

                else      -> TODO()
            }

            else      -> TODO()
        }

        Expr.Binary.Operator.DIVIDE        -> when (val left = visit(expr.operandLeft).fromRef()) {
            is Double -> when (val right = visit(expr.operandRight).fromRef()) {
                is Double -> left / right

                else      -> TODO()
            }

            else      -> TODO()
        }

        Expr.Binary.Operator.MODULUS       -> when (val left = visit(expr.operandLeft).fromRef()) {
            is Double -> when (val right = visit(expr.operandRight).fromRef()) {
                is Double -> left % right

                else      -> TODO()
            }

            else      -> TODO()
        }
    }

    override fun visitBreakStmt(stmt: Stmt.Break) {
        throw Redirect.Break(stmt.loc)
    }

    override fun visitSkipStmt(stmt: Stmt.Skip) {
        throw Redirect.Skip(stmt.loc)
    }

    override fun visitRedoStmt(stmt: Stmt.Redo) {
        throw Redirect.Redo(stmt.loc)
    }

    override fun visitSendStmt(stmt: Stmt.Send) {
        val value = visit(stmt.value).fromRef()

        throw Redirect.Send(stmt.loc, value)
    }

    override fun visitExitStmt(stmt: Stmt.Exit) {
        val value = visit(stmt.value).fromRef()

        throw Redirect.Exit(stmt.loc, value)
    }

    override fun visitFunctionStmt(stmt: Stmt.Function) {
        TODO("Not yet implemented")
    }

    override fun visitGroupStmt(stmt: Stmt.Chain) {
        var value: Any = Unit

        for (expr in stmt.exprs) {
            value = visit(expr)
        }

        when (stmt.mode) {
            Stmt.Chain.Mode.NONE      -> Unit

            Stmt.Chain.Mode.CONDITION -> {
                val stmts = if (value.toBoolean()) stmt.left else stmt.right

                for (stmt in stmts) {
                    visit(stmt)
                }
            }

            Stmt.Chain.Mode.EXCEPTION -> {
                try {
                    for (stmt in stmt.left) {
                        visit(stmt)
                    }
                }
                catch (e: Exception) {
                    for (stmt in stmt.right) {
                        visit(stmt)
                    }
                }
            }
        }
    }
}