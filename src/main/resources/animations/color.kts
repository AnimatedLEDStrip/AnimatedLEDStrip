@file:Suppress("UnusedImport", "PackageDirectoryMismatch")

// ## animation info ##
// name Color
// abbr COL
// colors 1
// repetitive false
// center notused
// delay notused
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
    setProlongedStripColor(color0)
}