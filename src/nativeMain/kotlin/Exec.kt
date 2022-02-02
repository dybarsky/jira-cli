
fun execute(command: Command, config: Config) {
    with(config) {
        when (command) {
            is Command.List     -> list(command.query)
            is Command.Start    -> start(command.issue)
            is Command.Review   -> review(command.issue)
            is Command.Done     -> done(command.issue)
            is Command.Browse   -> browse(command.issue)
            is Command.Blocked  -> block(command.issue)
            is Command.Version  -> version()
        }
    }
}

private fun version() {
    println(VERSION)
}

private inline fun Config.getTicket(issue: Issue?): String =
    issue?.id
        ?.let { "$project-$it" }
        ?: branchTicket()

private inline fun prompt(ticket: String, action: String = "Updating") {
    if (ticket.isEmpty()) {
        println("Provide issue")
        return
    }
    println("$action ${ticket.yellow().dim()}:")
}

private fun Config.start(issue: Issue?) {
    val ticket = getTicket(issue)
    prompt(ticket)
    journal("assigning issue") {
        "jira-cli assign -Q $ticket $user".exec()
    }
    journal("moving in progress") {
        "jira-cli transition --noedit -Q 'in progress' $ticket".exec()
    }
}

private fun Config.review(issue: Issue?) {
    val ticket = getTicket(issue)
    prompt(ticket)
    journal("moving to review") {
        "jira-cli transition --noedit 'in review' $ticket".exec()
    }
}

private fun Config.done(issue: Issue?) {
    val ticket = getTicket(issue)
    prompt(ticket)
    journal("moving to done") {
        "jira-cli transition --noedit 'closed' $ticket".exec()
    }
}

private fun Config.browse(issue: Issue?) {
    val ticket = getTicket(issue)
    prompt(ticket, action = "Opening")
    journal("starting browser") {
        "jira-cli browse $ticket".exec()
    }
}

private fun Config.block(issue: Issue?) {
    val ticket = getTicket(issue)
    prompt(ticket)
    journal("moving blocked") {
        "jira-cli transition --noedit 'blocked' $ticket".exec()
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
    print("Loading...")
    val result = "jira-cli list -q \"$jiraQuery\"".exec()
    print("\r          \r")
    print(result.output)
}
