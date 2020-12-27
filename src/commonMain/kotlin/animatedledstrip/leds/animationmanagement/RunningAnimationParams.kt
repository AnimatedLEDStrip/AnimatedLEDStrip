package animatedledstrip.leds.animationmanagement

import animatedledstrip.animations.Animation
import animatedledstrip.animations.Direction
import animatedledstrip.colors.ColorContainerInterface
import animatedledstrip.colors.PreparedColorContainer
import animatedledstrip.utils.SendableData
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class RunningAnimationParams private constructor(
    val animationName: String,
    val colors: List<PreparedColorContainer>,
    val center: Int,
    val delay: Long,
    val direction: Direction,
    val distance: Int,
    val id: String,
    val runCount: Int,
    val section: String,
    val spacing: Int,
    val sourceParams: AnimationToRunParams,
) : SendableData {

    @Transient
    lateinit var animation: Animation

    constructor(
        animation: Animation,
        animationName: String,
        colors: List<PreparedColorContainer>,
        center: Int,
        delay: Long,
        direction: Direction,
        distance: Int,
        id: String,
        runCount: Int,
        section: String,
        spacing: Int,
        sourceParams: AnimationToRunParams,
    ) : this(animationName, colors, center, delay, direction, distance, id, runCount, section, spacing, sourceParams) {
        this.animation = animation
    }

    /**
     * A map used internally by animations to communicate between
     * runs of the animation (see Alternate or Cat Toy)
     */
    @Transient
    val extraData = mutableMapOf<String, Any?>()

    fun withModifications(
        animation: String = this.animationName,
        colors: MutableList<ColorContainerInterface> = this.colors.toMutableList(),
        center: Int = this.center,
        delay: Long = this.sourceParams.delay,
        delayMod: Double = this.sourceParams.delayMod,
        direction: Direction = this.direction,
        distance: Int = this.distance,
        id: String = this.id,
        runCount: Int = this.runCount,
        section: String = this.section,
        spacing: Int = this.spacing,
    ): AnimationToRunParams = AnimationToRunParams(animation, colors, center, delay, delayMod,
                                                   direction, distance, id, runCount, section, spacing)

    override fun toHumanReadableString(): String {
        TODO("not implemented")
    }
}
