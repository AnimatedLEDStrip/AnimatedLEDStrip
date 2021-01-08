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

package animatedledstrip.test.animations

import animatedledstrip.animations.Animation
import animatedledstrip.animations.Dimensionality
import animatedledstrip.animations.predefined.bubbleSort
import animatedledstrip.animations.predefined.mergeSortParallel
import animatedledstrip.communication.decodeJson
import animatedledstrip.communication.toUTF8String
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

class AnimationInfoTest : StringSpec(
    {
        "constructor" {
            val info = Animation.AnimationInfo(
                name = "Test",
                abbr = "TST",
                description = "A test animation",
                signatureFile = "sig.png",
                runCountDefault = 1,
                minimumColors = 4,
                unlimitedColors = true,
                dimensionality = Dimensionality.oneDimensional,
                directional = false,
                // TODO: Test remaining properties
            )

            info.name shouldBe "Test"
            info.abbr shouldBe "TST"
            info.description shouldBe "A test animation"
            info.signatureFile shouldBe "sig.png"
            info.minimumColors shouldBe 4
            info.unlimitedColors.shouldBeTrue()
            info.dimensionality.shouldContainExactly(Dimensionality.ONE_DIMENSIONAL)
            info.directional.shouldBeFalse()

        }

        "encode JSON".config(enabled = false) {
            // TODO: 1/5/2021 Fix
            bubbleSort.info.jsonString() shouldBe """{"type":"AnimationInfo","name":"Bubble Sort","abbr":"BST","dimensionality":"ONE_DIMENSIONAL","description":"Visualization of bubble sort.\n`pCols[0]` is randomized, then bubble sort is used to resort it.","signatureFile":"bubble_sort.png","runCountDefault":1,"minimumColors":1,"unlimitedColors":false,"center":"NOTUSED","delay":"USED","direction":"NOTUSED","distance":"NOTUSED","spacing":"NOTUSED","delayDefault":5,"distanceDefault":-1,"spacingDefault":3};;;"""
        }
        "decode JSON".config(enabled = false) {
            // TODO: 1/5/2021 Fix
            val json =
                """{"type":"AnimationInfo", "name":"Alternate","abbr":"ALT","dimensionality":"ONE_DIMENSIONAL","description":"A description","signatureFile":"alternate.png","runCountDefault":1,"minimumColors":2,"unlimitedColors":true,"center":"NOTUSED","delay":"USED","direction":"NOTUSED","distance":"NOTUSED","spacing":"NOTUSED","delayDefault":1000,"distanceDefault":20,"spacingDefault":3}"""

            val correctData = Animation.AnimationInfo(
                name = "Alternate",
                abbr = "ALT",
                description = "A description",
                signatureFile = "alternate.png",
                runCountDefault = 1,
                minimumColors = 2,
                unlimitedColors = true,
                dimensionality = Dimensionality.oneDimensional,
                directional = true,
            )

            json.decodeJson() as Animation.AnimationInfo shouldBe correctData
        }

        "encode and decode JSON" {
            val info1 = mergeSortParallel.info
            val infoBytes = info1.json()

            val info2 = infoBytes.toUTF8String().decodeJson() as Animation.AnimationInfo

            info2 shouldBe info1
        }
    })
