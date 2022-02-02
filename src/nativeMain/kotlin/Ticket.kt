
fun branchTicket(): String =
    "git rev-parse --abbrev-ref HEAD | grep -Eo '^(\\w+/)?(\\w+[-_])?[0-9]+' | grep -Eo '(\\w+[-])?[0-9]+' | tr '[:lower:]' '[:upper:]'"
        .exec()
        .replace("\n", "")
