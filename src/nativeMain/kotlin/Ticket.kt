import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

private const val file = ".git/ticket"

fun saveTicket(ticket: String) {
    val path = createPath()
    FileSystem.SYSTEM.write(path) { writeUtf8(ticket) }
}

fun loadTicket(): String {
    val path = createPath()
    return FileSystem.SYSTEM.read(path) { readUtf8() }
}

private fun createPath(): Path {
    val currentDir = runCatching { "pwd".exec() }
        .getOrNull()
        ?.replace("\n", "")
    return "$currentDir/$file".toPath()
}
