@file:Suppress("UnusedImport", "PackageDirectoryMismatch")

// ## animation info ##
// name Smooth Fade
// abbr SMF
// colors 1
// repetitive true
// center notused
// delay used 50
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
val delay = data.delay

leds.apply {
    iterateOverPixels {
        setProlongedStripColor(color0[it])
        delayBlocking(delay)
    }
}
