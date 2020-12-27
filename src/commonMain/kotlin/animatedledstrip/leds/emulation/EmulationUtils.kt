package animatedledstrip.leds.emulation

import animatedledstrip.leds.stripmanagement.LEDStrip
import animatedledstrip.leds.stripmanagement.StripInfo

fun createNewEmulatedStrip(stripInfo: StripInfo): LEDStrip = LEDStrip(stripInfo, EmulatedWS281x(stripInfo.numLEDs))

fun createNewEmulatedStrip(numLEDs: Int): LEDStrip = LEDStrip(StripInfo(numLEDs), EmulatedWS281x(numLEDs))
