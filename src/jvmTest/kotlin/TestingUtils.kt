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

import animatedledstrip.animations.*
import animatedledstrip.animations.predefined.color
import animatedledstrip.colors.PreparedColorContainer
import animatedledstrip.leds.animationmanagement.AnimationToRunParams
import animatedledstrip.leds.animationmanagement.RunningAnimationParams
import animatedledstrip.leds.colormanagement.pixelProlongedColorList
import animatedledstrip.leds.colormanagement.pixelTemporaryColorList
import animatedledstrip.leds.locationmanagement.Location
import animatedledstrip.leds.sectionmanagement.SectionManager
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import kotlin.test.assertTrue

val newRunningAnimationParams: RunningAnimationParams
    get() = RunningAnimationParams(color, "", listOf(), "", "", -1,
                                   Direction.FORWARD, mapOf(), mapOf(), mapOf(),
                                   mapOf(), mapOf(), AnimationToRunParams())

fun haveProlongedColors(colors: PreparedColorContainer) = object : Matcher<SectionManager> {
    override fun test(value: SectionManager) =
        MatcherResult(passed = value.pixelProlongedColorList == colors.colors,
                      failureMessage = "Prolonged colors do not match:\nExpected:\n" +
                                       "    ${colors.colors}\nActual:\n    ${value.pixelProlongedColorList}",
                      negatedFailureMessage = "Prolonged colors match:\n" +
                                              "    ${value.pixelProlongedColorList}")
}

fun SectionManager.assertAllPixelProlongedColors(prolongedColor: Int) {
    val colors = pixelProlongedColorList
    colors.forEachIndexed { i, c ->
        assertTrue(
            "Pixel $i check failed (prolonged). Expected: $prolongedColor on all pixels. Actual: $colors"
        ) { c == prolongedColor }
    }
}

fun SectionManager.assertAllTemporaryPixels(color: Int) {
    stripManager.pixelTemporaryColorList.forEachIndexed { i, c ->
        assertTrue(
            "Pixel $i check failed (temporary). Expected: $color on all pixels. Actual (${
                startPixel + getPhysicalIndex(0)
            }:${endPixel + getPhysicalIndex(0)}): ${stripManager.pixelTemporaryColorList}"
        ) { c == color }
    }
}

fun SectionManager.assertAllProlongedPixels(color: Int) {
    stripManager.pixelProlongedColorList.forEachIndexed { i, c ->
        assertTrue(
            "Pixel $i check failed (prolonged). Expected: $color on all pixels. Actual (${
                startPixel + getPhysicalIndex(0)
            }:${endPixel + getPhysicalIndex(0)}): ${stripManager.pixelProlongedColorList}"
        ) { c == color }
    }
}

fun SectionManager.assertPixels(indices: IntRange, temporaryColor: Int, prolongedColor: Int) {
    assertTemporaryPixels(indices, temporaryColor)
    assertProlongedPixels(indices, prolongedColor)
}

fun SectionManager.assertTemporaryPixels(indices: IntRange, color: Int) {
    indices.forEach {
        assertTrue(
            "Pixel $it check failed (temporary). Expected: $color on pixels ${indices.first}..${indices.last}. Actual (${
                startPixel + getPhysicalIndex(0)
            }:${endPixel + getPhysicalIndex(0)}): ${stripManager.pixelTemporaryColorList}"
        ) { stripManager.pixelTemporaryColorList[it] == color }
    }
}

fun SectionManager.assertProlongedPixels(indices: IntRange, color: Int) {
    indices.forEach {
        assertTrue(
            "Pixel $it check failed (prolonged). Expected: $color on pixels ${indices.first}..${indices.last}. Actual (${
                startPixel + getPhysicalIndex(0)
            }:${endPixel + getPhysicalIndex(0)}): ${stripManager.pixelProlongedColorList}"
        ) { stripManager.pixelProlongedColorList[it] == color }
    }
}

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

val filteredStringArb = Arb.string().filter { !it.contains("\"") && !it.contains("\\") }
val intArb = Arb.int()
val dimensionalityArb = Arb.enum<Dimensionality>()
val nullableIntArb = Arb.int().orNull()
val nullableDoubleArb = Arb.double().orNull()
val nullableLocationArb = locationArb.orNull()
val nullableDistanceArb = distanceArb.orNull()

val animIntParamArb: Arb<AnimationParameter<Int>> =
    arbitrary { rs ->
        AnimationParameter(filteredStringArb.next(rs), filteredStringArb.next(rs), nullableIntArb.next(rs))
    }

val animDoubleParamArb: Arb<AnimationParameter<Double>> =
    arbitrary { rs ->
        AnimationParameter(filteredStringArb.next(rs), filteredStringArb.next(rs), nullableDoubleArb.next(rs))
    }

val animLocationParamArb: Arb<AnimationParameter<Location>> =
    arbitrary { rs ->
        AnimationParameter(filteredStringArb.next(rs), filteredStringArb.next(rs), nullableLocationArb.next(rs))
    }

val animDistanceParamArb: Arb<AnimationParameter<Distance>> =
    arbitrary { rs ->
        AnimationParameter(filteredStringArb.next(rs), filteredStringArb.next(rs), nullableDistanceArb.next(rs))
    }

data class ArbParams(
    val intParams: List<AnimationParameter<Int>>,
    val doubleParams: List<AnimationParameter<Double>>,
    val locationParams: List<AnimationParameter<Location>>,
    val distanceParams: List<AnimationParameter<Distance>>,
)

val animParamsArb: Arb<ArbParams> =
    Arb.bind(Arb.list(animIntParamArb, 0..3),
             Arb.list(animDoubleParamArb, 0..3),
             Arb.list(animLocationParamArb, 0..3),
             Arb.list(animDistanceParamArb, 0..3)) { i, d, l, ds ->
        ArbParams(i, d, l, ds)
    }

data class ArbInfo(
    val name: String,
    val abbr: String,
    val description: String,
    val signatureFile: String,
    val runCountDefault: Int,
    val minimumColors: Int,
    val unlimitedColors: Boolean,
    val dimensionality: Set<Dimensionality>,
    val directional: Boolean,
)

val animInfoArb: Arb<ArbInfo> =
    arbitrary { rs ->
        ArbInfo(
            filteredStringArb.next(rs),
            filteredStringArb.next(rs),
            filteredStringArb.next(rs),
            filteredStringArb.next(rs),
            intArb.next(rs),
            intArb.next(rs),
            Arb.bool().next(rs),
            Arb.set(dimensionalityArb, 1..3).next(rs),
            Arb.bool().next(rs))
    }