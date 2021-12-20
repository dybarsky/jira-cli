import kotlinx.cinterop.toKString
import okio.FileSystem
import okio.Path.Companion.toPath
import platform.posix.getenv

private val home = getenv("HOME")?.toKString()
private val file = "$home/.jira".toPath()

data class Config(
    val components: String,
    val project: String,
    val sprint: String,
    val user: String,
)

fun readConfig(): Config {
    val content = FileSystem.SYSTEM.read(file) { readUtf8() }
    val properties = content
        .split("\n")
        .filter { it.isNotEmpty() }
        .map { it.split("=") }
        .associate { it[0] to it[1] }
    return Config(
        components = properties.load("components"),
        project = properties.load("project"),
        sprint = properties.load("sprint"),
        user = properties.load("user"),
    )
}

private fun Map<String, String>.load(name: String): String =
    getOrElse(name) {
        error("Set $name property into ~/.jira file")
    }
