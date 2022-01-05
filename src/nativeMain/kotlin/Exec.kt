
fun execute(command: Command, config: Config) {
    with(config) {
        when (command) {
            is Command.Init     -> init(command.issue)
            is Command.List     -> list(command.query)
            is Command.Start    -> start(command.issue)
            is Command.Review   -> review(command.issue)
            is Command.Done     -> done(command.issue)
            is Command.Close    -> close(command.issue)
            is Command.Browse   -> browse(command.issue)
            is Command.Clean    -> clean()
            is Command.Show     -> show()
            is Command.Version  -> version()
        }
    }
}

private fun Config.init(issue: Issue) {
    saveTicket("$project-${issue.id}")
}

private fun clean() {
    saveTicket("")
}

private fun show() {
    println(loadTicket())
}

private fun version() {
    println(VERSION)
}

private fun Config.start(issue: Issue?) {
    val ticket = issue?.id?.let { "$project-$it" } ?: loadTicket()
    log("assigning issue...") {
        "jira-cli assign -Q $ticket $user".exec()
    }
    log("moving in progress...") {
        "jira-cli transition --noedit -Q 'to dev' $ticket".exec()
        "jira-cli transition --noedit -Q 'start dev' $ticket".exec()
    }
}

private fun Config.review(issue: Issue?) {
    val ticket = issue?.id?.let { "$project-$it" } ?: loadTicket()
    log("moving to review...") {
        "jira-cli transition --noedit 'to done queue' $ticket".exec()
    }
}

private fun Config.done(issue: Issue?) {
    val ticket = issue?.id?.let { "$project-$it" } ?: loadTicket()
    log("moving to qa...") {
        "jira-cli transition --noedit 'review passed' $ticket".exec()
    }
}

private fun Config.close(issue: Issue?) {
    val ticket = issue?.id?.let { "$project-$it" } ?: loadTicket()
    log("moving to done...") {
        "jira-cli transition --noedit 'closed' $ticket".exec()
    }
}

private fun Config.browse(issue: Issue?) {
    val ticket = issue?.id?.let { "$project-$it" } ?: loadTicket()
    log("opening browser...") {
        "jira-cli browse $ticket".exec()
    }
}

private fun Config.list(query: Query) {
    val (me, status) = query
    val category = when(status) {
        Status.Open       -> "'To Do'"
        Status.Progress   -> "'In Progress'"
        Status.Closed     -> "Done"
        else              -> null
    }
    val jiraQuery = buildString {
        append("sprint = $sprint")
        append(" AND component in ($components)")
        if (me) append(" AND assignee = currentUser()")
        if (category != null) append(" AND statusCategory = $category")
    }
    println("Loading...")
    println("jira-cli list -q \"$jiraQuery\"".exec())
}

private fun log(message: String, block: () -> Any) {
    print(message)
    block()
    println("Ok")
}
