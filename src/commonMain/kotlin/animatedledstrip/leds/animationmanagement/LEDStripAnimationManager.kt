package animatedledstrip.leds.animationmanagement

import animatedledstrip.leds.sectionmanagement.SectionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope

class LEDStripAnimationManager(override val sectionManager: SectionManager) : AnimationManager {

    override val runningAnimations: RunningAnimationMap = RunningAnimationMap()

    override val animationScope: CoroutineScope = GlobalScope

}
