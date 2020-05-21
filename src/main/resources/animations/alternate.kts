@file:Suppress("UnusedImport", "PackageDirectoryMismatch")

// ## animation info ##
// name Alternate
// abbr ALT
// colors 2
// repetitive true
// center notused
// delay used 1000
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
val color1 = data.pCols[1]

val delay = data.delay

leds.apply {
    setProlongedStripColor(color0)
    delayBlocking(delay)
    setProlongedStripColor(color1)
    delayBlocking(delay)
}
