package animatedledstrip.animationutils

/**
 * Helper enum for specifying the requirement level of an animation parameter.
 *
 */
enum class ReqLevel {
    /**
     * Animation parameter must be set by the user
     */
    REQUIRED,
    /**
     * Animation parameter may be set by the user, otherwise will be set to a
     * default as specified (`color#` will default to [animatedledstrip.colors.ccpresets.CCBlack])
     */
    OPTIONAL,
    /**
     * Animation does not use parameter
     */
    NOTUSED
}