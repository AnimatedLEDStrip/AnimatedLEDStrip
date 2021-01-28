/*
 * Copyright (c) 2018-2021 AnimatedLEDStrip
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

package animatedledstrip.test

import animatedledstrip.animations.AnimationParameter
import animatedledstrip.animations.Dimensionality
import animatedledstrip.animations.parameters.*
import animatedledstrip.animations.predefined.color
import animatedledstrip.colors.ColorContainer
import animatedledstrip.colors.ColorContainerInterface
import animatedledstrip.colors.PreparedColorContainer
import animatedledstrip.leds.animationmanagement.AnimationToRunParams
import animatedledstrip.leds.animationmanagement.RunningAnimationParams
import animatedledstrip.leds.colormanagement.pixelProlongedColorList
import animatedledstrip.leds.locationmanagement.Location
import animatedledstrip.leds.sectionmanagement.SectionManager
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*

val newRunningAnimationParams: RunningAnimationParams
    get() = RunningAnimationParams(color, "", listOf(), "", "", -1,
                                   mapOf(), mapOf(), mapOf(), mapOf(), mapOf(),
                                   mapOf(), mapOf(), AnimationToRunParams())

fun haveProlongedColors(colors: PreparedColorContainer) = object : Matcher<SectionManager> {
    override fun test(value: SectionManager) =
        MatcherResult(passed = value.pixelProlongedColorList == colors.colors,
                      failureMessage = "Prolonged colors do not match:\nExpected:\n" +
                                       "    ${colors.colors}\nActual:\n    ${value.pixelProlongedColorList}",
                      negatedFailureMessage = "Prolonged colors match:\n" +
                                              "    ${value.pixelProlongedColorList}")
}

//fun SectionManager.assertAllPixelProlongedColors(prolongedColor: Int) {
//    val colors = pixelProlongedColorList
//    colors.forEachIndexed { i, c ->
//        assertTrue(
//            "Pixel $i check failed (prolonged). Expected: $prolongedColor on all pixels. Actual: $colors"
//        ) { c == prolongedColor }
//    }
//}

//fun SectionManager.assertAllTemporaryPixels(color: Int) {
//    stripManager.pixelTemporaryColorList.forEachIndexed { i, c ->
//        assertTrue(
//            "Pixel $i check failed (temporary). Expected: $color on all pixels. Actual (${
//                startPixel + getPhysicalIndex(0)
//            }:${endPixel + getPhysicalIndex(0)}): ${stripManager.pixelTemporaryColorList}"
//        ) { c == color }
//    }
//}
//
//fun SectionManager.assertAllProlongedPixels(color: Int) {
//    stripManager.pixelProlongedColorList.forEachIndexed { i, c ->
//        assertTrue(
//            "Pixel $i check failed (prolonged). Expected: $color on all pixels. Actual (${
//                startPixel + getPhysicalIndex(0)
//            }:${endPixel + getPhysicalIndex(0)}): ${stripManager.pixelProlongedColorList}"
//        ) { c == color }
//    }
//}
//
//fun SectionManager.assertPixels(indices: IntRange, temporaryColor: Int, prolongedColor: Int) {
//    assertTemporaryPixels(indices, temporaryColor)
//    assertProlongedPixels(indices, prolongedColor)
//}

//fun SectionManager.assertTemporaryPixels(indices: IntRange, color: Int) {
//    indices.forEach {
//        assertTrue(
//            "Pixel $it check failed (temporary). Expected: $color on pixels ${indices.first}..${indices.last}. Actual (${
//                startPixel + getPhysicalIndex(0)
//            }:${endPixel + getPhysicalIndex(0)}): ${stripManager.pixelTemporaryColorList}"
//        ) { stripManager.pixelTemporaryColorList[it] == color }
//    }
//}
//
//fun SectionManager.assertProlongedPixels(indices: IntRange, color: Int) {
//    indices.forEach {
//        assertTrue(
//            "Pixel $it check failed (prolonged). Expected: $color on pixels ${indices.first}..${indices.last}. Actual (${
//                startPixel + getPhysicalIndex(0)
//            }:${endPixel + getPhysicalIndex(0)}): ${stripManager.pixelProlongedColorList}"
//        ) { stripManager.pixelProlongedColorList[it] == color }
//    }
//}

val largeDoubleArb: Arb<Double> = arbitrary { rs ->
    val i = rs.random.nextInt(Int.MIN_VALUE + 1, Int.MAX_VALUE - 1)
    return@arbitrary if (i < 0) i - rs.random.nextDouble()
    else i + rs.random.nextDouble()
}

val locationArb: Arb<Location> =
    Arb.bind(largeDoubleArb,
             largeDoubleArb,
             largeDoubleArb) { x, y, z -> Location(x, y, z) }

val distanceArb: Arb<Distance> =
    Arb.bind(largeDoubleArb,
             largeDoubleArb,
             largeDoubleArb,
             Arb.bool()) { x, y, z, aOrP ->
        if (aOrP) AbsoluteDistance(x, y, z) else PercentDistance(x, y, z)
    }

val absoluteDistanceArb: Arb<AbsoluteDistance> =
    Arb.bind(largeDoubleArb,
             largeDoubleArb,
             largeDoubleArb) { x, y, z -> AbsoluteDistance(x, y, z) }

val rotationArb: Arb<Rotation> =
    Arb.bind(largeDoubleArb,
             largeDoubleArb,
             largeDoubleArb,
             Arb.bool()) { x, y, z, rOrD ->
        if (rOrD) RadiansRotation(x, y, z) else DegreesRotation(x, y, z)
    }

val radiansRotationArb: Arb<RadiansRotation> =
    Arb.bind(largeDoubleArb,
             largeDoubleArb,
             largeDoubleArb) { x, y, z -> RadiansRotation(x, y, z) }

val equationArb: Arb<Equation> =
    Arb.bind(Arb.list(largeDoubleArb, 0..5),
             Arb.bool()) { c, _ -> Equation(c) }

val colorContainerArb: Arb<ColorContainerInterface> =
    Arb.bind(Arb.list(Arb.int(0..0xFFFFFF)), Arb.bool()) { c, cOrP ->
        if (cOrP) ColorContainer(c.toMutableList()) else PreparedColorContainer(c)
    }

val preparedColorContainerArb: Arb<PreparedColorContainer> =
    Arb.bind(Arb.list(Arb.int(0..0xFFFFFF)), Arb.bool()) { c, _ ->
        PreparedColorContainer(c)
    }

val filteredStringArb = Arb.string().map { it.replace("""["'#$\\]""".toRegex(), "") }
val intArb = Arb.int()
val dimensionalityArb = Arb.enum<Dimensionality>()
val nullableIntArb = Arb.int().orNull()
val nullableDoubleArb = Arb.double().orNull()
val nullableStringArb = filteredStringArb.orNull()
val nullableLocationArb = locationArb.orNull()
val nullableDistanceArb = distanceArb.orNull()
val nullableRotationArb = rotationArb.orNull()
val nullableEquationArb = equationArb.orNull()

val animIntParamArb: Arb<AnimationParameter<Int>> =
    arbitrary { rs ->
        AnimationParameter(filteredStringArb.next(rs), filteredStringArb.next(rs), nullableIntArb.next(rs))
    }

val animDoubleParamArb: Arb<AnimationParameter<Double>> =
    arbitrary { rs ->
        AnimationParameter(filteredStringArb.next(rs), filteredStringArb.next(rs), nullableDoubleArb.next(rs))
    }

val animStringParamArb: Arb<AnimationParameter<String>> =
    arbitrary { rs ->
        AnimationParameter(filteredStringArb.next(rs), filteredStringArb.next(rs), nullableStringArb.next(rs))
    }

val animLocationParamArb: Arb<AnimationParameter<Location>> =
    arbitrary { rs ->
        AnimationParameter(filteredStringArb.next(rs), filteredStringArb.next(rs), nullableLocationArb.next(rs))
    }

val animDistanceParamArb: Arb<AnimationParameter<Distance>> =
    arbitrary { rs ->
        AnimationParameter(filteredStringArb.next(rs), filteredStringArb.next(rs), nullableDistanceArb.next(rs))
    }

val animRotationParamArb: Arb<AnimationParameter<Rotation>> =
    arbitrary { rs ->
        AnimationParameter(filteredStringArb.next(rs), filteredStringArb.next(rs), nullableRotationArb.next(rs))
    }

val animEquationParamArb: Arb<AnimationParameter<Equation>> =
    arbitrary { rs ->
        AnimationParameter(filteredStringArb.next(rs), filteredStringArb.next(rs), nullableEquationArb.next(rs))
    }
