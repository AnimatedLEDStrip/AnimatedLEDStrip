package animatedledstrip.animationutils.predefined

import animatedledstrip.animationutils.Animation
import animatedledstrip.animationutils.ParamUsage
import animatedledstrip.animationutils.PredefinedAnimation
import animatedledstrip.colors.PreparedColorContainer
import animatedledstrip.utils.delayBlocking

val bubbleSort = PredefinedAnimation(
    Animation.AnimationInfo(
        name = "Bubble Sort",
        abbr = "BST",
        repetitive = false,
        minimumColors = 1,
        center = ParamUsage.NOTUSED,
        delay = ParamUsage.USED,
        delayDefault = 5,
        direction = ParamUsage.NOTUSED,
        distance = ParamUsage.NOTUSED,
        spacing = ParamUsage.NOTUSED
    )
) { leds, data, _ ->
    val colorMap = data.pCols[0].colors.mapIndexed { index, it -> Pair(index, it) }.shuffled().toMutableList()
    val color = PreparedColorContainer(colorMap.map { it.second })
    val delay = data.delay

    leds.apply {
        setProlongedStripColor(color)

        var keepSearching = true

        while (keepSearching) {
            keepSearching = false
            for (pixel in 0 until numLEDs - 1) {
                if (colorMap[pixel].first > colorMap[pixel + 1].first) {
                    keepSearching = true
                    val temp = colorMap[pixel]
                    colorMap[pixel] = colorMap[pixel + 1]
                    setProlongedPixelColor(pixel, colorMap[pixel].second)
                    colorMap[pixel + 1] = temp
                    setProlongedPixelColor(pixel + 1, colorMap[pixel + 1].second)
                }
                delayBlocking(delay)
            }
        }
    }
}
