import kotlinx.cinterop.refTo
import kotlinx.cinterop.toKString
import platform.posix.fgets
import platform.posix.pclose
import platform.posix.popen

fun String.exec(): String {
    val pointer = popen(this, "r") ?: error("Failed to run command: $this")

    val stdout = buildString {
        val buffer = ByteArray(4096)
        while (true) {
            val input = fgets(buffer.refTo(0), buffer.size, pointer) ?: break
            append(input.toKString())
        }
    }

    val status = pclose(pointer)
    if (status != 0) error("Command `$this` failed with status $status")

    return stdout
}
