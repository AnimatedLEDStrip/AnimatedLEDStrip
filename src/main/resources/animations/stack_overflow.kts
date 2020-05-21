@file:Suppress("UnusedImport", "PackageDirectoryMismatch")

// ## animation info ##
// name Stack Overflow
// abbr STO
// colors 2
// repetitive true
// center notused
// delay used 2
// direction notused
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
val delay = data.delay

leds.apply {
    val baseAnimation = AnimationData()
        .animation("Stack")
        .delay(delay)

    runParallelAndJoin(
        scope,
        Pair(
            baseAnimation.copy(
                colors = listOf(color0),
                direction = Direction.FORWARD
            ),
            this
        ),
        Pair(
            baseAnimation.copy(
                colors = listOf(color1),
                direction = Direction.BACKWARD
            ),
            this
        )
    )
}
