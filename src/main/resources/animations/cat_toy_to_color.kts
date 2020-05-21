@file:Suppress("UnusedImport", "PackageDirectoryMismatch")

// ## animation info ##
// name Cat Toy To Color
// abbr CTC
// colors 1
// repetitive false
// center notused
// delay used 5
// direction notused
// distance notused
// spacing notused
// ## end info ##

import animatedledstrip.animationutils.*
import animatedledstrip.colors.*
import animatedledstrip.colors.ccpresets.*
import animatedledstrip.leds.*
import animatedledstrip.utils.*
import kotlin.math.max
import kotlin.math.min


@Suppress("UNRESOLVED_REFERENCE")
val leds = bindings["leds"] as AnimatedLEDStrip.Section

@Suppress("UNRESOLVED_REFERENCE")
val data = bindings["data"] as AnimationData

val color0 = data.pCols[0]

leds.apply {
    val pixels = indices.shuffled()
    var oldPixel = 0

    for (newPixel in pixels) {
        runSequential(
            animation = data.copy(
                animation = "Pixel Run",
                direction = if (oldPixel > newPixel) Direction.BACKWARD else Direction.FORWARD
            ),
            section = getSection(min(oldPixel, newPixel), max(oldPixel, newPixel), this.numLEDs)
        )
        setProlongedPixelColor(newPixel, color0)
        delayBlocking((Math.random() * 2500).toLong())
        oldPixel = newPixel
    }
}
