package eu.fuddi

import eu.fuddi.wikimedia.wikiMediaConfigOf
import org.apache.jena.query.ARQ

fun main(args: Array<String>) {
    ARQ.init()
    val parameters = args.asArgumentMap()
    val sourceFile = parameters["source-file"] ?: error("No source-file given!")
    val templateDirectory = parameters["template-directory"] ?: error("No template-directory given!")
    generateWiki(sourceFile, templateDirectory, wikiMediaConfigOf(parameters))
}

fun Array<String>.asArgumentMap() = toList().zipWithNext()
        .toMap()
        .filter { it.key.startsWith("-") }
        .mapKeys { it.key.drop(1) }