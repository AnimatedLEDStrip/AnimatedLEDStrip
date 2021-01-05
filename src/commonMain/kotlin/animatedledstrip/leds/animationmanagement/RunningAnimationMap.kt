/*
 * Copyright (c) 2018-2021 AnimatedLEDStrip
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package animatedledstrip.leds.animationmanagement

/**
 * Custom implementation of a map in order to prevent third-party code from
 * changing the map. In other words, we don't want to start an animation
 * and then be unable to stop it because we lost the `Job` instance associated
 * with it.
 */
class RunningAnimationMap {

    /**
     * The underlying map. Visibility is set to internal so that this library
     * can run methods directly on the map if necessary, but others cannot.
     */
    internal val map =
        mutableMapOf<String, RunningAnimation>()

    /**
     * Operator alias for getting a map entry
     */
    internal operator fun get(key: String): RunningAnimation? = map[key]

    /**
     * Operator alias for setting a map entry
     */
    internal operator fun set(key: String, value: RunningAnimation) = map.set(key, value)

    /**
     * Remove a map entry
     */
    internal fun remove(key: String): RunningAnimation? = map.remove(key)


    /**
     * A copy of the list of entries in the map
     */
    val entries: List<Pair<String, RunningAnimation>>
        get() = map.entries.map { it.toPair() }

    /**
     * A copy of the list of IDs (keys) in the map
     */
    val ids: List<String>
        get() = map.keys.toList()

    /**
     * A copy of the list of animations (values) in the map
     */
    val animations: List<RunningAnimation>
        get() = map.values.toList()
}
