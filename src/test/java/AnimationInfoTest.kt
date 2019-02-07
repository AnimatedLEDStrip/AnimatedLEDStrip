package animatedledstrip.test

import animatedledstrip.leds.AnimationInfo
import animatedledstrip.leds.ReqLevel
import org.junit.Test
import kotlin.test.assertTrue

class AnimationInfoTest {

    @Test
    fun testDefaultConstruction() {
        val ai = AnimationInfo()

        assertTrue { ai.abbr == "" }
        assertTrue { ai.color1 == ReqLevel.NOTUSED }
        assertTrue { ai.color2 == ReqLevel.NOTUSED }
        assertTrue { ai.color3 == ReqLevel.NOTUSED }
        assertTrue { ai.color4 == ReqLevel.NOTUSED }
        assertTrue { ai.color5 == ReqLevel.NOTUSED }
        assertTrue { ai.delay == ReqLevel.NOTUSED }
        assertTrue { ai.delayDefault == 0L }
        assertTrue { ai.direction == ReqLevel.NOTUSED }
        assertTrue { ai.spacing == ReqLevel.NOTUSED }
        assertTrue { ai.spacingDefault == 0 }
    }

}