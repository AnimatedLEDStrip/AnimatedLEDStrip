#!/usr/bin/env kotlin

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

@file:DependsOn("co.touchlab:kermit-jvm:1.2.3")
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.8.1")
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.6.3")
@file:DependsOn("../build/libs/animatedledstrip-core-jvm-1.0.2.jar")

import animatedledstrip.animations.Animation
import animatedledstrip.animations.Dimensionality
import animatedledstrip.animations.groups.AnimationGroup
import animatedledstrip.animations.groups.GroupType
import animatedledstrip.animations.parameters.*
import animatedledstrip.animations.predefinedAnimations
import animatedledstrip.animations.predefinedGroups
import animatedledstrip.leds.emulation.createNewEmulatedStrip
import java.io.FileWriter

val animList = mutableListOf<String>()

fun String.toFileName(): String = replace(" ", "-").replace("(", "").replace(")", "")

fun String.createSigName(): String =
    "(?<=[a-zA-Z ])[A-Z]|(?<=[ ])[a-z]|\\(".toRegex().replace(this) {
        "_${it.value}"
    }.replace("[\\s()]".toRegex(), "").toLowerCase()

fun createFile(info: Animation.AnimationInfo): FileWriter {
    val fileName = info.name.toFileName()

    animList.add("- [${info.name}](animations/$fileName)")

    return FileWriter("animations/$fileName.md")
}

fun createHeader(file: FileWriter, info: Animation.AnimationInfo) {
    file.append("---\n")
    file.append("title: ${info.name}\n")
    file.append("parent: Animations\n")
    file.append("---\n\n")

    file.append("<!-- THIS FILE IS AUTOMATICALLY GENERATED -->\n")
    file.append("<!-- MAKE CHANGES TO THE AnimationInfo INSTANCE ASSOCIATED WITH THIS ANIMATION -->\n\n")

    file.append("# ${info.name}\n\n")
}

val Animation.AnimationInfo.parameterCount: Int
    get() = intParams.size + doubleParams.size + stringParams.size + locationParams.size +
            distanceParams.size + rotationParams.size + equationParams.size

fun Distance.toFormattedString(): String = if (this is PercentDistance) "$x%, $y%, $z%" else "$x, $y, $z"

fun Rotation.toFormattedString(): String =
    if (this is DegreesRotation) "$xRotation°, $yRotation°, $zRotation°, $rotationOrder"
    else "$xRotation rad, $yRotation rad, $zRotation rad, $rotationOrder"

val superscriptMap: Map<Char, Char> = mapOf('0' to '⁰',
                                            '1' to '¹',
                                            '2' to '²',
                                            '3' to '³',
                                            '4' to '⁴',
                                            '5' to '⁵',
                                            '6' to '⁶',
                                            '7' to '⁷',
                                            '8' to '⁸',
                                            '9' to '⁹')

fun Int.toSuperscript(): String =
    toString().map { superscriptMap[it]!! }.joinToString("") { it.toString() }

fun Equation.toFormattedString(): String =
    if (coefficients.isEmpty()) "0x⁰"
    else coefficients.mapIndexed { index, value -> "${value}x${index.toSuperscript()}" }.joinToString(" + ")

fun createInfoDocumentation(file: FileWriter, info: Animation.AnimationInfo) {
    file.append("## Animation Info\n\n")

    file.append("|Quality|Value|\n")
    file.append("|:-:|:-:|\n")
    file.append("|name|${info.name}|\n")
    file.append("|abbr|${info.abbr}|\n")
    file.append("|runCount default|${if (info.runCountDefault != -1) info.runCountDefault else "Endless"}|\n")
    file.append("|minimum colors|${info.minimumColors}|\n")
    file.append("|unlimited colors|${info.unlimitedColors}|\n")
    file.append("|dimensionality|${info.dimensionality.joinToString()}|\n")
    file.append("\n")
    if (info.parameterCount > 0) {
        file.append("|Parameter|Type|Default Value|Description|\n")
        file.append("|:-:|:-:|:-:|:-:|\n")
        for (param in info.intParams)
            file.append("|${param.name}|Int|${param.default ?: "0"}|${param.description}|\n")
        for (param in info.doubleParams)
            file.append("|${param.name}|Double|${param.default ?: "0.0"}|${param.description}|\n")
        for (param in info.stringParams)
            file.append("|${param.name}|String|\"${param.default ?: ""}\"|${param.description}|\n")
        for (param in info.locationParams)
            file.append("|${param.name}|[Location](/core/new-animations.html#location)|${param.default?.coordinates ?: "Center of all pixels"}|${param.description}|\n")
        for (param in info.distanceParams)
            file.append("|${param.name}|[Distance](/core/new-animations.html#distance)|${param.default?.toFormattedString() ?: "Enough to encompass all pixels"}|${param.description}|\n")
        for (param in info.rotationParams)
            file.append("|${param.name}|[Rotation](/core/new-animations.html#rotation)|${param.default?.toFormattedString() ?: ""}|${param.description}|\n")
        for (param in info.equationParams)
            file.append("|${param.name}|[Equation](/core/new-animations.html#equation)|${param.default?.toFormattedString() ?: ""}|${param.description}|\n")
        file.append("\n")
    }

    file.append("## Description\n")
    file.append(info.description)
    file.append("\n\n")

    file.append("## [Animation Signature](Animation-Signatures)\n")
    if (info.dimensionality.contains(Dimensionality.ONE_DIMENSIONAL))
        file.append("### One Dimensional\n\n![${info.name} Signature](/signatures/${info.name.createSigName()}.png)\n\n")
    if (info.dimensionality.contains(Dimensionality.TWO_DIMENSIONAL))
        file.append("### Two Dimensional\n\n![${info.name} 2D Signature](/signatures/${info.name.createSigName()}.gif)\n\n")
}

val ledStrip = createNewEmulatedStrip(10)

fun createGroupDocumentation(file: FileWriter, info: AnimationGroup.NewAnimationGroupInfo) {
    when (info.groupType) {
        GroupType.ORDERED -> file.append("![Ordered](https://img.shields.io/badge/-ordered-green)\n\n")
        GroupType.RANDOMIZED -> file.append("![Randomized](https://img.shields.io/badge/-randomized-blue)\n\n")
    }

    file.append("## Included Animations\n")
    file.append(info.animationList.joinToString("\n") { "- [$it](/animations/${it.toFileName()})" })
    file.append("\n\n")

    val preparedInfo = ledStrip.animationManager.prepareGroupAnimation(info)

    createInfoDocumentation(file, preparedInfo.info)
}

predefinedAnimations.forEach {
    val file = createFile(it.info)
    createHeader(file, it.info)
    createInfoDocumentation(file, it.info)
    file.close()
}

val animationList = animList.joinToString("\n")
animList.clear()

predefinedGroups.forEach {
    val file = createFile(it.groupInfo)
    createHeader(file, it.groupInfo)
    createGroupDocumentation(file, it)
    file.close()
}

val animationGroupList = animList.joinToString("\n")

val animationListWriter = FileWriter("animations.md")
animationListWriter.append("---\n")
animationListWriter.append("title: Animations\n")
animationListWriter.append("nav_order: 2\n")
animationListWriter.append("has_children: true\n")
animationListWriter.append("---\n\n")
animationListWriter.append("# Animations\n\n")
animationListWriter.append(animationList)
animationListWriter.append("\n\n")
animationListWriter.append("# Groups\n\n")
animationListWriter.append(animationGroupList)
animationListWriter.append("\n")
animationListWriter.close()
