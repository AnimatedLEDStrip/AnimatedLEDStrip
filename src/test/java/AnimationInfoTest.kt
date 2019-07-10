package animatedledstrip.test

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