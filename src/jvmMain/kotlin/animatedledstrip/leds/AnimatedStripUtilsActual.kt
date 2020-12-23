package animatedledstrip.leds

import animatedledstrip.animationutils.RunningAnimation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

actual fun RunningAnimation.joinBlocking()= runBlocking { job.join() }

actual fun <T> runBlockingNonCancellable(
    block: suspend CoroutineScope.() -> T,
): T {
    return runBlocking {
        withContext(NonCancellable) {
            block.invoke(this@runBlocking)
        }
    }
}