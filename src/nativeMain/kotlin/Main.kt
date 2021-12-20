
fun main(args: Array<String>) {
	runCatching {
		val command = parse(args)
		val config = readConfig()
		execute(command, config)
	}.onFailure {
		println(it.message)
	}
}
