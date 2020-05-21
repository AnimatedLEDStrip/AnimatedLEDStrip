@file:Suppress("UnusedImport", "PackageDirectoryMismatch")

// ## animation info ##
// name Splat
// abbr SPT
// colors 1
// repetitive false
// center used
// delay used 5
// direction notused
// distance used
// spacing notused
// ## end info ##

import animatedledstrip.animationutils.*
import animatedledstrip.colors.*
import animatedledstrip.colors.ccpresets.*
import animatedledstrip.leds.*
import animatedledstrip.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlin.math.max
import kotlin.math.min

@Suppress("UNRESOLVED_REFERENCE")
val leds = bindings["leds"] as AnimatedLEDStrip.Section

@Suppress("UNRESOLVED_REFERENCE")
val data = bindings["data"] as AnimationData

@Suppress("UNRESOLVED_REFERENCE")
val scope = bindings["scope"] as CoroutineScope

val color0 = data.pCols[0]
val center = data.center
val delay = data.delay
val distance = data.distance

leds.apply {
    val baseAnimation = AnimationData()
        .animation("Wipe")
        .color(color0)
        .delay(delay)

    runParallelAndJoin(
        scope,
        Pair(
            baseAnimation.copy(direction = Direction.FORWARD),
            getSection(center, min(center + distance, numLEDs - 1))
        ),
        Pair(
            baseAnimation.copy(direction = Direction.BACKWARD),
            getSection(max(center - distance, 0), center)
        )
    )
}
