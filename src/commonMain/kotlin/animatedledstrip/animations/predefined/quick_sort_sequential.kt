package animatedledstrip.animations.predefined

import animatedledstrip.animations.Animation
import animatedledstrip.animations.ParamUsage
import animatedledstrip.animations.PredefinedAnimation
import animatedledstrip.colors.PreparedColorContainer
import animatedledstrip.colors.shuffledWithIndices
import animatedledstrip.leds.colormanagement.setPixelProlongedColor
import animatedledstrip.leds.colormanagement.setStripProlongedColor
import kotlinx.coroutines.delay
import kotlin.random.Random

val quickSortSequential = PredefinedAnimation(
    Animation.AnimationInfo(
        name = "Quick Sort (Sequential)",
        abbr = "QKS",
        description = "Visualization of quick sort.\n" +
                      "`pCols[0]` is randomized, then a quick sort is " +
                      "used to re-sort it. Pivot locations are chosen randomly.",
        signatureFile = "quick_sort_sequential.png",
        runCountDefault = 1,
        minimumColors = 1,
        unlimitedColors = false,
        center = ParamUsage.NOTUSED,
        delay = ParamUsage.USED,
        delayDefault = 25,
        direction = ParamUsage.NOTUSED,
        distance = ParamUsage.NOTUSED,
        spacing = ParamUsage.NOTUSED,
    )
) { leds, params, _ ->

    data class SortablePixel(val finalLocation: Int, val currentLocation: Int, val color: Int)

    val colorMap =
        params.colors[0]
            .shuffledWithIndices()
            .mapIndexed { index, it -> SortablePixel(it.first, index, it.second) }
            .toMutableList()
    val color = PreparedColorContainer(colorMap.map { it.color })

    leds.apply {
        setStripProlongedColor(color)

        fun updateColorAtLocation(location: Int) {
            setPixelProlongedColor(location, colorMap[location].color)
        }

        suspend fun partition(startIndex: Int, endIndex: Int): Int {
            var pivotLocation = Random.nextInt(startIndex, endIndex)
            var i = startIndex
            var j = pivotLocation + 1

            while (i < pivotLocation) {
                if (colorMap[i].finalLocation > colorMap[pivotLocation].finalLocation) {
                    val tmp = colorMap[i]
                    for (p in i until pivotLocation) {
                        colorMap[p] = colorMap[p + 1]
                        updateColorAtLocation(p)
                    }
                    colorMap[pivotLocation] = tmp
                    updateColorAtLocation(pivotLocation)
                    pivotLocation--
                    delay(params.delay)
                } else i++
            }

            while (j <= endIndex) {
                if (colorMap[j].finalLocation < colorMap[pivotLocation].finalLocation) {
                    val tmp = colorMap[j]
                    for (p in j downTo pivotLocation + 1) {
                        colorMap[p] = colorMap[p - 1]
                        updateColorAtLocation(p)
                    }
                    colorMap[pivotLocation] = tmp
                    updateColorAtLocation(pivotLocation)
                    pivotLocation++
                    delay(params.delay)
                }
                j++
            }
            return pivotLocation
        }

        suspend fun sort(startIndex: Int, endIndex: Int) {
            if (startIndex >= endIndex) return

            val pivot = partition(startIndex, endIndex)
            sort(startIndex, pivot - 1)
            sort(pivot + 1, endIndex)
        }
        sort(0, colorMap.lastIndex)
    }
}
