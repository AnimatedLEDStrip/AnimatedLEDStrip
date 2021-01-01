/*
 *  Copyright (c) 2018-2020 AnimatedLEDStrip
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package animatedledstrip.test.leds.animationmanagement

import animatedledstrip.leds.animationmanagement.AnimationToRunParams
import animatedledstrip.leds.animationmanagement.endAnimation
import animatedledstrip.leds.animationmanagement.removeWhitespace
import animatedledstrip.test.newRunningAnimationParams
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class AnimationManagementUtilsTests : StringSpec(
    {
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
