package animatedledstrip.leds

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


import animatedledstrip.ccpresets.CCBlack

/**
 * Class used when calling animations to specify colors, parameters, etc.
 * for the animation.
 */
class AnimationData() {

    /* parameters */

    /**
     * The animation to be run (REQUIRED).
     */
    lateinit var animation: Animation


    /**
     * The first color.
     */
    var color1: ColorContainer = CCBlack
    /**
     * The second color.
     */
    var color2: ColorContainer = CCBlack
    /**
     * The third color.
     */
    var color3: ColorContainer = CCBlack
    /**
     * The fourth color.
     */
    var color4: ColorContainer = CCBlack
    /**
     * The fifth color.
     */
    var color5: ColorContainer = CCBlack

    /**
     * A variable-length list of colors for the animation.
     */
    var colorList = mutableListOf<ColorContainer>()

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
     * Set the first color with a `ColorContainer`.
     *
     * @param color A `ColorContainer` instance.
     */
    fun color1(color: ColorContainer): AnimationData {
        this.color1 = color
        return this
    }

    /**
     * Set the first color with a `Long`.
     *
     * @param color A `Long` in range(0..16777215)
     */
    fun color1(color: Long): AnimationData {
        this.color1 = ColorContainer(color)
        return this
    }

    /**
     * Set the first color with an `Int`.
     *
     * @param color An `Int` in range(0..16777215)
     */
    fun color1(color: Int): AnimationData {
        this.color1 = ColorContainer(color.toLong())
        return this
    }

    /**
     * Set the first color with a hexadecimal string.
     *
     * @param color A `String` in hexadecimal format
     */
    fun color1(color: String): AnimationData {
        this.color1 = ColorContainer(parseHex(color))
        return this
    }

    /**
     * Set the first color with a `ColorContainer`.
     *
     * @param color A `ColorContainer` instance.
     */
    fun color(color: ColorContainer): AnimationData {
        this.color1 = color
        return this
    }

    /**
     * Set the first color with a `Long`.
     *
     * @param color A `Long` in range(0..16777215)
     */
    fun color(color: Long): AnimationData {
        this.color1 = ColorContainer(color)
        return this
    }

    /**
     * Set the first color with an `Int`.
     *
     * @param color An `Int` in range(0..16777215)
     */
    fun color(color: Int): AnimationData {
        this.color1 = ColorContainer(color.toLong())
        return this
    }

    /**
     * Set the first color with a hexadecimal string.
     *
     * @param color A `String` in hexadecimal format
     */
    fun color(color: String): AnimationData {
        this.color1 = ColorContainer(parseHex(color))
        return this
    }

    /**
     * Set the second color with a `ColorContainer`.
     *
     * @param color A `ColorContainer` instance.
     */
    fun color2(color: ColorContainer): AnimationData {
        this.color2 = color
        return this
    }

    /**
     * Set the second color with a `Long`.
     *
     * @param color A `Long` in range(0..16777215)
     */
    fun color2(color: Long): AnimationData {
        this.color2 = ColorContainer(color)
        return this
    }

    /**
     * Set the second color with an `Int`.
     *
     * @param color An `Int` in range(0..16777215)
     */
    fun color2(color: Int): AnimationData {
        this.color2 = ColorContainer(color.toLong())
        return this
    }

    /**
     * Set the second color with a hexadecimal string.
     *
     * @param color A `String` in hexadecimal format
     */
    fun color2(color: String): AnimationData {
        this.color2 = ColorContainer(parseHex(color))
        return this
    }

    /**
     * Set the third color with a `ColorContainer`.
     *
     * @param color A `ColorContainer` instance.
     */
    fun color3(color: ColorContainer): AnimationData {
        this.color3 = color
        return this
    }

    /**
     * Set the third color with a `Long`.
     *
     * @param color A `Long` in range(0..16777215)
     */
    fun color3(color: Long): AnimationData {
        this.color3 = ColorContainer(color)
        return this
    }

    /**
     * Set the third color with an `Int`.
     *
     * @param color An `Int` in range(0..16777215)
     */
    fun color3(color: Int): AnimationData {
        this.color3 = ColorContainer(color.toLong())
        return this
    }

    /**
     * Set the third color with a hexadecimal string.
     *
     * @param color A `String` in hexadecimal format
     */
    fun color3(color: String): AnimationData {
        this.color3 = ColorContainer(parseHex(color))
        return this
    }

    /**
     * Set the fourth color with a `ColorContainer`.
     *
     * @param color A `ColorContainer` instance.
     */
    fun color4(color: ColorContainer): AnimationData {
        this.color4 = color
        return this
    }

    /**
     * Set the fourth color with a `Long`.
     *
     * @param color A `Long` in range(0..16777215)
     */
    fun color4(color: Long): AnimationData {
        this.color4 = ColorContainer(color)
        return this
    }

    /**
     * Set the fourth color with an `Int`.
     *
     * @param color An `Int` in range(0..16777215)
     */
    fun color4(color: Int): AnimationData {
        this.color4 = ColorContainer(color.toLong())
        return this
    }

    /**
     * Set the fourth color with a hexadecimal string.
     *
     * @param color A `String` in hexadecimal format
     */
    fun color4(color: String): AnimationData {
        this.color4 = ColorContainer(parseHex(color))
        return this
    }

    /**
     * Set the fifth color with a `ColorContainer`.
     *
     * @param color A `ColorContainer` instance.
     */
    fun color5(color: ColorContainer): AnimationData {
        this.color5 = color
        return this
    }

    /**
     * Set the fifth color with a `Long`.
     *
     * @param color A `Long` in range(0..16777215)
     */
    fun color5(color: Long): AnimationData {
        this.color5 = ColorContainer(color)
        return this
    }

    /**
     * Set the fifth color with an `Int`.
     *
     * @param color An `Int` in range(0..16777215)
     */
    fun color5(color: Int): AnimationData {
        this.color5 = ColorContainer(color.toLong())
        return this
    }

    /**
     * Set the fifth color with a hexadecimal string.
     *
     * @param color A `String` in hexadecimal format
     */
    fun color5(color: String): AnimationData {
        this.color5 = ColorContainer(parseHex(color))
        return this
    }


    /**
     * Set the colorList with a `List` of `ColorContainer`s, `Long`s, or `String`s in
     * hexadecimal format.
     *
     * @param colorList A `List<ColorContainer>`, `List<Long>`, or `List<String>` instance
     */
    fun colorList(colorList: List<*>): AnimationData {
        this.colorList = mutableListOf<ColorContainer>().apply {
            colorList.forEach {
                when (it) {
                    is ColorContainer -> this.add(it)
                    is Long -> this.add(ColorContainer(it))
                    is String -> this.add(ColorContainer(parseHex(it)))
                }
            }
        }
        return this
    }

    /**
     * Add a color to the colorList (added by reference).
     *
     * @param color A `ColorContainer` instance that will be added by reference
     * to colorList
     */
    fun addToColorList(color: ColorContainer): AnimationData {
        this.colorList.add(color)
        return this
    }

    /**
     * Add a color to the colorList (creates new `ColorContainer` instance)
     *
     * @param color A `Long` that will be converted to `ColorContainer` and added
     * to `colorList`
     */
    fun addToColorList(color: Long): AnimationData {
        this.colorList.add(ColorContainer(color))
        return this
    }

    /**
     * Add a color to `colorList` (creates new ColorContainer instance)
     *
     * @param color A `String` in hexadecimal format that will be converted
     * to `ColorContainer` and added to colorList
     */
    fun addToColorList(color: String): AnimationData {
        this.colorList.add(ColorContainer(parseHex(color)))
        return this
    }

    /**
     * Append a `List` of `ColorContainer`s, `Long`s, or `String`s in hexadecimal
     * format to the end of `colorList`.
     *
     * @param colorList A `List<ColorContainer>`, `List<Long>` or `List<String>`
     * instance to be added to `colorList`
     */
    fun addToColorList(colorList: List<*>): AnimationData {
        this.colorList.apply {
            colorList.forEach {
                when (it) {
                    is ColorContainer -> this.add(it)
                    is Long -> this.add(ColorContainer(it))
                    is String -> this.add(ColorContainer(parseHex(it)))
                }
            }
        }
        return this
    }

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

    /**
     * Constructor used by the server when receiving a `Map` from a client
     */
    constructor(params: Map<*, *>) : this() {
        val a = params["Animation"]
        animation = when (a) {
            null -> throw Exception("Animation not defined")
            is Animation -> params["Animation"] as Animation
            is String -> {
                when (a.toUpperCase()) {
                    "COL" -> Animation.COLOR
                    "MCOL" -> Animation.MULTICOLOR
                    else -> try {
                        animationInfoMap.entries.filter {
                            it.value.abbr == (params["Animation"] as String).toUpperCase()
                        }[0].key
                    } catch (e: IndexOutOfBoundsException) {
                        throw Exception("Animation parameter not defined")
                    }
                }
            }
            else -> throw Exception("Invalid type for animation parameter")
        }
        color1 = when (params["Color1"]) {
            is Long -> ColorContainer(params["Color1"] as Long)
            is ColorContainer -> ColorContainer(params["Color1"] as ColorContainer)
            else -> ColorContainer(0x0)
        }


        color2 = when (params["Color2"]) {
            is Long -> ColorContainer(params["Color2"] as Long)
            is ColorContainer -> ColorContainer(params["Color2"] as ColorContainer)
            else -> ColorContainer(0x0)
        }

        color3 = when (params["Color3"]) {
            is Long -> ColorContainer(params["Color3"] as Long)
            is ColorContainer -> ColorContainer(params["Color3"] as ColorContainer)
            else -> ColorContainer(0x0)
        }

        color4 = when (params["Color4"]) {
            is Long -> ColorContainer(params["Color4"] as Long)
            is ColorContainer -> ColorContainer(params["Color4"] as ColorContainer)
            else -> ColorContainer(0x0)
        }

        color5 = when (params["Color5"]) {
            is Long -> ColorContainer(params["Color5"] as Long)
            is ColorContainer -> ColorContainer(params["Color5"] as ColorContainer)
            else -> ColorContainer(0x0)
        }

        if (params["ColorList"] as List<*>? != null) {
            (params["ColorList"] as List<*>).forEach { c ->
                when (c) {
                    is Long -> colorList.add(ColorContainer(c))
                    is ColorContainer -> colorList.add(ColorContainer(c))
                }
            }
        }
        continuous = params["Continuous"] as Boolean? ?: false
        delay = params["Delay"] as Long? ?: when (animationInfoMap[animation]?.delay) {
            ReqLevel.REQUIRED -> throw Exception("Animation delay required for $animation")
            else -> 0L
        }
        delayMod = params["DelayMod"] as Double? ?: 1.0
        direction = when (params["Direction"]) {
            is Char -> when (params["Direction"]) {
                'F', 'f' -> Direction.FORWARD
                'B', 'b' -> Direction.BACKWARD
                else -> throw Exception("Direction chars can be 'F' or 'B'")
            }
            is Direction -> params["Direction"] as Direction
            else -> Direction.FORWARD
        }
        endPixel = params["EndPixel"] as Int? ?: 0
        id = params["ID"] as String? ?: ""
        spacing = params["Spacing"] as Int? ?: 3        // TODO: Replace with default like delay
        startPixel = params["StartPixel"] as Int? ?: 0
    }


    /**
     * Create a `String` out of the values of this instance.
     */
    override fun toString() =
            "$animation: $color1, $color2, $color3, $color4, $color5, $colorList, $continuous, $delay, $direction, $id, $spacing"


    /**
     * Create a `Map` that can be sent over a socket.
     */
    fun toMap() = mapOf<String, Any?>(
            "Animation" to animation,
            "Color1" to color1.hex,
            "Color2" to color2.hex,
            "Color3" to color3.hex,
            "Color4" to color4.hex,
            "Color5" to color5.hex,
            "ColorList" to mutableListOf<Long>().apply {
                colorList.forEach {
                    this.add(it.hex)
                }
            },
            "Continuous" to continuous,
            "Delay" to delay,
            "DelayMod" to delayMod,
            "Direction" to when (direction) {
                Direction.FORWARD -> 'F'
                Direction.BACKWARD -> 'B'
            },
            "EndPixel" to endPixel,
            "ID" to id,
            "Spacing" to spacing,
            "StartPixel" to startPixel
    )

}
