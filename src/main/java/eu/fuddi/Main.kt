package eu.fuddi

fun main(args: Array<String>) {
    val parameters = args.asArgumentMap()
    generateWiki(parameters["source-file"] ?: "data.rdf")
}

fun Array<String>.asArgumentMap() = toList().zipWithNext().toMap()