package animatedledstrip.leds.stripmanagement

import animatedledstrip.leds.animationmanagement.RunningAnimationParams
import animatedledstrip.leds.sectionmanagement.LEDStripSectionManager
import animatedledstrip.leds.animationmanagement.LEDStripAnimationManager
import animatedledstrip.leds.colormanagement.LEDStripColorManager
import animatedledstrip.leds.sectionmanagement.Section

class LEDStrip(
    val stripInfo: StripInfo,
    val nativeLEDStrip: NativeLEDStrip,
) {

    val numLEDs: Int = stripInfo.numLEDs

    val validIndices: List<Int> = IntRange(0, stripInfo.numLEDs - 1).toList()

    val colorManager = LEDStripColorManager(this)

    val renderer: LEDStripRenderer = LEDStripRenderer(nativeLEDStrip, colorManager)

    val sectionManager: LEDStripSectionManager = LEDStripSectionManager(this)

    val animationManager: LEDStripAnimationManager = LEDStripAnimationManager(sectionManager)


    /**
     * Callback run before the first iteration of the animation.
     */
    var startAnimationCallback: ((RunningAnimationParams) -> Any?)? = null

    /**
     * Callback run after the last iteration of the animation (but before the
     * animation is removed from `runningAnimations`).
     */
    var endAnimationCallback: ((RunningAnimationParams) -> Any?)? = null

    /**
     * Callback to run when a new section is created.
     */
    var newSectionCallback: ((Section) -> Any?)? = null
}