package kakkoiichris.ternary.script

import kakkoiichris.ternary.lexer.Location

/**
 * Ternary
 *
 * Copyright (C) 2023, KakkoiiChris
 *
 * File:    Redirect.kt
 *
 * Created: Wednesday, May 24, 2023, 00:07:30
 *
 * @author Christian Bryce Alexander
 */
sealed class Redirect(val origin: Location) : RuntimeException() {
    class Break(origin: Location) : Redirect(origin)
    
    class Redo(origin: Location) : Redirect(origin)
    
    class Skip(origin: Location) : Redirect(origin)
    
    class Send(origin: Location, val value: Any) : Redirect(origin)
    
    class Exit(origin: Location, val value: Any) : Redirect(origin)
}