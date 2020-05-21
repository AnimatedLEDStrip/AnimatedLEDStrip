@file:Suppress("UnusedImport", "PackageDirectoryMismatch")

// ## animation info ##
// name Pixel Marathon
// abbr PXM
// colors 5
// repetitive true
// center notused
// delay used 8
// direction used
// distance notused
// spacing notused
// ## end info ##

import animatedledstrip.animationutils.*
import animatedledstrip.colors.*
import animatedledstrip.colors.ccpresets.*
import animatedledstrip.leds.*
import animatedledstrip.utils.*
import kotlinx.coroutines.CoroutineScope

@Suppress("UNRESOLVED_REFERENCE")
val leds = bindings["leds"] as AnimatedLEDStrip.Section

@Suppress("UNRESOLVED_REFERENCE")
val data = bindings["data"] as AnimationData

@Suppress("UNRESOLVED_REFERENCE")
val scope = bindings["scope"] as CoroutineScope

val color0 = data.pCols[0]
val color1 = data.pCols[1]
val color2 = data.pCols[2]
val color3 = data.pCols[3]
val color4 = data.pCols[4]
val delay = data.delay
val direction = data.direction

leds.apply {
    val baseAnimation = AnimationData().animation("Pixel Run")
        .direction(direction).delay(delay)

    runParallel(baseAnimation.copy().color(color4), scope = scope)
    delayBlocking((Math.random() * 500).toLong())

    runParallel(baseAnimation.copy().color(color3), scope = scope)
    delayBlocking((Math.random() * 500).toLong())

    runParallel(baseAnimation.copy().color(color1), scope = scope)
    delayBlocking((Math.random() * 500).toLong())

    runParallel(baseAnimation.copy().color(color2), scope = scope)
    delayBlocking((Math.random() * 500).toLong())

    runParallel(baseAnimation.copy().color(color0), scope = scope)
    delayBlocking((Math.random() * 500).toLong())

    runParallel(baseAnimation.copy().color(color1), scope = scope)
    delayBlocking((Math.random() * 500).toLong())

    runParallel(baseAnimation.copy().color(color4), scope = scope)
    delayBlocking((Math.random() * 500).toLong())

    runParallel(baseAnimation.copy().color(color2), scope = scope)
    delayBlocking((Math.random() * 500).toLong())

    runParallel(baseAnimation.copy().color(color3), scope = scope)
    delayBlocking((Math.random() * 500).toLong())

    runParallel(baseAnimation.copy().color(color0), scope = scope)
    delayBlocking((Math.random() * 500).toLong())
}
