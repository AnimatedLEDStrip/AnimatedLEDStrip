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

import animatedledstrip.animations.definedAnimations
import java.io.FileReader
import java.io.FileWriter

val animList = mutableListOf<String>()

definedAnimations.forEach {
    val info = it.value.info

    val fileName = info.name.replace(" ", "-").replace("(", "").replace(")", "")

    animList.add("  - [${info.name}]($fileName)")

    val file = FileWriter("wiki/$fileName.md")

    file.append("<!-- THIS FILE IS AUTOMATICALLY GENERATED -->\n")
    file.append("<!-- MAKE CHANGES TO THE AnimationInfo INSTANCE ASSOCIATED WITH THIS ANIMATION -->\n\n")
    file.append("## Animation Info\n")
//    if (info.repetitive)
//        file.append("[![Repetitive](https://img.shields.io/badge/-repetitive-blue.svg)](Repetitive-vs-NonRepetitive)\n")
//    else
//        file.append("[![Non-Repetitive](https://img.shields.io/badge/-non%20repetitive-lightgrey.svg)](Repetitive-vs-NonRepetitive)\n")

    file.append("\n")
    file.append("|Quality|Value|\n")
    file.append("|:-:|:-:|\n")
    file.append("|name|`${info.name}`|\n")
    file.append("|abbr|`${info.abbr}`|\n")
    file.append("|runCountDefault|${if (info.runCountDefault != -1) "`${info.runCountDefault}`" else "Endless"}|\n")
    file.append("|minimum colors|`${info.minimumColors}`|\n")
    file.append("|unlimited colors|`${info.unlimitedColors}`|\n")
    file.append("|dimensionality|`${info.dimensionality}`|\n")
    file.append("|directional|`${info.directional}`|\n")
    file.append("\n")
    file.append("|Parameter|Type|Default Value|Description|\n")
    file.append("|:-:|:-:|:-:|:-:|\n")
    for (param in info.intParams)
        file.append("|${param.name}|`Int`|`${param.default ?: ""}`|${param.description}|\n")
    for (param in info.doubleParams)
        file.append("|${param.name}|`Double`|`${param.default ?: ""}`|${param.description}|\n")
    for (param in info.locationParams)
        file.append("|${param.name}|`Location`|`${param.default?.coordinates ?: "Center of all pixels"}`|${param.description}|\n")
    for (param in info.distanceParams)
        file.append("|${param.name}|`Distance`|`${param.default ?: "Enough to encompass all pixels"}`|${param.description}|\n")
    for (param in info.equationParams)
        file.append("|${param.name}|`Equation`|`${param.default ?: ""}`|${param.description}|\n")

    file.append("\n")
    file.append("## Description\n")
    file.append(info.description)

    val sigName = "(?<=[a-zA-Z ])[A-Z]|(?<=[ ])[a-z]|\\(".toRegex().replace(info.name) {
        "_${it.value}"
    }.replace("[\\s()]".toRegex(), "").toLowerCase()

    file.append("\n\n")
    file.append("## [Animation Signature](Animation-Signatures)\n")
    file.append("![${info.name} Signature](https://github.com/AnimatedLEDStrip/AnimatedLEDStrip/blob/master/animation-signatures/${sigName}.png)\n")
    file.close()
}

val fileReader = FileReader("wiki/_Sidebar.md")
var sidebar = fileReader.readText()
fileReader.close()

sidebar = sidebar.replace(
    Regex("<!-- START ANIM LIST -->[\\s\\S]*<!-- END ANIM LIST -->"),
    "<!-- START ANIM LIST -->\n" +
    "<!-- THIS SECTION IS AUTOMATICALLY GENERATED - ANY MODIFICATIONS WILL BE OVERWRITTEN -->\n" +
    "- **Animations**\n" +
    animList.joinToString("\n") +
    "\n<!-- END ANIM LIST -->",
)

val fileWriter = FileWriter("wiki/_Sidebar.md")
fileWriter.write(sidebar)
fileWriter.close()
