Jira Command Line
-----------------

Kotlin native based wrapper around official jira cli.


### Config 
Create `~/.jira` file with content:
```
project=AT
sprint=765
components=Frontent,Backend
user=maksym@email.com
```


### Usage

```
jira <command> <args>
Commands:
    list <status> - tickets list in sprint by status [open|progress|closed]
    init <number> - saves issue id to .git/ticket file
    start         - assigns ticket and moves it to `in progress`
    review        - move ticket to `in review` status
    done          - move ticket to `qa stage` status
    close         - close ticket with `done` status
    clean         - clears .git/ticket file
```
