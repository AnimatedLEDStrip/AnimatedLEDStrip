package animatedledstrip.leds.animationmanagement

val AnimationManager.numLEDs: Int
    get() = sectionManager.numLEDs

val AnimationManager.validIndices: List<Int>
    get() = sectionManager.validIndices

inline fun AnimationManager.iterateOverPixels(
    operation: (Int) -> Unit,
) {
    for (q in validIndices) operation.invoke(q)
}

inline fun AnimationManager.iterateOverPixelsReverse(
    operation: (Int) -> Unit,
) {
    for (q in validIndices.reversed()) operation.invoke(q)
}

fun AnimationManager.randomPixel(): Int = validIndices.random()

fun AnimationManager.shuffledIndices(): List<Int> = validIndices.shuffled()
