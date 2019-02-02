package animatedledstrip.leds

class EmulatedAnimatedLEDStripNonConcurrent(numLEDs: Int,
                               pin: Int,
                               imageDebugging: Boolean = false): AnimatedLEDStripNonConcurrent(numLEDs, pin, imageDebugging){
    override var ledStrip: LEDStripInterface = EmulatedWS281x(pin, 255, numLEDs)
}