package animatedledstrip.leds.colormanagement

expect class LEDStripColorLogger(
    stripColorManager: LEDStripColorManager,
    rendersBeforeSave: Int,
) {
    suspend fun saveStripState()
}