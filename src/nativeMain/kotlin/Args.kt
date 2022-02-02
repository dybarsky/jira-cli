
private val help = """
    Usage: jira <command> <args>
    Commands:
        list <status> - tickets list in sprint by status [open|progress|closed]
        start         - assigns ticket and moves it to `in progress` 
        review        - move ticket to `in review` status
        done          - move ticket to `closed` status
        blocked       - move ticket to `blocked` status
        browse        - open jira ticket in the browser
        version       - prints version of the app
""".trimIndent()

value class Issue(val id: Int)

enum class Status {
    Open, Progress, Closed
}
data class Query(
    val me: Boolean,
    val status: Status?,
)

sealed interface Command {
    object Version : Command
    data class List(val query: Query) : Command
    data class Start(val issue: Issue?) : Command
    data class Review(val issue: Issue?) : Command
    data class Done(val issue: Issue?) : Command
    data class Browse(val issue: Issue?) : Command
    data class Blocked(val issue: Issue?) : Command
}

fun parse(args: Array<String>): Command {
    val command = args.firstOrNull()
    val params = args.drop(1)
    val issue = params.findIssue()
    return when (command) {
        "list"      -> Command.List(params.findQuery())
        "start"     -> Command.Start(issue)
        "review"    -> Command.Review(issue)
        "done"      -> Command.Done(issue)
        "blocked"   -> Command.Blocked(issue)
        "browse"    -> Command.Browse(issue)
        "version"   -> Command.Version
        else        -> error("Unknown command ${command?.dim() ?: ""}\n$help")
    }
}

private fun List<String>.findQuery(): Query {
    val me = any { it.equals("me", ignoreCase = true) }
    val status = mapNotNull { it.asStatus() }.firstOrNull()
    if (status == null && me.not()) {
        error("Query can't be empty. Use [me] and optional status [open|progress|closed]")
    }
    return Query(me, status)
}

private fun List<String>.findIssue(): Issue? {
    return mapNotNull {
        runCatching { Issue(it.toInt()) }.getOrNull()
    }.firstOrNull()
}

private fun String?.asStatus(): Status? = Status
    .values()
    .firstOrNull { this.equals(it.name, ignoreCase = true) }
