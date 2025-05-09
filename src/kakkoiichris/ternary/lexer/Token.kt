package kakkoiichris.ternary.lexer

data class Token<T : TokenType>(val location: Location, val type: T)

sealed interface TokenType {
    val rep: String
}

enum class Symbol(override val rep: String) : TokenType {
    // ASSIGNMENT
    EQUAL("="),
    PLUS_EQUAL("+="),
    DASH_EQUAL("-="),
    STAR_EQUAL("*="),
    SLASH_EQUAL("/="),
    PERCENT_EQUAL("%="),

    // LOGICAL OR
    PIPE("|"),

    // LOGICAL AND
    AMPERSAND("&"),

    // EQUALITY
    DOUBLE_EQUAL("=="),
    TILDE_EQUAL("~="),

    // RELATIONAL
    LESS("<"),
    LESS_EQUAL("<="),
    GREATER(">"),
    GREATER_EQUAL(">="),

    // CONTAINS
    GREATER_LESS("><"),
    LESS_GREATER("<>"),

    // RANGE
    DOUBLE_DOT(".."),

    // ADDITIVE
    PLUS("+"),
    DASH("-"),

    // MULTIPLICATIVE
    STAR("*"),
    SLASH("/"),
    PERCENT("%"),

    // PREFIX
    TILDE("~"),

    // DOT
    DOT("."),

    // DELIMITERS
    COMMA(","),
    LEFT_PAREN("("),
    RIGHT_PAREN(")"),
    LEFT_SQUARE("["),
    RIGHT_SQUARE("]"),
    LEFT_BRACE("{"),
    RIGHT_BRACE("}"),
    QUESTION("?"),
    EXCLAMATION("!"),
    COLON(":"),
    DOUBLE_COLON("::"),
    SEMICOLON(";");
}

enum class Keyword : TokenType {
    BREAK,
    SKIP,
    REDO,
    SEND,
    EXIT,
    FN;

    override val rep = name.lowercase()
}

data class Value(val value: Any) : TokenType {
    override val rep get() = "Value<$value>"
}

data class Name(val name: String) : TokenType {
    override val rep get() = "Name<$name>"
}

data object End : TokenType {
    override val rep get() = "End"
}