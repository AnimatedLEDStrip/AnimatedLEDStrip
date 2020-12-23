package animatedledstrip.leds

import animatedledstrip.utils.tryWithLock
import org.pmw.tinylog.Logger

actual fun LEDStrip.withPixelLock(pixel: Int, operation: () -> Any?) {
    pixelLocks[pixel]?.tryWithLock {
        operation.invoke()
    } ?: Logger.warn("Could not find Mutex for pixel $pixel")
}