package animatedledstrip.leds.sectionmanagement

import animatedledstrip.leds.animationmanagement.AnimationManager

fun AnimationManager.createSection(name: String, startPixel: Int, endPixel: Int): Section =
    sectionManager.createSection(name, startPixel, endPixel)

fun AnimationManager.getSection(sectionName: String): Section =
    sectionManager.getSection(sectionName)

fun AnimationManager.getSubSection(startPixel: Int, endPixel: Int): Section =
    sectionManager.getSubSection(startPixel, endPixel)