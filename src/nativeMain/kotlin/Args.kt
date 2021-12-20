
value class Issue(val id: Int)

enum class Status {
    Open, Progress, Closed
}

sealed interface Command {
    data class Init(val issue: Issue) : Command
    data class List(val status: Status) : Command
    object Start : Command
    object Review : Command
    object Done : Command
    object Close : Command
    object Clean : Command
}

fun parse(args: Array<String>): Command {
    return when (args.firstOrNull()) {
        "list"      -> Command.List(args.second(String?::asStatus))
        "init"      -> Command.Init(Issue(args.second(String?::asIssue)))
        "start"     -> Command.Start
        "review"    -> Command.Review
        "done"      -> Command.Done
        "close"     -> Command.Close
        "clean"     -> Command.Clean
        else        -> help()
    }
}

private fun <T> Array<String>.second(mapper: (String?) -> T): T {
    return getOrNull(1).run(mapper)
}

fun String?.asIssue(): Int =
    runCatching { this?.toInt() }
        .getOrNull()
        ?: error("Can't parse issue id")

fun String?.asStatus(): Status = Status
    .values()
    .firstOrNull { this.equals(it.name, ignoreCase = true) }
    ?: error("Status not supported. Use [open|progress|closed]")

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
