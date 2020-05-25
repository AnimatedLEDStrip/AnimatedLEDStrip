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

package animatedledstrip.animationutils

import org.jetbrains.kotlin.cli.common.environment.setIdeaIoUseFallback
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine
import javax.script.CompiledScript
import javax.script.ScriptEngineManager

/**
 * Stores information about a defined animation, including information about it
 * and the compiled script. Code is compiled when the class is initialized.
 *
 * @property rawCode A string with the code to compile for this animation
 */
data class Animation(
    val info: AnimationInfo,
    val rawCode: String
) {

    /**
     * The JSR223 scripting engine used to compile this animation and create
     * bindings for it.
     */
    val animationScriptingEngine: KotlinJsr223JvmLocalScriptEngine = run {
        setIdeaIoUseFallback()
        ScriptEngineManager().getEngineByExtension("kts")!! as KotlinJsr223JvmLocalScriptEngine
    }

    /**
     * The compiled animation script.
     */
    val code: CompiledScript = animationScriptingEngine.compile(rawCode)

    /**
     * Stores information about an animation.
     *
     * @property name The name used to identify this animation
     * @property abbr
     * @property numReqColors The number of required colors for this animation
     * @property numOptColors The number of optional colors for this animation
     * @property repetitive Can this animation be repeated
     *   (see https://github.com/AnimatedLEDStrip/AnimatedLEDStrip/wiki/Repetitive-vs-NonRepetitive-vs-Radial)
     * @property center Does this animation use the `center` parameter
     * @property delay Does this animation use the `delay` parameter
     * @property delayDefault Default value for the `delay` parameter
     * @property direction Does this animation use the `direction` parameter
     * @property distance Does this animation use the `distance` parameter
     * @property distanceDefault Default value for the `distance` parameter
     * @property spacing Does this animation use the `spacing` parameter
     * @property spacingDefault Default value for the `spacing` parameter
     */
    data class AnimationInfo(
        val name: String,
        val abbr: String,
        val numReqColors: Int,
        val numOptColors: Int,
        val repetitive: Boolean,
        val center: ParamUsage,
        val delay: ParamUsage,
        val delayDefault: Long,
        val direction: ParamUsage,
        val distance: ParamUsage,
        val distanceDefault: Int,
        val spacing: ParamUsage,
        val spacingDefault: Int
    ) {
        val numColors: Int = numReqColors + numOptColors
    }

    /**
     * Only show the animation info but not the raw code when converted to a string.
     */
    override fun toString(): String {
        return "Animation(info=$info)"
    }
}
