@file:Suppress("UnusedImport", "PackageDirectoryMismatch")

// ## animation info ##
// name Fireworks
// abbr FWS
// colors 1
// optColors 4
// repetitive true
// center notused
// delay used 30
// direction notused
// distance used 20
// spacing notused
// ## end info ##

import animatedledstrip.animationutils.*
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
val delay = data.delay

leds.apply {
    val color = data.colors.random()
    if (color != EmptyColorContainer) {
        runParallel(
            data.copy(
                colors = listOf(color),
                animation = "Ripple",
                center = randomPixelIn(indices)
            ),
            scope = scope
        )
        delayBlocking(delay * 20)
    }
}
