
fun execute(command: Command, config: Config) {
    with(config) {
        when (command) {
            is Command.Init -> init(command.issue)
            is Command.List -> list(command.status)
            Command.Start   -> start()
            Command.Review  -> review()
            Command.Done    -> done()
            Command.Close   -> close()
            Command.Clean   -> clean()
        }
    }
}

private fun Config.init(issue: Issue){
    saveTicket("$project-${issue.id}")
}

private fun Config.clean() {
    saveTicket("")
}

private fun Config.start() {
    val issue = loadTicket()
    log("assigning issue...") {
        "jira-cli assign -Q $issue $user".exec()
    }
    log("moving in progress...") {
        "jira-cli transition --noedit -Q 'to dev' $issue".exec()
        "jira-cli transition --noedit -Q 'start dev' $issue".exec()
    }
}

private fun Config.review() {
    val issue = loadTicket()
    log("moving to review...") {
        "jira-cli transition --noedit 'to done queue' $issue".exec()
    }
}

private fun Config.done() {
    val issue = loadTicket()
    log("moving to qa...") {
        "jira-cli transition --noedit 'review passed' $issue".exec()
    }
}

private fun Config.close() {
    val issue = loadTicket()
    log("moving to done...") {
        "jira-cli transition --noedit 'closed' $issue".exec()
    }
}

private fun Config.list(status: Status) {
    val category = when(status) {
        Status.Open       -> "'To Do'"
        Status.Progress   -> "'In Progress'"
        Status.Closed     -> "Done"
    }
    val query = """
        sprint = $sprint 
        AND statusCategory = $category 
        AND component in ($components) 
    """.trimIndent().replace("\n", "")
    println("Loading...")
    println("jira-cli list -q \"$query\"".exec())
}

private fun log(message: String, block: () -> Any) {
    print(message)
    block()
    println("Ok")
}
