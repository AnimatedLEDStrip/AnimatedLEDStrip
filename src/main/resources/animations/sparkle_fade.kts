@file:Suppress("UnusedImport", "PackageDirectoryMismatch")

// ## animation info ##
// name Sparkle Fade
// abbr SPF
// colors 1
// repetitive true
// center notused
// delay used 50
// direction notused
// distance notused
// spacing notused
// ## end info ##

import animatedledstrip.animationutils.*
import animatedledstrip.leds.*
import animatedledstrip.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

@Suppress("UNRESOLVED_REFERENCE")
val leds = bindings["leds"] as AnimatedLEDStrip.Section

@Suppress("UNRESOLVED_REFERENCE")
val data = bindings["data"] as AnimationData

@Suppress("UNRESOLVED_REFERENCE")
val scope = bindings["scope"] as CoroutineScope

val color0 = data.pCols[0]
val delay = data.delay

leds.apply {
    val jobs = indices.map { n ->
        scope.launch(sparkleThreadPool) {
            delayBlocking((Math.random() * 5000).toLong())
            setAndFadePixel(
                pixel = n,
                color = color0,
                amountOfOverlay = 25,
                context = sparkleThreadPool
            )
            delayBlocking(delay)
        }
    }
    runBlockingNonCancellable {
        jobs.joinAll()
    }
}
