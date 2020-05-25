package animatedledstrip.test

import animatedledstrip.animationutils.*
import org.junit.Test
import org.pmw.tinylog.Configurator
import org.pmw.tinylog.Level
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DefineAnimationTest {

    private val testLogs = TestLogWriter()
    private val baseStr = "// name Test\n// abbr T\n"

    @Test
    fun testDefineNewAnimationMissingInfo() {


    }


    @Test
    fun testParseAnimationInfoName() {
        val info1 = parseAnimationInfo("// name Test\n// abbr T\n", "Test")
        assertTrue { info1.name == "Test" }

        val info2 = parseAnimationInfo("// name Test TEST\n// abbr T\n", "Test")
        assertTrue { info2.name == "Test TEST" }

        assertFailsWith<BadParamNotEnoughArgsException> {
            parseAnimationInfo("// name", "Test")
        }

        assertFailsWith<MissingParamException> {
            parseAnimationInfo("", "Test")
        }
    }

    @Test
    fun testParseAnimationInfoAbbr() {
        val info1 = parseAnimationInfo("// name Test\n// abbr T\n", "Test")
        assertTrue { info1.abbr == "T" }

        assertFailsWith<BadParamNotEnoughArgsException> {
            parseAnimationInfo("// abbr", "Test")
        }

        assertFailsWith<MissingParamException> {
            parseAnimationInfo("// name Test", "Test")
        }
    }

    @Test
    fun testParseAnimationInfoColors() {
        val info1 = parseAnimationInfo("${baseStr}// colors 5", "Test")
        assertTrue { info1.numReqColors == 5 }

        assertFailsWith<BadParamNotEnoughArgsException> {
            parseAnimationInfo("${baseStr}// colors", "Test")
        }

        assertFailsWith<BadParamInvalidTypeException> {
            parseAnimationInfo("${baseStr}// colors x", "Test")
        }
    }

    @Test
    fun testParseAnimationInfoOptColors() {
        val info1 = parseAnimationInfo("${baseStr}// optColors 5", "Test")
        assertTrue { info1.numOptColors == 5 }

        assertFailsWith<BadParamNotEnoughArgsException> {
            parseAnimationInfo("${baseStr}// optColors", "Test")
        }

        assertFailsWith<BadParamInvalidTypeException> {
            parseAnimationInfo("${baseStr}// optColors x", "Test")
        }
    }

    @Test
    fun testParseAnimationInfoRepetitive() {
        val info1 = parseAnimationInfo("${baseStr}// repetitive true", "Test")
        assertTrue { info1.repetitive }

        val info2 = parseAnimationInfo("${baseStr}// repetitive false", "Test")
        assertFalse { info2.repetitive }

        val info3 = parseAnimationInfo("${baseStr}// repetitive x", "Test")
        assertFalse { info3.repetitive }

        assertFailsWith<BadParamNotEnoughArgsException> {
            parseAnimationInfo("${baseStr}// repetitive", "Test")
        }
    }

    @Test
    fun testParseAnimationInfoCenter() {
        val info1 = parseAnimationInfo(baseStr, "Test")
        assertTrue { info1.center == ParamUsage.NOTUSED }

        val info2 = parseAnimationInfo("${baseStr}// center notused", "Test")
        assertTrue { info2.center == ParamUsage.NOTUSED }

        val info3 = parseAnimationInfo("${baseStr}// center used", "Test")
        assertTrue { info3.center == ParamUsage.USED }

        assertFailsWith<BadParamNotEnoughArgsException> {
            parseAnimationInfo("${baseStr}// center", "Test")
        }

        assertFailsWith<BadParamInvalidTypeException> {
            parseAnimationInfo("${baseStr}// center x", "Test")
        }
    }

    @Test
    fun testParseAnimationInfoDelay() {
        Configurator.currentConfig().addWriter(testLogs, Level.DEBUG).activate()
        testLogs.clearLogs()

        val info1 = parseAnimationInfo(baseStr, "Test")
        assertTrue { info1.delay == ParamUsage.NOTUSED }

        val info2 = parseAnimationInfo("${baseStr}// delay notused", "Test")
        assertTrue { info2.delay == ParamUsage.NOTUSED }

        val info3 = parseAnimationInfo("${baseStr}// delay used", "Test")
        assertTrue { info3.delay == ParamUsage.USED }
        assertTrue { info3.delayDefault == DEFAULT_DELAY }
        testLogs.checkLogs(setOf(Pair(Level.WARNING, "Test: Param delay does not have a default, using 50")))

        val info4 = parseAnimationInfo("${baseStr}// delay used 5", "Test")
        assertTrue { info4.delay == ParamUsage.USED }
        assertTrue { info4.delayDefault == 5L }

        assertFailsWith<BadParamNotEnoughArgsException> {
            parseAnimationInfo("${baseStr}// delay", "Test")
        }

        assertFailsWith<BadParamInvalidTypeException> {
            parseAnimationInfo("${baseStr}// delay x", "Test")
        }

        assertFailsWith<BadParamInvalidTypeException> {
            parseAnimationInfo("${baseStr}// delay used x", "Test")
        }

        Configurator.currentConfig().removeWriter(testLogs).activate()
    }

    @Test
    fun testParseAnimationInfoDirection() {
        val info1 = parseAnimationInfo(baseStr, "Test")
        assertTrue { info1.direction == ParamUsage.NOTUSED }

        val info2 = parseAnimationInfo("${baseStr}// direction notused", "Test")
        assertTrue { info2.direction == ParamUsage.NOTUSED }

        val info3 = parseAnimationInfo("${baseStr}// direction used", "Test")
        assertTrue { info3.direction == ParamUsage.USED }

        assertFailsWith<BadParamNotEnoughArgsException> {
            parseAnimationInfo("${baseStr}// direction", "Test")
        }

        assertFailsWith<BadParamInvalidTypeException> {
            parseAnimationInfo("${baseStr}// direction x", "Test")
        }
    }


    @Test
    fun testParseAnimationInfoDistance() {
        Configurator.currentConfig().addWriter(testLogs, Level.DEBUG).activate()
        testLogs.clearLogs()

        val info1 = parseAnimationInfo(baseStr, "Test")
        assertTrue { info1.distance == ParamUsage.NOTUSED }

        val info2 = parseAnimationInfo("${baseStr}// distance notused", "Test")
        assertTrue { info2.distance == ParamUsage.NOTUSED }

        val info3 = parseAnimationInfo("${baseStr}// distance used", "Test")
        assertTrue { info3.distance == ParamUsage.USED }
        assertTrue { info3.distanceDefault == -1 }
        testLogs.checkLogs(
            setOf(Pair(Level.WARNING, "Test: Param distance does not have a default, using full length of strip"))
        )

        val info4 = parseAnimationInfo("${baseStr}// distance used 5", "Test")
        assertTrue { info4.distance == ParamUsage.USED }
        assertTrue { info4.distanceDefault == 5 }

        assertFailsWith<BadParamNotEnoughArgsException> {
            parseAnimationInfo("${baseStr}// distance", "Test")
        }

        assertFailsWith<BadParamInvalidTypeException> {
            parseAnimationInfo("${baseStr}// distance x", "Test")
        }

        assertFailsWith<BadParamInvalidTypeException> {
            parseAnimationInfo("${baseStr}// distance used x", "Test")
        }

        Configurator.currentConfig().removeWriter(testLogs).activate()
    }

    @Test
    fun testParseAnimationInfoSpacing() {
        Configurator.currentConfig().addWriter(testLogs, Level.DEBUG).activate()
        testLogs.clearLogs()

        val info1 = parseAnimationInfo(baseStr, "Test")
        assertTrue { info1.spacing == ParamUsage.NOTUSED }

        val info2 = parseAnimationInfo("${baseStr}// spacing notused", "Test")
        assertTrue { info2.spacing == ParamUsage.NOTUSED }

        val info3 = parseAnimationInfo("${baseStr}// spacing used", "Test")
        assertTrue { info3.spacing == ParamUsage.USED }
        assertTrue { info3.spacingDefault == DEFAULT_SPACING }
        testLogs.checkLogs(
            setOf(Pair(Level.WARNING, "Test: Param spacing does not have a default, using 3"))
        )

        val info4 = parseAnimationInfo("${baseStr}// spacing used 5", "Test")
        assertTrue { info4.spacing == ParamUsage.USED }
        assertTrue { info4.spacingDefault == 5 }

        assertFailsWith<BadParamNotEnoughArgsException> {
            parseAnimationInfo("${baseStr}// spacing", "Test")
        }

        assertFailsWith<BadParamInvalidTypeException> {
            parseAnimationInfo("${baseStr}// spacing x", "Test")
        }

        assertFailsWith<BadParamInvalidTypeException> {
            parseAnimationInfo("${baseStr}// spacing used x", "Test")
        }

        Configurator.currentConfig().removeWriter(testLogs).activate()
    }
}