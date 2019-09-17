package animatedledstrip.utils

import animatedledstrip.colors.ColorContainer

fun Long.toARGB(): Int = (this or 0xFF000000).toInt()
fun Int.toARGB(): Int = (this or 0xFF000000.toInt())

fun Int.toColorContainer(): ColorContainer = ColorContainer(this.toLong())
fun Long.toColorContainer(): ColorContainer = ColorContainer(this)
