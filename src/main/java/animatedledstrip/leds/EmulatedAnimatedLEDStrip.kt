package animatedledstrip.leds

class EmulatedAnimatedLEDStrip(numLEDs: Int,
                               pin: Int,
                               imageDebugging: Boolean = false): AnimatedLEDStrip(numLEDs, pin, imageDebugging){
    override var ledStrip: LEDStripInterface = EmulatedWS281x(pin, 255, numLEDs)

}