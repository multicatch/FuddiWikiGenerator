package eu.fuddi

import eu.fuddi.wikimedia.wikiMediaConfigOf
import org.apache.jena.query.ARQ

fun main(args: Array<String>) {
    ARQ.init()
    val parameters = args.asArgumentMap()
    val sourceFile = parameters["source-file"] ?: error("No source-file given!")
    val templateName = parameters["template-file"] ?: error("No template-file given!")
    generateWiki(sourceFile, templateName, wikiMediaConfigOf(parameters))
}

fun Array<String>.asArgumentMap() = toList().zipWithNext()
        .toMap()
        .filter { it.key.startsWith("-") }
        .mapKeys { it.key.drop(1) }