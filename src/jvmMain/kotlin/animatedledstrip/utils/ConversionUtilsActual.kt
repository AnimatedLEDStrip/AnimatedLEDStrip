package animatedledstrip.utils

actual fun ByteArray?.toUTF8(size: Int): String  {
    requireNotNull(this)
    return this.toString(Charsets.UTF_8).take(size)
}
