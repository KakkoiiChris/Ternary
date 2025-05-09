package kakkoiichris.ternary.lexer

data class Location(val file: String = "", val row: Int = -1, val col: Int = -1) {
    override fun toString() = """$file @ Row $row, Col $col"""
}