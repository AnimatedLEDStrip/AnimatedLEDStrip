package animatedledstrip.test

/*
 *  Copyright (c) 2019 AnimatedLEDStrip
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


import animatedledstrip.animationutils.AnimationInfo
import animatedledstrip.animationutils.ReqLevel
import org.junit.Test
import kotlin.test.assertTrue

class AnimationInfoTest {

    @Test
    fun testDefaultConstruction() {
        val ai = AnimationInfo()

        assertTrue { ai.abbr == "" }
        assertTrue { ai.numReqColors == 0 }
        assertTrue { ai.numOptColors == 0 }
        assertTrue { ai.numColors == 0 }
        assertTrue { ai.delay == ReqLevel.NOTUSED }
        assertTrue { ai.delayDefault == 0L }
        assertTrue { ai.direction == ReqLevel.NOTUSED }
        assertTrue { ai.spacing == ReqLevel.NOTUSED }
        assertTrue { ai.spacingDefault == 0 }
    }

}