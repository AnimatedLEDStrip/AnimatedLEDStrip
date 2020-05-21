@file:Suppress("UnusedImport", "PackageDirectoryMismatch")

// ## animation info ##
// name Fade To Color
// abbr FTC
// colors 1
// repetitive false
// center notused
// delay used 30
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
import kotlinx.coroutines.launch

@Suppress("UNRESOLVED_REFERENCE")
val leds = bindings["leds"] as AnimatedLEDStrip.Section

@Suppress("UNRESOLVED_REFERENCE")
val data = bindings["data"] as AnimationData

@Suppress("UNRESOLVED_REFERENCE")
val scope = bindings["scope"] as CoroutineScope

val color0 = data.pCols[0]

leds.apply {
    iterateOverPixels {
        prolongedColors[it] = color0[it]
        scope.launch(sparkleThreadPool) {
            fadePixel(it)
        }
    }
}
