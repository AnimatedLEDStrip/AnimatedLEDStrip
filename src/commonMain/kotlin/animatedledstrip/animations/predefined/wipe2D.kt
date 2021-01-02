package animatedledstrip.animations.predefined

import animatedledstrip.animations.Animation
import animatedledstrip.animations.Dimensionality
import animatedledstrip.animations.ParamUsage
import animatedledstrip.animations.PredefinedAnimation
import animatedledstrip.leds.colormanagement.setPixelFadeColor
import animatedledstrip.leds.stripmanagement.LEDLocation
import kotlinx.coroutines.delay

val wipe2D = PredefinedAnimation(
    Animation.AnimationInfo(
        name = "Wipe 2D",
        abbr = "WIP2",
        dimensionality = Dimensionality.TWO_DIMENSIONAL,
        description = "Starts two [Meteor](Meteor) animations running in opposite " +
                      "directions from `center`, stopping after traveling `distance` " +
                      "or at the end of the strip/section, whichever comes first.\n" +
                      "Does not wait for the Meteor animations to be complete before " +
                      "returning, giving a ripple-like appearance when run continuously.",
        signatureFile = "ripple_2d.png",
        runCountDefault = -1,
        minimumColors = 1,
        unlimitedColors = false,
        center = ParamUsage.USED,
        delay = ParamUsage.USED,
        delayDefault = 30,
        direction = ParamUsage.NOTUSED,
        distance = ParamUsage.USED,
        distanceDefault = 1000,
        spacing = ParamUsage.USED,
        spacingDefault = 10,
    )
) { leds, params, _ ->
    val color0 = params.colors[0]
    val center = LEDLocation(0.0, 0.0, 0.0)
    val delay = params.delay
    val distance = params.distance

    leds.apply {
        val pixelsToChangePerIteration: MutableMap<Int, MutableList<Int>> = mutableMapOf()
        val changedPixels = mutableListOf<LEDLocation>()

        var finalIteration = 0
        for ((iteration, currentDistance) in (0..distance step params.spacing).withIndex()) {
            pixelsToChangePerIteration[iteration] = mutableListOf()
            for (pixel in leds.sectionManager.stripManager.ledLocationManager.ledLocations) {
                if (pixel.key.x - center.x < currentDistance && pixel.key !in changedPixels
                ) {
                    pixelsToChangePerIteration[iteration]!!.add(pixel.value)
                    changedPixels.add(pixel.key)
                }
            }
            finalIteration = iteration
        }

        for (iteration in 0..finalIteration) {
            for (pixel in pixelsToChangePerIteration[iteration]!!)
                leds.setPixelFadeColor(pixel, color0)
//            for (pixel in pixelsToRipplePerIteration[iteration - 1] ?: pixelsToRipplePerIteration[pixelsToRipplePerIteration.size - 1]!!)
//                leds.revertPixel(pixel)
            delay(delay)
        }
        delay(delay * 20)
    }
}
