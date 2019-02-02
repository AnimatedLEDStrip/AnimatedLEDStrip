package animatedledstrip.leds

class EmulatedAnimatedLEDStrip(numLEDs: Int,
                               pin: Int,
                               emulated: Boolean = false,
                               imageDebugging: Boolean = false): AnimatedLEDStrip(numLEDs, pin, emulated, imageDebugging){
    override var ledStrip: LEDStripInterface = EmulatedWS281x(pin, 255, numLEDs)

}