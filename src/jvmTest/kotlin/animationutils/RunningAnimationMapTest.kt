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

package animatedledstrip.test.animationutils

import animatedledstrip.animationutils.RunningAnimation
import animatedledstrip.animationutils.RunningAnimationMap
import animatedledstrip.test.newRunningAnimationParams
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.maps.shouldNotContainKey
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlinx.coroutines.Job


class RunningAnimationMapTest : StringSpec(
    {

        "get" {
            val map = RunningAnimationMap()

            map.map["TEST"] = RunningAnimation(newRunningAnimationParams, Job())

            map["TEST"] shouldBeSameInstanceAs map.map["TEST"]
        }

        "set" {
            val map = RunningAnimationMap()
            val testAnim = RunningAnimation(newRunningAnimationParams, Job())

            map["TEST"] = testAnim

            map.map["TEST"] shouldBeSameInstanceAs testAnim
        }

        "remove" {
            val map = RunningAnimationMap()

            map.map["TEST"] = RunningAnimation(newRunningAnimationParams, Job())

            map.map.shouldContainKey("TEST")
            map.remove("TEST")
            map.map.shouldNotContainKey("TEST")
        }

        "entries" {
            val map = RunningAnimationMap()
            val testAnim = RunningAnimation(newRunningAnimationParams, Job())
            map.map["TEST"] = testAnim

            map.entries.shouldContainExactly(Pair("TEST", testAnim))
        }

        "ids" {
            val map = RunningAnimationMap()
            map.map["TEST"] = RunningAnimation(newRunningAnimationParams, Job())

            map.ids.shouldContainExactly("TEST")
        }

        "animations" {
            val map = RunningAnimationMap()
            val testAnim = RunningAnimation(newRunningAnimationParams, Job())
            map.map["TEST"] = testAnim

            map.animations.shouldContainExactly(testAnim)
        }
    })
