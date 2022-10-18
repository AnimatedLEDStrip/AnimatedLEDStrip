/*
 * Copyright (c) 2018-2022 AnimatedLEDStrip
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package animatedledstrip.leds.stripmanagement

import animatedledstrip.leds.animationmanagement.LEDStripAnimationManager
import animatedledstrip.leds.animationmanagement.RunningAnimationParams
import animatedledstrip.leds.colormanagement.LEDStripColorManager
import animatedledstrip.leds.locationmanagement.PixelLocationManager
import animatedledstrip.leds.sectionmanagement.LEDStripSectionManager
import animatedledstrip.leds.sectionmanagement.Section

/**
 * Manages the managers that manage all aspects of the strip, from animations
 * to sending colors to the strip to sections of the strip, etc.
 *
 * @property stripInfo Information about this strip
 * @property nativeLEDStrip The actual LED strip this instance is managing
 */
class LEDStrip(
    val stripInfo: StripInfo,
    val nativeLEDStrip: NativeLEDStrip,
) {

    /**
     * The number of pixels in this strip
     */
    val numLEDs: Int = stripInfo.numLEDs

    init {
        require(numLEDs > 0) { "Number of LEDs must be greater than 0" }
    }

    /**
     * A list of the valid pixel indices in this strip
     */
    val pixelIndices: List<Int> = IntRange(0, stripInfo.numLEDs - 1).toList()

    /**
     * Manages colors on the strip
     */
    val colorManager = LEDStripColorManager(this)

    /**
     * Manages rendering the colors on the strip
     */
    val renderer: LEDStripRenderer = LEDStripRenderer(nativeLEDStrip, colorManager, stripInfo.renderDelay)

    /**
     * Manages strip sections
     */
    val sectionManager: LEDStripSectionManager = LEDStripSectionManager(this)

    /**
     * Manages animations running on the strip
     */
    val animationManager: LEDStripAnimationManager = LEDStripAnimationManager(sectionManager)

    val pixelLocationManager: PixelLocationManager = PixelLocationManager(stripInfo.ledLocations, numLEDs)


    /**
     * Callback run before the first iteration of the animation
     */
    var startAnimationCallback: ((RunningAnimationParams) -> Any?)? = null

    /**
     * Callback run after the last iteration of the animation (but before the
     * animation is removed from `animationManager.runningAnimations`)
     */
    var endAnimationCallback: ((RunningAnimationParams) -> Any?)? = null

    /**
     * Callback run when an animation is paused
     */
    var pauseAnimationCallback: ((RunningAnimationParams) -> Any?)? = null

    /**
     * Callback run when an animation is resumed
     */
    var resumeAnimationCallback: ((RunningAnimationParams) -> Any?)? = null

    /**
     * Callback to run when a new section is created
     */
    var newSectionCallback: ((Section) -> Any?)? = null

    fun close() {
        renderer.close()
//        animationManager.animationScope.cancel()
    }
}
