package animatedledstrip.leds.colormanagement

import animatedledstrip.utils.b
import animatedledstrip.utils.g
import animatedledstrip.utils.r
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

actual class LEDStripColorLogger actual constructor(
    val stripColorManager: LEDStripColorManager,
    val rendersBeforeSave: Int,
) {

    private val fileName = when (stripColorManager.stripManager.stripInfo.fileName) {
        null -> "signature_${SimpleDateFormat("MMDDYY_hhmmss").format(Date())}.csv"
        else -> {
            if (stripColorManager.stripManager.stripInfo.fileName.endsWith(".csv"))
                stripColorManager.stripManager.stripInfo.fileName
            else "${stripColorManager.stripManager.stripInfo.fileName}.csv"
        }
    }

    private val saveStateBuffer = StringBuilder()
    private val saveStateChannel: Channel<String> = Channel(Channel.UNLIMITED)

    init {
        GlobalScope.launch {
            var renderNum = 0

            for (saveState in saveStateChannel) {
                saveStateBuffer.appendLine(saveState)
                if (renderNum++ >= rendersBeforeSave) {
                    launch(Dispatchers.IO) {
                        FileWriter(fileName, true).append(saveStateBuffer.toString()).close()
                        saveStateBuffer.clear()
                    }.join()
                    renderNum = 0
                }
            }
        }
    }

    actual suspend fun saveStripState() {
        saveStateChannel.send(
            stripColorManager.pixelActualColorList.joinToString(separator = ",") { "${it.r},${it.g},${it.b}" }
        )
    }
}