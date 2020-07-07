import animatedledstrip.animationutils.definedAnimations
import animatedledstrip.animationutils.ParamUsage
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
    if (info.repetitive)
        file.append("[![Repetitive](https://img.shields.io/badge/-repetitive-blue.svg)](Repetitive-vs-NonRepetitive)\n")
    else
        file.append("[![Non-Repetitive](https://img.shields.io/badge/-non%20repetitive-lightgrey.svg)](Repetitive-vs-NonRepetitive)\n")

    file.append("\n")
    file.append("|Quality|Usage|Default|\n")
    file.append("|:-:|:-:|:-:|\n")
    file.append("|name|`${info.name}`||\n")
    file.append("|abbr|`${info.abbr}`||\n")
    file.append("|repetitive|`${info.repetitive}`||\n")
    file.append("|minimum colors|`${info.minimumColors}`||\n")
    file.append("|unlimited colors|`${info.unlimitedColors}`||\n")
    file.append("|center|`${info.center}`||\n")
    file.append("|delay|`${info.delay}`|${if (info.delay == ParamUsage.USED) "`${info.delayDefault}`" else ""}|\n")
    file.append("|direction|`${info.direction}`||\n")
    file.append("|distance|`${info.distance}`|${if (info.distance == ParamUsage.USED) "`${if (info.distanceDefault == -1) "whole strip" else info.distanceDefault.toString()}`" else ""}|\n")
    file.append("|spacing|`${info.spacing}`|${if (info.spacing == ParamUsage.USED) "`${info.spacingDefault}`" else ""}|\n")

    file.append("\n")
    file.append("## Description\n")
    file.append(info.description)

    file.append("\n\n")
    file.append("## [Animation Signature](Animation-Signatures)\n")
    file.append("![${info.name} Signature](https://github.com/AnimatedLEDStrip/AnimatedLEDStrip/blob/master/animation-signatures/${info.signatureFile})\n")
    file.append("\n")
    file.append("###### Last updated: `v${args[0]}`\n")
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
            "\n<!-- END ANIM LIST -->"
)

val fileWriter = FileWriter("wiki/_Sidebar.md")
fileWriter.write(sidebar)
fileWriter.close()