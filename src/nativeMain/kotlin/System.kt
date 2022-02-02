import kotlinx.cinterop.refTo
import kotlinx.cinterop.toKString
import platform.posix.fgets
import platform.posix.pclose
import platform.posix.popen

data class Result(val status: Int, val output: String)

fun String.exec(): Result {
    val pointer = popen(this, "r") ?: error("Failed to run command: $this")
    val stdout = buildString {
        val buffer = ByteArray(1024)
        while (true) {
            val input = fgets(buffer.refTo(0), buffer.size, pointer) ?: break
            append(input.toKString())
        }
    }
    val status = pclose(pointer)
    return Result(status, stdout)
}

inline fun journal(message: String, operation: () -> Any) {
    print("$message...")
    val result = operation()
    if (result !is Result) {
        println("Ok".green())
        return
    }
    if (result.status == 0) {
        println("Ok".green())
    } else {
        println("Fail".red())
    }
}
