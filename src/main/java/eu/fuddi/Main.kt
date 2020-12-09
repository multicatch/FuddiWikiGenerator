package eu.fuddi

import eu.fuddi.wikimedia.wikiMediaConfigOf

fun main(args: Array<String>) {
    val parameters = args.asArgumentMap()
    val sourceFile = parameters["source-file"] ?: error("No source-file given!")
    val templateName = parameters["template-name"] ?: error("No template-name given!")
    generateWiki(sourceFile, templateName, wikiMediaConfigOf(parameters))
}

fun Array<String>.asArgumentMap() = toList().zipWithNext().toMap()