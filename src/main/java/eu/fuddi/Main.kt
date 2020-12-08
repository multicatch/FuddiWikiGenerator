package eu.fuddi

import eu.fuddi.rdf.subjectsIn
import org.apache.jena.riot.RDFDataMgr.loadModel

fun main(args: Array<String>) {
    val parameters = args.asArgumentMap()
    loadModel("data.rdf").subjectsIn("")
}

fun Array<String>.asArgumentMap() = toList().zipWithNext()