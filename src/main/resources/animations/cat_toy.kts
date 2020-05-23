@file:Suppress("UnusedImport", "PackageDirectoryMismatch")

// ## animation info ##
// name Cat Toy
// abbr CAT
// colors 1
// repetitive true
// center notused
// delay used 5
// direction notused
// distance notused
// spacing notused
// ## end info ##


import animatedledstrip.animationutils.*
import animatedledstrip.leds.*
import animatedledstrip.utils.*

@Suppress("UNRESOLVED_REFERENCE")
val leds = bindings["leds"] as AnimatedLEDStrip.Section

@Suppress("UNRESOLVED_REFERENCE")
val data = bindings["data"] as AnimationData

val color0 = data.pCols[0]

leds.apply {
    val pixel1 = randomPixelIn(indices)
    val pixel2 = randomPixelIn(indices.first(), pixel1)
    val pixel3 = randomPixelIn(pixel2, indices.last())
    val pixel4 = randomPixelIn(indices.first(), pixel3)
    val pixel5 = randomPixelIn(pixel4, indices.last())

    runSequential(
        animation = data.copy(
            animation = "Pixel Run",
            direction = Direction.FORWARD
        ),
        section = getSubSection(0, pixel1)
    )
    setTemporaryPixelColor(pixel1, color0)
    delayBlocking((Math.random() * 2500).toLong())
    revertPixel(pixel1)

    runSequential(
        animation = data.copy(
            animation = "Pixel Run",
            direction = Direction.BACKWARD
        ),
        section = getSubSection(pixel2, pixel1)
    )
    setTemporaryPixelColor(pixel2, color0)
    delayBlocking((Math.random() * 2500).toLong())
    revertPixel(pixel2)

    runSequential(
        animation = data.copy(
            animation = "Pixel Run",
            direction = Direction.FORWARD
        ),
        section = getSubSection(pixel2, pixel3)
    )
    setTemporaryPixelColor(pixel3, color0)
    delayBlocking((Math.random() * 2500).toLong())
    revertPixel(pixel3)

    runSequential(
        animation = data.copy(
            animation = "Pixel Run",
            direction = Direction.BACKWARD
        ),
        section = getSubSection(pixel4, pixel3)
    )
    setTemporaryPixelColor(pixel4, color0)
    delayBlocking((Math.random() * 2500).toLong())
    revertPixel(pixel4)

    runSequential(
        animation = data.copy(
            animation = "Pixel Run",
            direction = Direction.FORWARD
        ),
        section = getSubSection(pixel4, pixel5)
    )
    setTemporaryPixelColor(pixel5, color0)
    delayBlocking((Math.random() * 2500).toLong())
    revertPixel(pixel5)

    runSequential(
        animation = data.copy(
            animation = "Pixel Run",
            direction = Direction.BACKWARD
        ),
        section = getSubSection(0, pixel5)
    )
}