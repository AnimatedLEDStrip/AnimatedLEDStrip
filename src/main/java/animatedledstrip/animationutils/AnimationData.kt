package animatedledstrip.animationutils

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


import animatedledstrip.colors.ccpresets.CCBlack
import animatedledstrip.colors.ColorContainer
import animatedledstrip.colors.ColorContainerInterface
import animatedledstrip.utils.parseHex
import java.io.Serializable

/**
 * Class used when calling animations to specify colors, parameters, etc.
 * for the animation.
 */
class AnimationData() : Serializable {

    /* parameters */

    /**
     * The animation to be run (REQUIRED).
     */
    lateinit var animation: Animation

    /**
     * The list of ColorContainers
     */
    val colors = mutableListOf<ColorContainerInterface>()


    /* Helper properties for the first 5 ColorContainers */

    var color0: ColorContainerInterface
        get() = if (colors.size > 0) colors[0] else CCBlack
        set(value) {
            color(value, 0)
        }
    var color1: ColorContainerInterface
        get() = if (colors.size > 1) colors[1] else CCBlack
        set(value) {
            color(value, 1)
        }
    var color2: ColorContainerInterface
        get() = if (colors.size > 2) colors[2] else CCBlack
        set(value) {
            color(value, 2)
        }
    var color3: ColorContainerInterface
        get() = if (colors.size > 3) colors[3] else CCBlack
        set(value) {
            color(value, 3)
        }
    var color4: ColorContainerInterface
        get() = if (colors.size > 4) colors[4] else CCBlack
        set(value) {
            color(value, 4)
        }

    /**
     * Specifies if the animation will run endlessly until stopped.
     */
    var continuous = true

    /**
     * Delay time (in milliseconds) used in the animation.
     */
    var delay = 0L
        get() {
            return (when (field) {
                0L -> {
                    when (animationInfoMap[animation]?.delay) {
                        ReqLevel.REQUIRED -> throw Exception("Animation delay required for $animation")
                        ReqLevel.OPTIONAL -> animationInfoMap[animation]?.delayDefault ?: 50L
                        ReqLevel.NOTUSED -> 50L
                        null -> 50L
                    }
                }
                else -> field
            } * delayMod).toLong()
        }

    /**
     * Multiplier for the `delay` value.
     */
    var delayMod = 1.0

    /**
     * 'Direction' the animation should run.
     */
    var direction = Direction.FORWARD

    /**
     * Last pixel on the strip that will show the animation (inclusive).
     */
    var endPixel = 0

    /**
     * ID for the animation (used by server and client for stopping continuous
     * animations).
     */
    var id = ""

    /**
     * Spacing used in the animation.
     */
    var spacing = 0
        get() {
            return (when (field) {
                0 -> {
                    when (animationInfoMap[animation]?.spacing) {
                        ReqLevel.REQUIRED -> throw Exception("Animation spacing required for $animation")
                        ReqLevel.OPTIONAL -> animationInfoMap[animation]?.spacingDefault ?: 3
                        ReqLevel.NOTUSED -> 3
                        null -> 3
                    }
                }
                else -> field
            })
        }

    /**
     * First pixel on the strip will show the animation.
     */
    var startPixel = 0


    /* Helper functions for setting values */

    /**
     * Sets the `animation` parameter.
     *
     * @param animation The animation to run.
     */
    fun animation(animation: Animation): AnimationData {
        this.animation = animation
        return this
    }


    /**
     * Set the color using a ColorContainer, hex String, or Int or Long
     * in range(0..16777215)
     *
     * @param color
     * @param index The index of the color in the list of colors
     */
    fun color(color: Any, index: Int = 0): AnimationData {

        if (colors.size <= index) for (i in colors.size..index) colors += CCBlack

        when (color) {
            is ColorContainerInterface -> colors[index] = color.toColorContainer()
            is Long -> colors[index] = ColorContainer(color)
            is Int -> colors[index] = ColorContainer(color.toLong())
            is String -> colors[index] = ColorContainer(parseHex(color))
        }

        return this
    }

    /* Helpers for setting the first 5 ColorContainers */

    fun color0(color: ColorContainerInterface) = color(color, 0)
    fun color0(color: Long) = color(color, 0)
    fun color0(color: Int) = color(color, 0)
    fun color0(color: String) = color(color, 0)

    fun color1(color: ColorContainerInterface) = color(color, 1)
    fun color1(color: Long) = color(color, 1)
    fun color1(color: Int) = color(color, 1)
    fun color1(color: String) = color(color, 1)

    fun color2(color: ColorContainerInterface) = color(color, 2)
    fun color2(color: Long) = color(color, 2)
    fun color2(color: Int) = color(color, 2)
    fun color2(color: String) = color(color, 2)

    fun color3(color: ColorContainerInterface) = color(color, 3)
    fun color3(color: Long) = color(color, 3)
    fun color3(color: Int) = color(color, 3)
    fun color3(color: String) = color(color, 3)

    fun color4(color: ColorContainerInterface) = color(color, 4)
    fun color4(color: Long) = color(color, 4)
    fun color4(color: Int) = color(color, 4)
    fun color4(color: String) = color(color, 4)

    /**
     * Set the `continuous` parameter.
     *
     * @param continuous A `Boolean`
     */
    fun continuous(continuous: Boolean): AnimationData {
        this.continuous = continuous
        return this
    }

    /**
     * Set the `delay` parameter.
     *
     * @param delay An `Int` representing the delay time in milliseconds the
     * animation will use
     */
    fun delay(delay: Int): AnimationData {
        this.delay = delay.toLong()
        return this
    }

    /**
     * Set the `delay` parameter.
     *
     * @param delay A `Long` representing the delay time in milliseconds the
     * animation will use
     */
    fun delay(delay: Long): AnimationData {
        this.delay = delay
        return this
    }

    /**
     * Set the `delayMod` parameter.
     *
     * @param delayMod A `Double` that is a multiplier for `delay`
     */
    fun delayMod(delayMod: Double): AnimationData {
        this.delayMod = delayMod
        return this
    }

    /**
     * Set the `direction` parameter.
     *
     * @param direction A `Direction` value ([Direction].`FORWARD` or [Direction].`BACKWARD`)
     */
    fun direction(direction: Direction): AnimationData {
        this.direction = direction
        return this
    }

    /**
     * Set the `direction` parameter with a `Char`.
     *
     * @param direction A `Char` representing `Direction.FORWARD` ('`F`') or
     * `Direction.BACKWARD` ('`B`')
     */
    fun direction(direction: Char): AnimationData {
        this.direction = when (direction) {
            'F', 'f' -> Direction.FORWARD
            'B', 'b' -> Direction.BACKWARD
            else -> throw Exception("Direction chars can be 'F' or 'B'")
        }
        return this
    }

    /**
     * Set the `endPixel` parameter.
     *
     * @param endPixel An `Int` that is the index of the last pixel showing the
     * animation (inclusive)
     */
    fun endPixel(endPixel: Int): AnimationData {
        this.endPixel = endPixel
        return this
    }

    /**
     * Set the `id` parameter.
     *
     * @param id A `String` used to identify a continuous animation instance
     */
    fun id(id: String): AnimationData {
        this.id = id
        return this
    }

    /**
     * Set the `spacing` parameter.
     *
     * @param spacing An `Int` that is the spacing used by the animation
     */
    fun spacing(spacing: Int): AnimationData {
        this.spacing = spacing
        return this
    }

    /**
     * Set the `startPixel` parameter.
     *
     * @param startPixel An `Int` that is the index of the first pixel showing the
     * animation (inclusive)
     */
    fun startPixel(startPixel: Int): AnimationData {
        this.startPixel = startPixel
        return this
    }

//    /**
//     * Constructor used by the server when receiving a `Map` from a client
//     */
//    constructor(params: Map<*, *>) : this() {
//        val a = params["Animation"]
//        animation = when (a) {
//            null -> throw Exception("Animation not defined")
//            is Animation -> params["Animation"] as Animation
//            is String -> {
//                when (a.toUpperCase()) {
//                    "COL" -> Animation.COLOR
//                    "MCOL" -> Animation.MULTICOLOR
//                    else -> try {
//                        animationInfoMap.entries.filter {
//                            it.value.abbr == (params["Animation"] as String).toUpperCase()
//                        }[0].key
//                    } catch (e: IndexOutOfBoundsException) {
//                        throw Exception("Animation parameter not defined")
//                    }
//                }
//            }
//            else -> throw Exception("Invalid type for animation parameter")
//        }
//        color0 = when (params["Color0"]) {
//            is Long -> ColorContainer(params["Color0"] as Long)
//            is List<*> -> ColorContainer(mutableListOf<Long>().apply {
//                (params["Color0"] as List<*>).forEach {
//                    this.add(it as Long)
//                }
//            })
//            is ColorContainer -> ColorContainer(params["Color0"] as ColorContainer)
//            else -> ColorContainer(0x0)
//        }
//
//
//        color1 = when (params["Color1"]) {
//            is Long -> ColorContainer(params["Color1"] as Long)
//            is List<*> -> ColorContainer(ColorContainer(mutableListOf<Long>().apply {
//                (params["Color1"] as List<*>).forEach {
//                    this.add(it as Long)
//                }
//            }))
//            is ColorContainer -> ColorContainer(params["Color1"] as ColorContainer)
//            else -> ColorContainer(0x0)
//        }
//
//        color2 = when (params["Color2"]) {
//            is Long -> ColorContainer(params["Color2"] as Long)
//            is List<*> -> ColorContainer(ColorContainer(mutableListOf<Long>().apply {
//                (params["Color2"] as List<*>).forEach {
//                    this.add(it as Long)
//                }
//            }))
//            is ColorContainer -> ColorContainer(params["Color2"] as ColorContainer)
//            else -> ColorContainer(0x0)
//        }
//
//        color3 = when (params["Color3"]) {
//            is Long -> ColorContainer(params["Color3"] as Long)
//            is List<*> -> ColorContainer(ColorContainer(mutableListOf<Long>().apply {
//                (params["Color3"] as List<*>).forEach {
//                    this.add(it as Long)
//                }
//            }))
//            is ColorContainer -> ColorContainer(params["Color3"] as ColorContainer)
//            else -> ColorContainer(0x0)
//        }
//
//        color4 = when (params["Color4"]) {
//            is Long -> ColorContainer(params["Color4"] as Long)
//            is List<*> -> ColorContainer(ColorContainer(mutableListOf<Long>().apply {
//                (params["Color4"] as List<*>).forEach {
//                    this.add(it as Long)
//                }
//            }))
//            is ColorContainer -> ColorContainer(params["Color4"] as ColorContainer)
//            else -> ColorContainer(0x0)
//        }
//
//        if (params["ColorList"] as List<*>? != null) {
//            (params["ColorList"] as List<*>).forEach { c ->
//                when (c) {
//                    is Long -> color0 as ColorContainer += (c)
//                    is ColorContainer -> color0 = c
//                }
//            }
//        }
//        continuous = params["Continuous"] as Boolean? ?: false
//        delay = when (params["Delay"]) {
//            is Long -> params["Delay"] as Long
//            is Int -> (params["Delay"] as Int).toLong()
//            null -> when (animationInfoMap[animation]?.delay) {
//                ReqLevel.REQUIRED -> throw Exception("Animation delay required for $animation")
//                else -> animationInfoMap[animation]?.delayDefault ?: 0L
//            }
//            else -> 0L
//        }
//        delayMod = params["DelayMod"] as Double? ?: 1.0
//        direction = when (params["Direction"]) {
//            is Char -> when (params["Direction"]) {
//                'F', 'f' -> Direction.FORWARD
//                'B', 'b' -> Direction.BACKWARD
//                else -> throw Exception("Direction chars can be 'F' or 'B'")
//            }
//            is Direction -> params["Direction"] as Direction
//            else -> Direction.FORWARD
//        }
//
//        endPixel = params["EndPixel"] as Int? ?: 0
//        id = params["ID"] as String? ?: ""
//        spacing = when (params["Spacing"]) {
//            is Int -> params["Spacing"] as Int
//            null -> when (animationInfoMap[animation]?.spacing) {
//                ReqLevel.REQUIRED -> throw Exception("Animation spacing required for $animation")
//                else -> animationInfoMap[animation]?.spacingDefault ?: 3
//            }
//            else -> 3
//        }
//        startPixel = params["StartPixel"] as Int? ?: 0
//    }


    /**
     * Create a `String` out of the values of this instance.
     */
    override fun toString() =
            "$animation: $color0, $color1, $color2, $color3, $color4, $continuous, $delay, $direction, $id, $spacing"


//    /**
//     * Create a `Map` that can be sent over a socket.
//     */
//    fun toMap() = mapOf<String, Any?>(
//            "Animation" to animation,
//            "Color0" to color0.toColorContainer().colors,
//            "Color1" to color1.toColorContainer().colors,
//            "Color2" to color2.toColorContainer().colors,
//            "Color3" to color3.toColorContainer().colors,
//            "Color4" to color4.toColorContainer().colors,
//            "Continuous" to continuous,
//            "Delay" to delay,
//            "DelayMod" to delayMod,
//            "Direction" to when (direction) {
//                Direction.FORWARD -> 'F'
//                Direction.BACKWARD -> 'B'
//            },
//            "EndPixel" to endPixel,
//            "ID" to id,
//            "Spacing" to spacing,
//            "StartPixel" to startPixel
//    )

}
