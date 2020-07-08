///*
// *  Copyright (c) 2018-2020 AnimatedLEDStrip
// *
// *  Permission is hereby granted, free of charge, to any person obtaining a copy
// *  of this software and associated documentation files (the "Software"), to deal
// *  in the Software without restriction, including without limitation the rights
// *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// *  copies of the Software, and to permit persons to whom the Software is
// *  furnished to do so, subject to the following conditions:
// *
// *  The above copyright notice and this permission notice shall be included in
// *  all copies or substantial portions of the Software.
// *
// *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// *  THE SOFTWARE.
// */
//
//package animatedledstrip.animationutils
//
//import animatedledstrip.leds.AnimatedLEDStrip
//import animatedledstrip.utils.scriptCache
//import com.google.gson.ExclusionStrategy
//import com.google.gson.FieldAttributes
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.runBlocking
//import kotlin.script.experimental.api.*
//import kotlin.script.experimental.host.toScriptSource
//import kotlin.script.experimental.host.with
//import kotlin.script.experimental.jvm.*
//import kotlin.script.experimental.jvm.util.classpathFromClass
//import kotlin.script.experimental.jvm.util.scriptCompilationClasspathFromContextOrNull
//import kotlin.script.experimental.jvmhost.JvmScriptCompiler
//
///**
// * Stores information about a defined animation, including information about it
// * and the compiled script. Code is compiled when the class is initialized.
// *
// * @property rawCode A string with the code to compile for this animation
// */
//class ScriptedAnimation(
//    override val info: AnimationInfo,
//    val rawCode: String
//) : Animation(info) {
//
//    companion object {
//        const val prefix = "SANM"
//
//        object ExStrategy : ExclusionStrategy {
//            override fun shouldSkipClass(p0: Class<*>?) = false
//
//            override fun shouldSkipField(field: FieldAttributes?): Boolean {
//                return when (field?.name) {
//                    "animationScriptingEngine", "code" -> true
//                    else -> false
//                }
//            }
//        }
//    }
//
//    override val prefix = ScriptedAnimation.prefix
//
//    private val hostConfig = defaultJvmScriptingHostConfiguration.with {
//        jvm {
//            baseClassLoader.replaceOnlyDefault(null)
//            compilationCache(scriptCache)
//        }
//    }
//
//    private val compileConfig = ScriptCompilationConfiguration().with {
//        dependencies(JvmDependencyFromClassLoader { Direction::class.java.classLoader })
//        dependencies(JvmDependencyFromClassLoader { AnimatedLEDStrip::class.java.classLoader })
//        updateClasspath(scriptCompilationClasspathFromContextOrNull())
//        updateClasspath(classpathFromClass<AnimationData>())
//        providedProperties(
//            "providedLEDs" to AnimatedLEDStrip.Section::class,
//            "providedData" to AnimationData::class,
//            "providedScope" to CoroutineScope::class
//        )
////        updateClasspath(KotlinJars.kotlinScriptStandardJarsWithReflect)
////        updateClasspath(classpathFromClass(AnimatedLEDStrip::class))
////        updateClasspath(classpathFromClass(Job::class))
//        hostConfiguration.update { hostConfig }
//    }
//
//    /**
//     * The JSR223 scripting engine used to compile this animation and create
//     * bindings for it.
//     */
//    val jvmCompiler = JvmScriptCompiler(hostConfig)
//    val jvmEvaluator = BasicJvmScriptEvaluator()
//
//    /**
//     * The compiled animation script.
//     */
//    val code: CompiledScript<*> = runBlocking {
//        jvmCompiler(
//            rawCode.toScriptSource(),
//            compileConfig
//        ).valueOr { failure ->
//            throw RuntimeException(
//                failure.reports.joinToString("\n") { it.exception?.toString() ?: it.message }
//                    .replace("in script.kts", "in ${info.name}"),
//                failure.reports.find { it.exception != null }?.exception
//            )
//        }
//    }
//
//
//    override fun runAnimation(leds: AnimatedLEDStrip.Section, data: AnimationData, scope: CoroutineScope) = runBlocking {
//        println("CCCCC")
//        println(
//            jvmEvaluator(code, ScriptEvaluationConfiguration().with {
//                providedProperties(
//                    "providedLEDs" to leds,
//                    "providedData" to data,
//                    "providedScope" to scope
//                )
//            })
//        )
//    }
//
//
//
//    /**
//     * Only show the animation info but not the raw code when converted to a string.
//     */
//    override fun toString(): String {
//        return "Animation(info=$info)"
//    }
//
//    override fun toHumanReadableString(): String =
//        """
//            Animation Definition
//              info:
//              ${info.toHumanReadableString()}
//              code: $rawCode
//            End Definition
//        """.trimMargin()
//}
