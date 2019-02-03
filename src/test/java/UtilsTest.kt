package animatedledstrip.test

import animatedledstrip.leds.parseHex
import org.junit.Test
import org.pmw.tinylog.Configurator
import org.pmw.tinylog.Level
import kotlin.test.assertTrue

class UtilsTest {

    @Test
    fun testParseHex() {
        assertTrue { parseHex("0xFF") == 0xFFL }
        assertTrue { parseHex("FFFF") == 0xFFFFL }
        assertTrue { parseHex("FF0000") == 0xFF0000L }
        Configurator.defaultConfig().level(Level.OFF).activate()
        assertTrue { parseHex("0xG") == 0L }
    }

}