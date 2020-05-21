@file:Suppress("UnusedImport", "PackageDirectoryMismatch")

// ## animation info ##
// name Bounce To Color
// abbr BTC
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

@Suppress("UNRESOLVED_REFERENCE")
val leds = bindings["leds"] as AnimatedLEDStrip.Section

@Suppress("UNRESOLVED_REFERENCE")
val data = bindings["data"] as AnimationData

val color0 = data.pCols[0]

leds.apply {
    iterateOver(0..numLEDs / 2) { i ->
        val baseAnimation = data.copy(animation = "Pixel Run")

        runSequential(
            animation = baseAnimation.copy(direction = Direction.FORWARD),
            section = getSection(i, numLEDs - i - 1)
        )
        setProlongedPixelColor(numLEDs - i - 1, color0)

        runSequential(
            animation = baseAnimation.copy(direction = Direction.BACKWARD),
            section = getSection(i, numLEDs - i - 2)
        )
        setProlongedPixelColor(i, color0)
    }
    if (numLEDs % 2 == 1) {
        setProlongedPixelColor(
            numLEDs / 2,
            color0
        )
    }
}
