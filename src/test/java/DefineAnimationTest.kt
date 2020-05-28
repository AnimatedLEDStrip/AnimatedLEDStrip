package animatedledstrip.test

import animatedledstrip.animationutils.*
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.pmw.tinylog.Level
import kotlin.test.*

class DefineAnimationTest {

    private val baseStr = "// name Test\n// abbr T\n"
    private val extendedBaseStr = "// ## animation info ##\n${baseStr}\n// ## end info ##"

    @Test
    fun testDefinedAnimationNames() {
        loadPredefinedAnimations(this::class.java.classLoader)
        awaitPredefinedAnimationsLoaded()

        assertTrue {
            definedAnimationNames().containsAll(
                listOf(
                    "Alternate",
                    "Bounce",
                    "Bounce To Color",
                    "Cat Toy",
                    "Cat Toy To Color",
                    "Color",
                    "Fade To Color",
                    "Fireworks",
                    "Meteor",
                    "Multi Pixel Run",
                    "Multi Pixel Run To Color",
                    "Pixel Marathon",
                    "Pixel Run",
                    "Ripple",
                    "Smooth Chase",
                    "Smooth Fade",
                    "Sparkle",
                    "Sparkle Fade",
                    "Sparkle To Color",
                    "Splat",
                    "Stack",
                    "Stack Overflow",
                    "Wipe"
                )
            )
        }
    }

    @Test
    fun testPrepareAnimName() {
        assertTrue { prepareAnimName("T-e-S_T") == "test" }
        assertTrue { prepareAnimName("test") == "test" }
    }

    @Test
    fun testFindAnimation() {
        assertNull(findAnimation("TEST"))
        defineNewAnimation("${extendedBaseStr}\nval x = 4 + 4", "Test")
        assertNotNull(findAnimation("TEST"))
        definedAnimations.remove("test")
    }


    /* defineAnimation() */

    @Test
    fun testDefineNewAnimationMissingInfo() {
        startLogCapture()

        defineNewAnimation("", "Test")
        assertLogs(setOf(Pair(Level.ERROR, "Could not find info for animation Test in provided code")))

        stopLogCapture()
    }

    @Test
    fun testDefineNewAnimationBadInfo() {
        startLogCapture()

        defineNewAnimation("// ## animation info ##\n${baseStr}// center\n// ## end info ##", "Test")
        assertLogs(
            setOf(
                Pair(
                    Level.ERROR,
                    "animatedledstrip.animationutils.BadParamNotEnoughArgsException: Not enough arguments for center"
                )
            )
        )

        clearLogs()

        defineNewAnimation("// ## animation info ##\n// ## end info ##", "Test")
        assertLogs(
            setOf(
                Pair(
                    Level.ERROR,
                    "animatedledstrip.animationutils.MissingParamException: Missing required parameter name"
                )
            )
        )

        clearLogs()

        defineNewAnimation("// ## animation info ##\n${baseStr}// colors x\n// ## end info ##", "Test")
        assertLogs(
            setOf(
                Pair(
                    Level.ERROR,
                    "animatedledstrip.animationutils.BadParamInvalidTypeException: Invalid type for parameter colors"
                )
            )
        )

        stopLogCapture()
    }

    @Test
    fun testDefineNewAnimationBadCode() {
        startLogCapture()

        defineNewAnimation("${extendedBaseStr}\nval x = 4 + ", "Test")
        assertLogs(
            setOf(
                Pair(Level.ERROR, "Error when compiling Test:"),
                Pair(Level.ERROR, "javax.script.ScriptException: error: incomplete code")
            )
        )

        stopLogCapture()
    }

    @Test
    fun testDefineNewAnimationExists() {
        startLogCapture()

        defineNewAnimation("${extendedBaseStr}\nval x = 4 + 4", "Test")
        defineNewAnimation("${extendedBaseStr}\nval x = 4 + 4", "Test")
        assertLogs(
            setOf(
                Pair(Level.INFO, "Successfully loaded new animation Test"),
                Pair(Level.ERROR, "Animation Test already exists")
            )
        )
        assertTrue { definedAnimations.containsKey("test") }
        definedAnimations.remove("test")
        assertFalse { definedAnimations.containsKey("test") }

        stopLogCapture()
    }

    @Test
    fun testDefineNewAnimationSuccess() {
        startLogCapture()

        defineNewAnimation("${extendedBaseStr}\nval x = 4 + 4", "Test")
        assertLogs(
            setOf(
                Pair(Level.INFO, "Successfully loaded new animation Test")
            )
        )
        assertTrue { definedAnimations.containsKey("test") }
        assertTrue { findAnimation("test")?.rawCode == "${extendedBaseStr}\nval x = 4 + 4" }
        definedAnimations.remove("test")
        assertFalse { definedAnimations.containsKey("test") }

        stopLogCapture()
    }


    /* parseAnimationInfo() */

    @Test
    fun testParseAnimationInfoName() {
        val info1 = parseAnimationInfo("// name Test\n// abbr T\n", "Test")
        assertTrue { info1.name == "Test" }

        val info2 = parseAnimationInfo("// \n// name Test TEST\n// abbr T\n", "Test")
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
        startLogCapture()

        val info1 = parseAnimationInfo(baseStr, "Test")
        assertTrue { info1.delay == ParamUsage.NOTUSED }

        val info2 = parseAnimationInfo("${baseStr}// delay notused", "Test")
        assertTrue { info2.delay == ParamUsage.NOTUSED }

        val info3 = parseAnimationInfo("${baseStr}// delay used", "Test")
        assertTrue { info3.delay == ParamUsage.USED }
        assertTrue { info3.delayDefault == DEFAULT_DELAY }
        assertLogs(setOf(Pair(Level.WARNING, "Test: Param delay does not have a default, using 50")))

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

        stopLogCapture()
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
        startLogCapture()

        val info1 = parseAnimationInfo(baseStr, "Test")
        assertTrue { info1.distance == ParamUsage.NOTUSED }

        val info2 = parseAnimationInfo("${baseStr}// distance notused", "Test")
        assertTrue { info2.distance == ParamUsage.NOTUSED }

        val info3 = parseAnimationInfo("${baseStr}// distance used", "Test")
        assertTrue { info3.distance == ParamUsage.USED }
        assertTrue { info3.distanceDefault == -1 }
        assertLogs(
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

        stopLogCapture()
    }

    @Test
    fun testParseAnimationInfoSpacing() {
        startLogCapture()

        val info1 = parseAnimationInfo(baseStr, "Test")
        assertTrue { info1.spacing == ParamUsage.NOTUSED }

        val info2 = parseAnimationInfo("${baseStr}// spacing notused", "Test")
        assertTrue { info2.spacing == ParamUsage.NOTUSED }

        val info3 = parseAnimationInfo("${baseStr}// spacing used", "Test")
        assertTrue { info3.spacing == ParamUsage.USED }
        assertTrue { info3.spacingDefault == DEFAULT_SPACING }
        assertLogs(
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

        stopLogCapture()
    }


    /* Load Predefined Animations */

    @Test
    fun testLoadPredefinedAnimations() {
        removeAllAnimations()
        startLogCapture()

        assertFalse { predefinedAnimLoadComplete }
        @Suppress("EXPERIMENTAL_API_USAGE")
        loadPredefinedAnimations(this::class.java.classLoader, newSingleThreadContext("Test Loader"))
        awaitPredefinedAnimationsLoaded()
        assertTrue { predefinedAnimLoadComplete }

        assertLogs(
            setOf(
                Pair(Level.INFO, "Loading predefined animations"),
                Pair(Level.DEBUG, "Loading animation alternate"),
                Pair(Level.INFO, "Successfully loaded new animation Alternate"),
                Pair(Level.DEBUG, "Loading animation bounce"),
                Pair(Level.INFO, "Successfully loaded new animation Bounce"),
                Pair(Level.DEBUG, "Loading animation bounce_to_color"),
                Pair(Level.INFO, "Successfully loaded new animation Bounce To Color"),
                Pair(Level.DEBUG, "Loading animation cat_toy"),
                Pair(Level.INFO, "Successfully loaded new animation Cat Toy"),
                Pair(Level.DEBUG, "Loading animation cat_toy_to_color"),
                Pair(Level.INFO, "Successfully loaded new animation Cat Toy To Color"),
                Pair(Level.DEBUG, "Loading animation color"),
                Pair(Level.INFO, "Successfully loaded new animation Color"),
                Pair(Level.DEBUG, "Loading animation fade_to_color"),
                Pair(Level.INFO, "Successfully loaded new animation Fade To Color"),
                Pair(Level.DEBUG, "Loading animation fireworks"),
                Pair(Level.INFO, "Successfully loaded new animation Fireworks"),
                Pair(Level.DEBUG, "Loading animation meteor"),
                Pair(Level.INFO, "Successfully loaded new animation Meteor"),
                Pair(Level.DEBUG, "Loading animation multi_pixel_run"),
                Pair(Level.INFO, "Successfully loaded new animation Multi Pixel Run"),
                Pair(Level.DEBUG, "Loading animation multi_pixel_run_to_color"),
                Pair(Level.INFO, "Successfully loaded new animation Multi Pixel Run To Color"),
                Pair(Level.DEBUG, "Loading animation pixel_marathon"),
                Pair(Level.INFO, "Successfully loaded new animation Pixel Marathon"),
                Pair(Level.DEBUG, "Loading animation pixel_run"),
                Pair(Level.INFO, "Successfully loaded new animation Pixel Run"),
                Pair(Level.DEBUG, "Loading animation ripple"),
                Pair(Level.WARNING, "ripple: Param distance does not have a default, using full length of strip"),
                Pair(Level.INFO, "Successfully loaded new animation Ripple"),
                Pair(Level.DEBUG, "Loading animation smooth_chase"),
                Pair(Level.INFO, "Successfully loaded new animation Smooth Chase"),
                Pair(Level.DEBUG, "Loading animation smooth_fade"),
                Pair(Level.INFO, "Successfully loaded new animation Smooth Fade"),
                Pair(Level.DEBUG, "Loading animation sparkle"),
                Pair(Level.INFO, "Successfully loaded new animation Sparkle"),
                Pair(Level.DEBUG, "Loading animation sparkle_fade"),
                Pair(Level.INFO, "Successfully loaded new animation Sparkle Fade"),
                Pair(Level.DEBUG, "Loading animation sparkle_to_color"),
                Pair(Level.INFO, "Successfully loaded new animation Sparkle To Color"),
                Pair(Level.DEBUG, "Loading animation splat"),
                Pair(Level.WARNING, "splat: Param distance does not have a default, using full length of strip"),
                Pair(Level.INFO, "Successfully loaded new animation Splat"),
                Pair(Level.DEBUG, "Loading animation stack"),
                Pair(Level.INFO, "Successfully loaded new animation Stack"),
                Pair(Level.DEBUG, "Loading animation stack_overflow"),
                Pair(Level.INFO, "Successfully loaded new animation Stack Overflow"),
                Pair(Level.DEBUG, "Loading animation wipe"),
                Pair(Level.INFO, "Successfully loaded new animation Wipe"),
                Pair(Level.INFO, "Finished loading predefined animations")
            )
        )

        stopLogCapture()
    }

    @Test
    fun testLoadPredefinedAnimationFileNotFound() {
        startLogCapture()
        runBlocking {
            loadPredefinedAnimation(this@DefineAnimationTest::class.java.classLoader, "Test", animationLoadingTreadPool)
        }

        assertLogs(
            setOf(
                Pair(Level.DEBUG, "Loading animation Test"),
                Pair(Level.WARNING, "Animation Test resource file could not be found")
            )
        )

        stopLogCapture()
    }

}