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

package animatedledstrip.animations.predefined

import animatedledstrip.animations.*
import animatedledstrip.colors.PreparedColorContainer
import animatedledstrip.colors.shuffledWithIndices
import animatedledstrip.leds.animationmanagement.numLEDs
import animatedledstrip.leds.animationmanagement.removeWhitespace
import animatedledstrip.leds.colormanagement.setStripProlongedColor
import kotlinx.coroutines.delay
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log
import kotlin.math.pow

val shellSort = DefinedAnimation(
    Animation.AnimationInfo(
        name = "Shell Sort",
        abbr = "SHS",
        description = "Visualization of shell sort.\n" +
                      "`colors[0]` is randomized, then a shell sort with the " +
                      "specified gap sequence is used to re-sort it.",
        runCountDefault = 1,
        minimumColors = 1,
        unlimitedColors = false,
        dimensionality = Dimensionality.oneDimensional,
        intParams = listOf(AnimationParameter("interMovementDelay", "Delay between sorting movements", 10)),
        stringParams = listOf(AnimationParameter("gapSequence",
                                                 "One of: shell, frank&lazarus, hibbard, papernov&stasevich, " +
                                                 "pratt, knuth, incerpi&sedgewick, sedgewick82, sedgewick86, " +
                                                 "tokuda, ciura",
                                                 "shell")),
    )
) { leds, params, _ ->
    val interMovementDelay = params.intParams.getValue("interMovementDelay").toLong()

    val sortablePixels =
        params.colors[0]
            .shuffledWithIndices()
            .mapIndexed { index, it -> SortablePixel(it.first, index, it.second) }
            .toMutableList()
    val startColor = PreparedColorContainer(sortablePixels.map { it.color })

    val n = leds.numLEDs.toDouble()

    val sequence: List<Int> =
        when (val name = params.stringParams.getValue("gapSequence")
            .removeWhitespace()
            .replace("[-_()]".toRegex(), "")
            .toLowerCase()
        ) {
            "shell" -> {
                fun formula(k: Int): Double = floor(n / 2.0.pow(k))
                val seq = mutableListOf<Int>()
                var k = 1
                var next = formula(k)
                while (next > 1) {
                    seq.add(next.toInt())
                    k++
                    next = formula(k)
                }
                seq.add(1)
                seq.toList()
            }
            "frank&lazarus" -> {
                fun formula(k: Int): Double = 2 * floor(n / 2.0.pow(k + 1)) + 1

                val seq = mutableListOf<Int>()
                var k = 1
                var next = formula(k)
                while (next > 3) {
                    seq.add(next.toInt())
                    k++
                    next = formula(k)
                }
                seq.add(3)
                seq.add(1)
                seq.toList()
            }
            "hibbard" -> {
                fun formula(k: Int): Double = 2.0.pow(k) - 1

                val seq = mutableListOf<Int>()
                var k = 1
                var next = formula(k)
                while (next < n) {
                    seq.add(next.toInt())
                    k++
                    next = formula(k)
                }
                seq.reversed().toList()
            }
            "papernov&stasevich" -> {
                fun formula(k: Int): Double = 2.0.pow(k) + 1

                val seq = mutableListOf<Int>()
                seq.add(1)
                var k = 1
                var next = formula(k)
                while (next < n) {
                    seq.add(next.toInt())
                    k++
                    next = formula(k)
                }
                seq.reversed()
            }
            "pratt" -> {
                val seq = mutableSetOf<Int>()
                val max2Power = log(n, 2.0).toInt()
                val max3Power = log(n, 3.0).toInt()
                for (i in 0..max2Power) {
                    pow3@ for (j in 0..max3Power) {
                        val c = 2.0.pow(i) * 3.0.pow(j)
                        if (c > n) break@pow3
                        seq.add(c.toInt())
                    }
                }
                seq.sorted().reversed()
            }
            "knuth" -> {
                fun formula(k: Int): Double = (3.0.pow(k) - 1) / 2

                val seq = mutableListOf<Int>()
                var k = 1
                var next = formula(k)
                while (next < ceil(n / 3)) {
                    seq.add(next.toInt())
                    k++
                    next = formula(k)
                }
                seq.reversed()
            }
            "incerpi&sedgewick" -> {
                // A036569
                mutableListOf(86961, 33936, 13776, 4592, 1968, 861, 336, 112, 48, 21, 7, 3, 1).filter { it < n }
            }
            "sedgewick82" -> {
                fun formula(k: Int): Double = 4.0.pow(k) + 3 * 2.0.pow(k - 1) + 1

                val seq = mutableListOf<Int>()
                seq.add(1)
                var k = 1
                var next = formula(k)
                while (next < n) {
                    seq.add(next.toInt())
                    k++
                    next = formula(k)
                }
                seq.reversed()
            }
            "sedgewick86" -> {
                fun formula(k: Int): Double =
                    if (k % 2 == 0) 9 * (2.0.pow(k) - 2.0.pow(k / 2)) + 1
                    else 8 * 2.0.pow(k) - 6 * 2.0.pow((k + 1) / 2) + 1

                val seq = mutableListOf<Int>()
                seq.add(1)
                var k = 1
                var next = formula(k)
                while (next < n) {
                    seq.add(next.toInt())
                    k++
                    next = formula(k)
                }
                seq.reversed()
            }
            "tokuda" -> {
                fun formula(k: Int): Double = ceil(0.2 * (9 * (9.0 / 4.0).pow(k - 1) - 4))

                val seq = mutableListOf<Int>()
                var k = 1
                var next = formula(k)
                while (next < n) {
                    seq.add(next.toInt())
                    k++
                    next = formula(k)
                }
                seq.reversed()
            }
            "ciura" -> {
                // A102549
                mutableListOf(1750, 701, 301, 132, 57, 23, 10, 4, 1).filter { it < n }
            }
            else -> error("$name is not a valid gap sequence")
        }

    leds.apply {
        setStripProlongedColor(startColor)
        for (gap in sequence) {
            for (i in gap until numLEDs) {
                val tmp = sortablePixels[i]
                var j = i
                while (j >= gap && sortablePixels[j - gap].finalLocation > tmp.finalLocation) {
                    sortablePixels[j] = sortablePixels[j - gap]
                    updateColorAtIndex(j, sortablePixels)
                    j -= gap
                    delay(interMovementDelay)
                }
                sortablePixels[j] = tmp
                updateColorAtIndex(j, sortablePixels)
                delay(interMovementDelay)
            }
        }
    }
}
