package animatedledstrip.animations

import animatedledstrip.colors.PreparedColorContainer
import animatedledstrip.colors.shuffledWithIndices

data class SortablePixel(val finalLocation: Int, val currentLocation: Int, val color: Int)

fun PreparedColorContainer.toSortableList(): MutableList<SortablePixel> =
    shuffledWithIndices()
        .mapIndexed { index, it -> SortablePixel(it.first, index, it.second) }
        .toMutableList()

fun List<SortablePixel>.toPreparedColorContainer(): PreparedColorContainer =
    PreparedColorContainer(this.map { it.color })
