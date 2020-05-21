@file:Suppress("UnusedImport", "PackageDirectoryMismatch")

// ## animation info ##
// name Bounce
// abbr BNC
// colors 1
// repetitive true
// center used
// delay used 10
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
    iterateOver(0..((endPixel - startPixel) / 2)) { i ->
        val baseAnimation = data.copy(
            animation = "Pixel Run"
        )

        runSequential(
            animation = baseAnimation.copy(direction = Direction.FORWARD),
            section = getSection(i, numLEDs - i - 1)
        )
        setAndFadePixel(
            pixel = numLEDs - i - 1,
            color = color0,
            amountOfOverlay = 25,
            delay = 50,
            context = parallelAnimationThreadPool
        )

        runSequential(
            animation = baseAnimation.copy(direction = Direction.BACKWARD),
            section = getSection(i, numLEDs - i - 2)
        )
        setAndFadePixel(
            pixel = i,
            color = color0,
            amountOfOverlay = 25,
            delay = 50,
            context = parallelAnimationThreadPool
        )
    }
    if (numLEDs % 2 == 1) {
        setAndFadePixel(
            pixel = numLEDs / 2,
            color = color0,
            amountOfOverlay = 25,
            delay = 50,
            context = parallelAnimationThreadPool
        )
    }
}

