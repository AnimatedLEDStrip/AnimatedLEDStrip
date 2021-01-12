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

package animatedledstrip.test.leds.animationmanagement

import animatedledstrip.leds.animationmanagement.AnimationToRunParams
import animatedledstrip.leds.animationmanagement.endAnimation
import animatedledstrip.leds.animationmanagement.removeWhitespace
import animatedledstrip.leds.animationmanagement.startAnimation
import animatedledstrip.leds.emulation.createNewEmulatedStrip
import animatedledstrip.test.newRunningAnimationParams
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

class AnimationManagementUtilsTests : StringSpec(
    {
        "start animation with random id" {
            val ledStrip = createNewEmulatedStrip(10)

            val permittedIDs = (0..99999999).toList()

            checkAll<String>(100) {
                ledStrip.animationManager.startAnimation(AnimationToRunParams(animation = "Color")).params.id.toInt() shouldBeIn permittedIDs
            }
            ledStrip.renderer.close()
        }

        "start animation with custom id" {
            val ledStrip = createNewEmulatedStrip(10)
            checkAll<String> { id ->
                ledStrip.animationManager.startAnimation(AnimationToRunParams(animation = "Color"),
                                                         id).params.id shouldBe id
            }
            ledStrip.renderer.close()
        }

//        "end animation that is running" {
//            val ledStrip = createNewEmulatedStrip(10)
//            checkAll<String> { id ->
//                ledStrip.animationManager.startAnimation(AnimationToRunParams(animation = "Color", runCount = -1), id)
//                ledStrip.animationManager.runningAnimations.ids.shouldContain(id)
//                ledStrip.animationManager.endAnimation(id)
//                withTimeout(1000) {
//                    ledStrip.animationManager.runningAnimations[id]?.join()
//                }
//            }
//            delay(1000)
//            ledStrip.animationManager.runningAnimations.ids.shouldBeEmpty()
//        }

        "animation to run params to end animation" {
            val params = AnimationToRunParams(id = "Test")
            val end = params.endAnimation()
            params.id shouldBe end.id
        }

        "running animation params to end animation" {
            val params = newRunningAnimationParams.copy(id = "test2")
            val end = params.endAnimation()
            params.id shouldBe end.id
        }

        "remove whitespace spaces" {
            "     ".removeWhitespace() shouldBe ""
            "a b c d e".removeWhitespace() shouldBe "abcde"
        }

        "remove whitespace tabs" {
            "\t".removeWhitespace() shouldBe ""
            "a\tb\tc\td\te".removeWhitespace() shouldBe "abcde"
        }

        "remove whitespace newlines" {
            "\n\r".removeWhitespace() shouldBe ""
            "a\nb\rc\nd\re".removeWhitespace() shouldBe "abcde"
        }
    }
)
