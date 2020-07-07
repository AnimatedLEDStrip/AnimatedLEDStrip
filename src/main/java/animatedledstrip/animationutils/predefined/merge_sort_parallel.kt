package animatedledstrip.animationutils.predefined

import animatedledstrip.animationutils.Animation
import animatedledstrip.animationutils.ParamUsage
import animatedledstrip.animationutils.PredefinedAnimation
import animatedledstrip.colors.PreparedColorContainer
import animatedledstrip.utils.delayBlocking
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Suppress("DuplicatedCode")
val mergeSortParallel = PredefinedAnimation(
    Animation.AnimationInfo(
        name = "Merge Sort (Parallel)",
        abbr = "MSP",
        description = "Visualization of merge sort. `pCols[0]` is randomized," +
                "then a parallelized merge sort is used to resort it.",
        signatureFile = "merge_sort_parallel.png",
        repetitive = false,
        minimumColors = 1,
        unlimitedColors = false,
        center = ParamUsage.NOTUSED,
        delay = ParamUsage.USED,
        delayDefault = 50,
        direction = ParamUsage.NOTUSED,
        distance = ParamUsage.NOTUSED,
        spacing = ParamUsage.NOTUSED
    )
) { leds, data, scope ->

    data class SortablePixel(val finalLocation: Int, val currentLocation: Int, val color: Long)

    val colorMap = data.pCols[0].colors.mapIndexed { index, it -> Pair(index, it) }.shuffled()
        .mapIndexed { index, it -> SortablePixel(it.first, index, it.second) }.toMutableList()
    val color = PreparedColorContainer(colorMap.map { it.color })

    leds.apply {
        setProlongedStripColor(color)

        fun updateColorAtLocation(location: Int) {
            setProlongedPixelColor(location, colorMap[location].color)
        }

        runBlocking {
            fun sort(startIndex: Int, endIndex: Int): Job? {
                if (startIndex == endIndex) return null

                return scope.launch {
                    val midpoint = startIndex + ((endIndex - startIndex) / 2)
                    val op1 = sort(startIndex, midpoint)
                    val op2 = sort(midpoint + 1, endIndex)

                    op1?.join()
                    op2?.join()

                    var p1 = startIndex
                    var p2 = midpoint + 1
                    for (x in 0 until endIndex - startIndex) {
                        when {
                            colorMap[p1].finalLocation < colorMap[p2].finalLocation -> {
                                p1++
                            }
                            colorMap[p2].finalLocation < colorMap[p1].finalLocation -> {
                                val temp = colorMap[p2]
                                for (i in p2 downTo p1 + 1) {
                                    colorMap[i] = colorMap[i - 1]
                                    updateColorAtLocation(i)
                                }
                                colorMap[p1] = temp
                                updateColorAtLocation(p1)
                                p1++
                                if (p2 < endIndex) p2++
                                delayBlocking(data.delay)
                            }
                        }
                    }
                }
            }
            sort(0, colorMap.lastIndex)
        }
    }
}
