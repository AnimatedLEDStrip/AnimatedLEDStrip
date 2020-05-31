package animatedledstrip.animationutils

import animatedledstrip.utils.SendableData

data class EndAnimation(
    val id: String
) : SendableData {

    companion object {
        const val prefix = "END "
    }

    override val prefix = EndAnimation.prefix

    override fun toHumanReadableString(): String = "End of animation $id"
}
