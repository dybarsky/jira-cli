
value class Issue(val id: Int)

enum class Status {
    Open, Progress, Closed
}
data class Query(
    val me: Boolean,
    val status: Status?,
)

sealed interface Command {
    object Show : Command
    object Clean : Command
    object Version : Command
    data class List(val query: Query) : Command
    data class Init(val issue: Issue) : Command
    data class Start(val issue: Issue?) : Command
    data class Review(val issue: Issue?) : Command
    data class Done(val issue: Issue?) : Command
    data class Close(val issue: Issue?) : Command
}

fun parse(args: Array<String>): Command {
    val command = args.firstOrNull()
    val params = args.drop(1)
    val issue = params.findIssue()
    return when (command) {
        "list"      -> Command.List(params.findQuery())
        "init"      -> Command.Init(issue ?: error("Provide issue id"))
        "start"     -> Command.Start(issue)
        "review"    -> Command.Review(issue)
        "done"      -> Command.Done(issue)
        "close"     -> Command.Close(issue)
        "version"   -> Command.Version
        "clean"     -> Command.Clean
        "show"      -> Command.Show
        else        -> help()
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

private fun <T> Array<String>.second(mapper: (String?) -> T): T {
    return getOrNull(1).run(mapper)
}

fun String?.asIssue(): Int =
    runCatching { this?.toInt() }
        .getOrNull()
        ?: error("Can't parse issue id")

fun String?.asStatus(): Status? = Status
    .values()
    .firstOrNull { this.equals(it.name, ignoreCase = true) }

private fun help(): Nothing {
    println("""
        Usage: jira <command> <args>
        Commands:
            list <status> - tickets list in sprint by status [open|progress|closed]
            init <number> - saves issue id to .git/ticket file
            start         - assigns ticket and moves it to `in progress` 
            review        - move ticket to `in review` status
            done          - move ticket to `qa stage` status
            close         - close ticket with `done` status
            clean         - clears .git/ticket file
    """.trimIndent())
    error("")
}
